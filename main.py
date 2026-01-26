import os
import sqlite3
import json
from typing import List, Optional
from fastapi import FastAPI, Request, Form
from fastapi.responses import HTMLResponse, RedirectResponse, JSONResponse
from fastapi.templating import Jinja2Templates
from fastapi.staticfiles import StaticFiles
from starlette.middleware.sessions import SessionMiddleware
from pydantic import BaseModel

app = FastAPI()

# --- 1. CONFIGURACIÓN DE RUTAS ABSOLUTAS ---
# Obtenemos la ruta de la carpeta donde está este archivo (Raíz del proyecto)
BASE_DIR = os.path.dirname(os.path.abspath(__file__))

DB_PATH = os.path.join(BASE_DIR, "minerva.db")
STATIC_DIR = os.path.join(BASE_DIR, "static")
TEMPLATES_DIR = os.path.join(BASE_DIR, "templates")

print(f"--- CONECTANDO A BBDD EN: {DB_PATH} ---")

# --- 2. MIDDLEWARE Y ARCHIVOS ESTÁTICOS ---
app.add_middleware(SessionMiddleware, secret_key="latararallevaunvestidoblancollenodecascabeles")
app.mount("/static", StaticFiles(directory=STATIC_DIR), name="static")
templates = Jinja2Templates(directory=TEMPLATES_DIR)


# --- 3. MODELOS DE DATOS (Pydantic) ---
class StudentData(BaseModel):
    id: str
    name: str
    attendance: str  # Viene como string del CSV, lo convertiremos
    grade: str  # Viene como string del CSV
    participation: str  # Equivale a practical_grade
    subject: str
    ra_vector: List[float]
    feedback: Optional[str] = ""
    risk: Optional[str] = None


# --- 4. INICIALIZACIÓN DE BBDD (MODELO RELACIONAL) ---
def init_db():
    conn = sqlite3.connect(DB_PATH)
    c = conn.cursor()

    # Tabla: USUARIOS (RF-06, RNF-03)
    c.execute('''
              CREATE TABLE IF NOT EXISTS users
              (
                  id
                  INTEGER
                  PRIMARY
                  KEY
                  AUTOINCREMENT,
                  username
                  TEXT
                  UNIQUE
                  NOT
                  NULL,
                  password_hash
                  TEXT
                  NOT
                  NULL,
                  role
                  TEXT
                  DEFAULT
                  'profesor'
              )
              ''')

    # Tabla: ALUMNOS (CU-01) - Identidad única
    c.execute('''
              CREATE TABLE IF NOT EXISTS students
              (
                  id
                  TEXT
                  PRIMARY
                  KEY,
                  full_name
                  TEXT
                  NOT
                  NULL,
                  created_at
                  TIMESTAMP
                  DEFAULT
                  CURRENT_TIMESTAMP
              )
              ''')

    # Tabla: DATOS ACADÉMICOS (RF-01) - Features para ML
    c.execute('''
              CREATE TABLE IF NOT EXISTS academic_data
              (
                  id
                  INTEGER
                  PRIMARY
                  KEY
                  AUTOINCREMENT,
                  student_id
                  TEXT
                  NOT
                  NULL,
                  subject
                  TEXT
                  NOT
                  NULL,
                  average_grade
                  REAL,
                  attendance_faults
                  INTEGER,
                  practical_grade
                  TEXT,
                  ra_vector
                  TEXT,
                  teacher_feedback_text
                  TEXT,
                  FOREIGN
                  KEY
              (
                  student_id
              ) REFERENCES students
              (
                  id
              )
                  )
              ''')

    # Tabla: PREDICCIONES (RF-03, CU-02) - Histórico
    c.execute('''
              CREATE TABLE IF NOT EXISTS predictions
              (
                  id
                  INTEGER
                  PRIMARY
                  KEY
                  AUTOINCREMENT,
                  student_id
                  TEXT
                  NOT
                  NULL,
                  prediction_date
                  TIMESTAMP
                  DEFAULT
                  CURRENT_TIMESTAMP,
                  pass_probability
                  REAL,
                  predicted_result
                  TEXT,
                  model_version
                  TEXT,
                  FOREIGN
                  KEY
              (
                  student_id
              ) REFERENCES students
              (
                  id
              )
                  )
              ''')

    # Usuario Admin por defecto (si no existe)
    try:
        c.execute("INSERT INTO users (username, password_hash, role) VALUES ('admin', 'admin', 'administrador')")
    except sqlite3.IntegrityError:
        pass

    conn.commit()
    conn.close()


# Ejecutamos la creación de tablas al arrancar
init_db()


# --- 5. RUTAS DE VISTAS (HTML) ---

@app.get("/", response_class=HTMLResponse)
def read_root(request: Request):
    return templates.TemplateResponse("index.html", {"request": request})


@app.get("/login", response_class=HTMLResponse)
def login_page(request: Request):
    if request.session.get("user"):
        return RedirectResponse(url="/demo")
    return templates.TemplateResponse("login.html", {"request": request})


@app.post("/login")
async def login_submit(request: Request, username: str = Form(...), password: str = Form(...)):
    # Lógica simple temporal. En producción, consultaríamos la tabla 'users' y verificaríamos hash.
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


# --- 6. API ENDPOINTS (LÓGICA DE NEGOCIO) ---

@app.post("/upload_data")
async def upload_data(students_data: List[StudentData]):
    """
    Recibe los datos del frontend (CSV parseado) y los distribuye
    en las tablas relacionales: students, academic_data y predictions.
    """
    conn = sqlite3.connect(DB_PATH)
    c = conn.cursor()

    try:
        count = 0
        for s in students_data:
            # A. Insertar/Actualizar identidad del ALUMNO
            c.execute('''
                INSERT OR REPLACE INTO students (id, full_name)
                VALUES (?, ?)
            ''', (s.id, s.name))

            # B. Insertar DATOS ACADÉMICOS
            # 1. Borramos datos previos de esa asignatura para evitar duplicados sucios
            c.execute('DELETE FROM academic_data WHERE student_id = ? AND subject = ?', (s.id, s.subject))

            # 2. Insertamos los nuevos datos
            ra_json = json.dumps(s.ra_vector)

            # Conversión segura de tipos
            try:
                grade_val = float(s.grade)
            except ValueError:
                grade_val = 0.0

            try:
                attendance_val = int(s.attendance)
            except ValueError:
                attendance_val = 0

            c.execute('''
                      INSERT INTO academic_data
                      (student_id, subject, average_grade, attendance_faults, practical_grade, ra_vector,
                       teacher_feedback_text)
                      VALUES (?, ?, ?, ?, ?, ?, ?)
                      ''', (
                          s.id,
                          s.subject,
                          grade_val,
                          attendance_val,
                          s.participation,
                          ra_json,
                          s.feedback
                      ))

            # C. Insertar PREDICCIÓN (si el frontend ya calculó algo o trajo riesgo)
            if s.risk:
                # Simulamos probabilidad basada en el riesgo para llenar el campo
                prob = 0.8 if s.risk == "Bajo" else (0.5 if s.risk == "Medio" else 0.2)

                c.execute('''
                          INSERT INTO predictions (student_id, pass_probability, predicted_result, model_version)
                          VALUES (?, ?, ?, ?)
                          ''', (s.id, prob, s.risk, "v0.1-demo"))

            count += 1

        conn.commit()
        return JSONResponse(content={"status": "success", "inserted_count": count})

    except Exception as e:
        conn.rollback()
        print(f"Error insertando datos: {e}")
        return JSONResponse(content={"status": "error", "detail": str(e)}, status_code=500)
    finally:
        conn.close()
