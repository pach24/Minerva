<div align="center">

# Minerva

### *Plataforma de Inteligencia Educativa con Machine Learning*

Predice el riesgo académico, analiza el rendimiento de los estudiantes y apoya a los docentes con analítica inteligente.

<br>

![Python](https://img.shields.io/badge/Python-3.12-3776AB?style=for-the-badge&logo=python&logoColor=white)
![FastAPI](https://img.shields.io/badge/FastAPI-0.124-009688?style=for-the-badge&logo=fastapi&logoColor=white)
![Scikit-Learn](https://img.shields.io/badge/Scikit--Learn-1.8-F7931E?style=for-the-badge&logo=scikitlearn&logoColor=white)
![Supabase](https://img.shields.io/badge/Supabase-Auth-3ECF8E?style=for-the-badge&logo=supabase&logoColor=white)

<br>

![Status](https://img.shields.io/badge/Estado-Funcional-success?style=flat-square)
![License](https://img.shields.io/badge/Licencia-Académica-blue?style=flat-square)
![ML](https://img.shields.io/badge/Modelo-SGDClassifier-orange?style=flat-square)
![Accuracy](https://img.shields.io/badge/Accuracy-~82%25-brightgreen?style=flat-square)

</div>

---

## Descripción general

**Minerva** es una plataforma web de analítica educativa que predice el riesgo de suspenso de los estudiantes usando Machine Learning. Está pensada para profesores que quieren identificar alumnos en riesgo académico antes de que el problema sea irreversible.

El sistema combina un modelo de clasificación entrenado con datos sintéticos realistas, un pipeline de NLP ligero para interpretar el feedback del docente, y un dashboard interactivo con carga de datos vía CSV.

---

## Características

**Predicción de riesgo académico**
- Clasifica a cada estudiante en cuatro niveles: Bajo, Medio, Alto y Crítico
- Calcula la probabilidad de aprobado como porcentaje
- Procesa hasta un grupo completo de alumnos en una sola petición

**Machine Learning**
- Modelo SGDClassifier entrenado con 1 000 estudiantes sintéticos
- 9 features que incluyen asistencia, notas, resultados de aprendizaje (RA) y participación
- Reentrenamiento completo y actualización incremental (partial fit) disponibles vía API
- NLP ligero: convierte el feedback textual del profesor en un score numérico (0–1)

**Dashboard web**
- Carga de datos mediante archivo CSV
- Tabla de resultados con colores por nivel de riesgo
- Panel lateral (drawer) con el detalle completo de cada estudiante
- Visualización de barras de progreso por resultado de aprendizaje

**Autenticación**
- Login y gestión de sesiones con Supabase Auth
- Rutas protegidas con `SessionMiddleware`

---

## Stack tecnológico

| Capa | Tecnología |
|---|---|
| Servidor web | FastAPI 0.124 + Uvicorn |
| Plantillas | Jinja2 |
| Sesiones | Starlette `SessionMiddleware` |
| Autenticación | Supabase Auth (SDK 2.30) |
| Machine Learning | scikit-learn 1.8 (SGDClassifier, StandardScaler) |
| Datos | pandas 3.0 + NumPy 2.4 |
| Persistencia del modelo | joblib (`.pkl`) |
| Frontend | HTML5, CSS3 (15 módulos), Vanilla JS |

---

## Estructura del proyecto

```
Minerva/
├── main.py                  # Punto de entrada: rutas FastAPI, auth, sesiones
├── .env                     # Variables de entorno (no se sube al repo)
│
├── ml/
│   ├── train.py             # Generación de datos sintéticos y entrenamiento
│   ├── predict.py           # Predicción por lotes y actualización incremental
│   └── feedback.py          # NLP: texto del profesor → score 0–1
│
├── models/
│   └── minerva_model.pkl    # Modelo serializado (scaler + clf + lista de features)
│
├── templates/
│   ├── index.html           # Landing page
│   ├── login.html           # Formulario de acceso
│   └── demo.html            # Dashboard principal (protegido)
│
└── static/
    ├── css/                 # 15 hojas de estilo modulares
    ├── scripts/             # script.js (landing) y profesor.js (dashboard)
    └── media/               # Imágenes y fondos
```

---

## Instalación

### Requisitos previos

- Python 3.12
- Una cuenta en [Supabase](https://supabase.com) con un proyecto creado

### Pasos

```bash
# 1. Clonar el repositorio
git clone https://github.com/pach24/Minerva.git
cd Minerva

# 2. Crear y activar el entorno virtual
python -m venv .venv

# Windows
.venv\Scripts\activate

# Linux / macOS
source .venv/bin/activate

# 3. Instalar dependencias
pip install fastapi uvicorn jinja2 python-dotenv python-multipart \
            supabase PyJWT scikit-learn pandas numpy joblib
```

### Variables de entorno

Crea un archivo `.env` en la raíz con el siguiente contenido:

```env
SUPABASE_URL=https://<tu-proyecto>.supabase.co
SUPABASE_KEY=<tu-anon-key>
SUPABASE_SECRET_KEY=<tu-service-role-key>
SESSION_SECRET=<cadena-aleatoria-larga>

MODEL_PATH=models/minerva_model.pkl
MIN_ACCURACY=0.80
TEST_SIZE=0.2
RANDOM_STATE=42
NUM_SYNTHETIC_STUDENTS=1000
```

### Entrenar el modelo inicial

El repositorio no incluye el archivo `.pkl` (está en `.gitignore`). Antes de arrancar el servidor por primera vez, genera el modelo:

```bash
python ml/train.py
```

Esto crea `models/minerva_model.pkl` con una accuracy aproximada del 82 %.

---

## Ejecución

```bash
uvicorn main:app --reload
```

La aplicación queda disponible en `http://127.0.0.1:8000`.

---

## Flujo de uso

```
1. Acceder a /         → Landing page con descripción de la plataforma
2. Clic en "Acceder"   → Formulario de login (/login)
3. Autenticarse        → Supabase valida credenciales y crea sesión
4. Dashboard (/demo)   → Subir CSV con datos del grupo
5. "Generar predicción"→ POST /api/predecir → tabla con niveles de riesgo
6. Clic en alumno      → Drawer lateral con desglose completo
```

---

## Endpoints de la API

| Método | Ruta | Descripción |
|---|---|---|
| `GET` | `/` | Landing page |
| `GET` | `/login` | Formulario de login |
| `POST` | `/login` | Autenticación vía Supabase |
| `GET` | `/logout` | Cierra sesión y redirige a `/login` |
| `GET` | `/demo` | Dashboard (requiere sesión activa) |
| `POST` | `/api/predecir` | Predicción de riesgo para un grupo de estudiantes |
| `POST` | `/api/entrenar` | Reentrenamiento completo del modelo |
| `POST` | `/api/actualizar` | Actualización incremental del modelo (`partial_fit`) |

### POST `/api/predecir`

Recibe una lista de estudiantes en JSON y devuelve las predicciones.

**Cuerpo de la petición:**
```json
[
  {
    "nombre": "Ana García",
    "asistencia": 78,
    "nota_media": 6.4,
    "participacion": "Media",
    "nota_practicas": 5.8,
    "ra_herencia": 7,
    "ra_polimorfismo": 5,
    "ra_ficheros": 6,
    "ra_interfaces": 4,
    "feedback": "Participa pero se distrae con facilidad"
  }
]
```

**Respuesta:**
```json
[
  {
    "nombre": "Ana García",
    "asistencia": 78,
    "nota_media": 6.4,
    "prob_aprobado": 0.61,
    "nivel_riesgo": "Medio"
  }
]
```

Los niveles de riesgo se asignan según la probabilidad de aprobado:

| Nivel | Probabilidad |
|---|---|
| Bajo | ≥ 70 % |
| Medio | 50 – 69 % |
| Alto | 30 – 49 % |
| Crítico | < 30 % |

---

## Formato del CSV

El CSV que se sube en el dashboard debe tener exactamente estas columnas (con cabecera):

```
ID,Nombre,Asistencia,Nota_Media,Participacion,Nota_Practicas,RA_Herencia,RA_Polimorfismo,RA_Ficheros,RA_Interfaces,Feedback
```

| Columna | Tipo | Rango / Valores |
|---|---|---|
| `ID` | Entero | Identificador del alumno |
| `Nombre` | Texto | Nombre completo |
| `Asistencia` | Número | 0 – 100 (%) |
| `Nota_Media` | Número | 0 – 10 |
| `Participacion` | Texto | `Alta`, `Media`, `Baja`, `Nula` |
| `Nota_Practicas` | Número | 0 – 10 |
| `RA_Herencia` | Número | 0 – 10 |
| `RA_Polimorfismo` | Número | 0 – 10 |
| `RA_Ficheros` | Número | 0 – 10 |
| `RA_Interfaces` | Número | 0 – 10 |
| `Feedback` | Texto | Observación libre del profesor (opcional) |

---

## Modelo de Machine Learning

### Features del modelo

| Feature | Rango | Peso en síntesis |
|---|---|---|
| `asistencia` | 0 – 100 | 25 % |
| `nota_media` | 0 – 10 | 30 % |
| `participacion_score` | 0 – 1 | 8 % |
| `nota_practicas` | 0 – 10 | 12 % |
| `ra_herencia` | 0 – 10 | 3.75 % |
| `ra_polimorfismo` | 0 – 10 | 3.75 % |
| `ra_ficheros` | 0 – 10 | 3.75 % |
| `ra_interfaces` | 0 – 10 | 3.75 % |
| `feedback_score` | 0 – 1 | 10 % |

### Pipeline

```
Generación de datos sintéticos (1 000 alumnos)
           ↓
   Train / Test split (80 / 20)
           ↓
   StandardScaler (normalización)
           ↓
 SGDClassifier(loss="log_loss", max_iter=1000)
           ↓
   Evaluación completa (accuracy, classification report,
   matriz de confusión, validación de umbrales)
           ↓
   Serialización en .pkl (scaler + clf + features)
           ↓
   predict_proba() → nivel de riesgo
```

### Evaluación del modelo

Al entrenar (`python ml/train.py`), el script ejecuta una evaluación completa antes de guardar el modelo. La salida incluye:

1. **Accuracy** en train y test, con la diferencia entre ambos para detectar overfitting
2. **Classification report** (precision, recall, F1-score por clase)
3. **Matriz de confusión** en formato texto
4. **Validación de umbrales mínimos** con veredicto PASS/FAIL por cada check:

| Métrica | Umbral mínimo |
|---|---|
| Accuracy (test) | ≥ 0.80 |
| Recall (mínimo por clase) | ≥ 0.75 |
| F1-score (mínimo por clase) | ≥ 0.75 |
| Diferencia train-test | < 0.05 |

Resultados actuales (seed 42, 1 000 alumnos sintéticos):

```
Accuracy:  train 0.855 / test 0.820
Recall:    Suspende 0.84 — Aprueba 0.81
F1-score:  Suspende 0.78 — Aprueba 0.85
Overfit:   0.035 (dentro del margen)
Veredicto: MODELO APTO
```

### NLP para feedback del profesor

El módulo `ml/feedback.py` convierte el texto libre del docente en un score numérico:

- Sin texto → `0.5` (neutro)
- Detecta palabras positivas (esfuerza, participa, excelente…) y negativas (distraído, falta, abandona…)
- Score = `positivas / (positivas + negativas)`
- Sin palabras reconocidas → `0.5`

---

## Pendiente / Roadmap

- [ ] Diagramas UML y wireframes del sistema
- [ ] Suite de tests (pytest)
- [ ] Generación de `requirements.txt`
- [ ] Gráficos de evolución temporal por alumno
- [ ] Ajuste de pesos del modelo por parte del profesor (CU-5)
- [ ] Exportación de informes en PDF
- [ ] Dockerización de la aplicación

---

## Licencia

Proyecto desarrollado con fines académicos.

---

<div align="center">

**Minerva** — *Predicting educational outcomes through intelligent systems.*

</div>
