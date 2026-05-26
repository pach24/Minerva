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

supabase = create_client(SUPABASE_URL, SUPABASE_KEY)

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
        request.session["user"] = res.user.email
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


# --- API: PREDICCIÓN ML ---
@app.post("/api/predecir")
async def api_predecir(request: Request):
    user = request.session.get("user")
    if not user:
        return JSONResponse({"error": "No autorizado"}, status_code=401)
    data = await request.json()
    results = predict_students(data)
    return JSONResponse(results)


# --- API: REENTRENAR MODELO (CU-2) ---
@app.post("/api/entrenar")
async def api_entrenar(request: Request):
    user = request.session.get("user")
    if not user:
        return JSONResponse({"error": "No autorizado"}, status_code=401)
    from ml.train import train
    import ml.predict as mp
    acc = train()
    mp._model = None  # invalida caché
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


# --- RUTA PROTEGIDA (DEMO) ---
@app.get("/demo", response_class=HTMLResponse)
def demo(request: Request):
    user = request.session.get("user")
    if not user:
        return RedirectResponse(url="/login")

    return templates.TemplateResponse("demo.html", {"request": request, "user": user})
