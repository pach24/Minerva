from fastapi import FastAPI, Request, Form
from fastapi.responses import HTMLResponse, RedirectResponse
from fastapi.templating import Jinja2Templates
from fastapi.staticfiles import StaticFiles
from starlette.middleware.sessions import SessionMiddleware

app = FastAPI()

# --- CONFIGURACIÓN ---

app.add_middleware(SessionMiddleware, secret_key="latararallevaunvestidoblancollenodecascabeles")

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
    # Si ya está logueado, lo mandamos directo a la demo
    if request.session.get("user") == "admin":
        return RedirectResponse(url="/demo")
    return templates.TemplateResponse("login.html", {"request": request})

# --- LOGIN (POST) ---
@app.post("/login")
async def login_submit(request: Request, username: str = Form(...), password: str = Form(...)):
    if username == "admin" and password == "admin":
        request.session["user"] = "admin"  # Guardamos sesión
        return RedirectResponse(url="/demo", status_code=303)
    else:
        return templates.TemplateResponse("login.html", {
            "request": request,
            "error": "Usuario o contraseña incorrectos"
        })

# --- LOGOUT ---
@app.get("/logout")
def logout(request: Request):
    request.session.clear()
    return RedirectResponse(url="/login")


# --- RUTA PROTEGIDA (DEMO) ---
@app.get("/demo", response_class=HTMLResponse)
def demo(request: Request):
    # Verificamos si existe la sesión
    user = request.session.get("user")
    if not user:
        return RedirectResponse(url="/login")

    return templates.TemplateResponse("demo.html", {"request": request, "user": user})