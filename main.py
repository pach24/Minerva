from dotenv import load_dotenv
import os

load_dotenv()

SUPABASE_URL = os.environ["SUPABASE_URL"]
SUPABASE_KEY = os.environ["SUPABASE_KEY"]
SESSION_SECRET = os.environ["SESSION_SECRET"]

from fastapi import FastAPI, Request, Form
from fastapi.responses import HTMLResponse, RedirectResponse, JSONResponse
from fastapi.templating import Jinja2Templates
from fastapi.staticfiles import StaticFiles
from starlette.middleware.sessions import SessionMiddleware
from supabase import create_client
from ml.predict import predict_students
import jwt
from jwt import PyJWKClient
import logging

logger = logging.getLogger("minerva")

supabase = create_client(SUPABASE_URL, SUPABASE_KEY)

# Verificación local de JWT con las claves públicas asimétricas (ES256) de Supabase.
# El JWKS es público: no depende de SUPABASE_KEY ni hace una llamada por petición.
JWKS_URL = SUPABASE_URL.rstrip("/") + "/auth/v1/.well-known/jwks.json"
_jwk_client = PyJWKClient(JWKS_URL)

app = FastAPI()

# --- CONFIGURACIÓN ---

app.add_middleware(SessionMiddleware, secret_key=SESSION_SECRET)

# Servir archivos estáticos desde /static
app.mount("/static", StaticFiles(directory="static"), name="static")

# Templates HTML
templates = Jinja2Templates(directory="templates")

@app.get("/", response_class=HTMLResponse)
def read_root(request: Request):
    return templates.TemplateResponse("index.html", {"request": request})

@app.get("/presentacion", response_class=HTMLResponse)
def presentacion(request: Request):
    return templates.TemplateResponse("presentacion.html", {"request": request})

# --- LOGIN (GET) ---
@app.get("/login", response_class=HTMLResponse)
def login_page(request: Request):
    if request.session.get("user"):
        return RedirectResponse(url="/demo")
    return templates.TemplateResponse("login.html", {"request": request})

# --- LOGIN (POST) ---
@app.post("/login")
async def login_submit(request: Request, email: str = Form(...), password: str = Form(...)):
    try:
        res = supabase.auth.sign_in_with_password({"email": email, "password": password})
        meta = res.user.user_metadata or {}
        display_name = (
            meta.get("display_name")
            or meta.get("full_name")
            or meta.get("name")
            or res.user.email.split("@")[0]
        )
        request.session["user"] = display_name
        return RedirectResponse(url="/demo", status_code=303)
    except Exception as e:
        return templates.TemplateResponse("login.html", {
            "request": request,
            "error": str(e)
        })

# --- LOGOUT ---
@app.get("/logout")
def logout(request: Request):
    request.session.clear()
    return RedirectResponse(url="/login")


def get_current_user(request: Request):
    user = request.session.get("user")
    if user:
        return user
    auth = request.headers.get("Authorization", "")
    if auth.startswith("Bearer "):
        token = auth.split(" ", 1)[1]
        return _verify_bearer_token(token)
    return None


def _verify_bearer_token(token: str):
    """Verifica localmente el JWT de Supabase (ES256) con la clave pública del JWKS.
    Devuelve el email del usuario si el token es válido, o None en caso contrario."""
    try:
        signing_key = _jwk_client.get_signing_key_from_jwt(token)
        claims = jwt.decode(
            token,
            signing_key.key,
            algorithms=["ES256"],
            audience="authenticated",
            issuer=SUPABASE_URL.rstrip("/") + "/auth/v1",
        )
        return claims.get("email")
    except Exception:
        return None


# --- API: PREDICCIÓN ML ---
@app.post("/api/predecir")
async def api_predecir(request: Request):
    user = get_current_user(request)
    if not user:
        return JSONResponse({"error": "No autorizado"}, status_code=401)
    data = await request.json()
    results = predict_students(data)

    try:
        rows = [{
            "alumno_id": str(r.get("id", "")),
            "alumno_nombre": r.get("nombre", ""),
            "asistencia": r.get("asistencia"),
            "nota_ra1": r.get("nota_ra1"),
            "nota_ra2": r.get("nota_ra2"),
            "nota_ra3": r.get("nota_ra3"),
            "nota_ra4": r.get("nota_ra4"),
            "nota_ra5": r.get("nota_ra5"),
            "nota_ra6": r.get("nota_ra6"),
            "nota_ra7": r.get("nota_ra7"),
            "nota_ra8": r.get("nota_ra8"),
            "nota_ra9": r.get("nota_ra9"),
            "nota_media": r.get("nota_media"),
            "pct_ras_superados": r.get("pct_ras_superados"),
            "prob_aprobado": r.get("prob_aprobado"),
            "nivel_riesgo": r.get("nivel_riesgo"),
            "feedback_profesor": r.get("feedback_profesor", ""),
        } for r in results]
        supabase.table("predicciones").insert(rows).execute()
    except Exception:
        # La predicción sigue siendo válida aunque falle el histórico: se
        # devuelve igualmente, pero el fallo queda registrado para diagnóstico.
        logger.exception("Fallo al persistir predicciones en Supabase (tabla 'predicciones')")

    return JSONResponse(results)


# --- API: REENTRENAR MODELO (CU-2) ---
@app.post("/api/entrenar")
async def api_entrenar(request: Request):
    user = request.session.get("user")
    if not user:
        return JSONResponse({"error": "No autorizado"}, status_code=401)
    from ml.train import train
    acc = train()
    # ml.predict recarga el modelo automáticamente al detectar el nuevo mtime del .pkl.
    return JSONResponse({"accuracy": round(acc, 3), "ok": True})


# --- API: ACTUALIZAR INCREMENTAL (partial_fit) ---
@app.post("/api/actualizar")
async def api_actualizar(request: Request):
    user = request.session.get("user")
    if not user:
        return JSONResponse({"error": "No autorizado"}, status_code=401)
    from ml.predict import partial_update
    data = await request.json()
    partial_update(data)
    return JSONResponse({"ok": True})


# --- API: MÉTRICAS DEL MODELO ---
@app.get("/modelo/metricas")
async def api_metricas():
    import json
    metrics_path = os.getenv("METRICS_PATH", "model_metrics.json")
    try:
        with open(metrics_path, "r", encoding="utf-8") as f:
            return JSONResponse(json.load(f))
    except FileNotFoundError:
        return JSONResponse({"error": "model_metrics.json no encontrado. Ejecuta /api/entrenar primero."}, status_code=404)


# --- API: HISTORIAL POR ALUMNO ---
@app.get("/api/historial/{alumno_id}")
async def api_historial(alumno_id: str, request: Request):
    if not get_current_user(request):
        return JSONResponse({"error": "No autorizado"}, status_code=401)
    res = (supabase.table("predicciones")
        .select("*")
        .eq("alumno_id", alumno_id)
        .order("fecha")
        .execute())
    return JSONResponse(res.data)


# --- RUTA PROTEGIDA (DEMO) ---
@app.get("/demo", response_class=HTMLResponse)
def demo(request: Request):
    user = request.session.get("user")
    if not user:
        return RedirectResponse(url="/login")

    return templates.TemplateResponse("demo.html", {"request": request, "user": user})
