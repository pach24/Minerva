from fastapi import FastAPI, Request, Form
from fastapi.responses import HTMLResponse, RedirectResponse
from fastapi.templating import Jinja2Templates
from fastapi.staticfiles import StaticFiles
from starlette.middleware.sessions import SessionMiddleware

# Importar routers
from app.routers import predictions, admin

# ❌ COMENTADO TEMPORALMENTE (hasta arreglar contraseña):
# from app.config.database import Base, engine
# Base.metadata.create_all(bind=engine)

app = FastAPI(title="Minerva - Sistema Predictivo Académico")

app.add_middleware(SessionMiddleware, secret_key="latararallevaunvestidoblancollenodecascabeles")
app.mount("/static", StaticFiles(directory="static"), name="static")
templates = Jinja2Templates(directory="templates")

# Incluir routers
app.include_router(predictions.router)
app.include_router(admin.router)

@app.get("/", response_class=HTMLResponse)
def read_root(request: Request):
    return templates.TemplateResponse("index.html", {"request": request})

@app.get("/login", response_class=HTMLResponse)
def login_page(request: Request):
    if request.session.get("user") == "admin":
        return RedirectResponse(url="/demo")
    return templates.TemplateResponse("login.html", {"request": request})

@app.post("/login")
async def login_submit(request: Request, username: str = Form(...), password: str = Form(...)):
    if username == "admin" and password == "admin":
        request.session["user"] = "admin"
        return RedirectResponse(url="/demo", status_code=303)
    else:
        return templates.TemplateResponse("login.html", {
            "request": request,
            "error": "Usuario o contraseña incorrectos"
        })

@app.get("/logout")
def logout(request: Request):
    request.session.clear()
    return RedirectResponse(url="/login")

@app.get("/demo", response_class=HTMLResponse)
def demo(request: Request):
    user = request.session.get("user")
    if not user:
        return RedirectResponse(url="/login")
    return templates.TemplateResponse("demo.html", {"request": request, "user": user})
