<div align="center">

# Minerva

### *Plataforma de Inteligencia Educativa con Machine Learning*

Predice el riesgo académico, analiza el rendimiento de los estudiantes y apoya a los docentes con analítica inteligente — disponible como aplicación web y app nativa Android.

<br>

![Python](https://img.shields.io/badge/Python-3.12-3776AB?style=for-the-badge&logo=python&logoColor=white)
![FastAPI](https://img.shields.io/badge/FastAPI-0.124-009688?style=for-the-badge&logo=fastapi&logoColor=white)
![Scikit-Learn](https://img.shields.io/badge/Scikit--Learn-1.8-F7931E?style=for-the-badge&logo=scikitlearn&logoColor=white)
![Supabase](https://img.shields.io/badge/Supabase-Auth-3ECF8E?style=for-the-badge&logo=supabase&logoColor=white)
![Android](https://img.shields.io/badge/Android-API_26+-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![Kotlin](https://img.shields.io/badge/Kotlin-2.x-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)

<br>

![Status](https://img.shields.io/badge/Estado-Funcional-success?style=flat-square)
![Version](https://img.shields.io/badge/Versión-0.1.0--alpha-blue?style=flat-square)
![License](https://img.shields.io/badge/Licencia-Académica-blue?style=flat-square)
![ML](https://img.shields.io/badge/Modelo-SGDClassifier-orange?style=flat-square)
![Accuracy](https://img.shields.io/badge/Accuracy-~82%25-brightgreen?style=flat-square)

</div>

---

## Descripción general

**Minerva** es una plataforma de analítica educativa que predice el riesgo de suspenso de los estudiantes usando Machine Learning. Está pensada para profesores que quieren identificar alumnos en riesgo académico antes de que el problema sea irreversible.

El sistema ofrece dos clientes:

- **Aplicación web** (FastAPI + Jinja2): dashboard interactivo accesible desde el navegador con carga de datos vía CSV.
- **App Android nativa** (Kotlin + Jetpack Compose): acceso móvil al mismo backend con autenticación, predicción por CSV y vista de detalle por alumno.

El backend combina un modelo de clasificación entrenado con datos sintéticos realistas, un pipeline de NLP ligero para interpretar el feedback del docente, y una API REST compartida por ambos clientes.

---

## Características

**Predicción de riesgo académico**
- Clasifica a cada estudiante en cuatro niveles: Bajo, Medio, Alto y Crítico
- Calcula la probabilidad de aprobado como porcentaje
- Procesa hasta un grupo completo de alumnos en una sola petición (batch)

**Machine Learning**
- Modelo SGDClassifier entrenado con 1 000 estudiantes sintéticos
- 9 features: asistencia, notas, resultados de aprendizaje (RA) y participación
- Reentrenamiento completo y actualización incremental (`partial_fit`) disponibles vía API
- NLP ligero: convierte el feedback textual del profesor en un score numérico (0–1)

**Aplicación web**
- Carga de datos mediante archivo CSV
- Tabla de resultados con colores por nivel de riesgo
- Panel lateral (drawer) con el detalle completo de cada estudiante
- Barras de progreso por resultado de aprendizaje

**App Android**
- Login con Supabase Auth y persistencia de sesión con DataStore
- Renovación automática de token (TokenAuthenticator + OkHttp)
- Carga y parseo de CSV desde el almacenamiento del dispositivo
- Pantalla de predicción con badges de riesgo por color
- Vista de detalle por alumno con desglose de métricas
- Arquitectura limpia: Splash → Login → Home / Evaluar / Opciones

**Autenticación**
- Login y gestión de sesiones con Supabase Auth
- Rutas web protegidas con `SessionMiddleware`
- Token Bearer con refresco automático en la app Android

---

## Stack tecnológico

### Backend / Web

| Capa | Tecnología |
|---|---|
| Servidor web | FastAPI 0.124 + Uvicorn 0.38 |
| Plantillas | Jinja2 3.1 |
| Sesiones | Starlette `SessionMiddleware` |
| Autenticación | Supabase Auth (SDK 2.30) |
| Machine Learning | scikit-learn 1.8 (SGDClassifier, StandardScaler) |
| Datos | pandas 3.0 + NumPy 2.4 |
| Persistencia del modelo | joblib (`.pkl`) |
| Frontend web | HTML5, CSS3 (15 módulos), Vanilla JS |

### App Android

| Capa | Tecnología |
|---|---|
| Lenguaje | Kotlin 2.x |
| UI | Jetpack Compose + Material 3 |
| Navegación | Navigation Compose |
| Inyección de dependencias | Hilt (Dagger) |
| Red | Retrofit 2 + OkHttp 4 + kotlinx.serialization |
| Sesiones | DataStore Preferences |
| Animaciones | Lottie Compose |
| Arquitectura | Clean Architecture (data / domain / presentation) |
| Min SDK | API 26 (Android 8.0) |
| Target SDK | API 35 (Android 15) |

---

## Estructura del proyecto

```
Minerva/
├── main.py                      # Punto de entrada: rutas FastAPI, auth, sesiones
├── requirements.txt             # Dependencias Python
├── .env                         # Variables de entorno (no se sube al repo)
│
├── ml/
│   ├── train.py                 # Generación de datos sintéticos y entrenamiento
│   ├── predict.py               # Predicción por lotes y actualización incremental
│   └── feedback.py              # NLP: texto del profesor → score 0–1
│
├── models/
│   └── minerva_model.pkl        # Modelo serializado (scaler + clf + features)
│
├── templates/
│   ├── index.html               # Landing page
│   ├── login.html               # Formulario de acceso
│   └── demo.html                # Dashboard principal (protegido)
│
├── static/
│   ├── css/                     # 15 hojas de estilo modulares
│   ├── scripts/                 # script.js (landing) · profesor.js (dashboard)
│   └── media/                   # Imágenes y fondos
│
└── android/                     # App Android nativa
    └── app/src/main/java/com/minerva/app/
        ├── core/                # Result<T> genérico
        ├── data/
        │   ├── csv/             # CsvStudentParser
        │   ├── local/           # SessionDataStore (DataStore Preferences)
        │   ├── remote/          # Retrofit APIs, DTOs, interceptores
        │   └── repository/      # Implementaciones de repositorios
        ├── di/                  # Módulos Hilt (Network, DataStore, Repository)
        ├── domain/
        │   ├── model/           # AuthSession, Student, Prediction, RiskLevel…
        │   ├── repository/      # Interfaces de repositorios
        │   └── usecase/         # Casos de uso (Login, Logout, Predict, ParseCsv…)
        └── presentation/
            ├── login/           # LoginScreen, LoginViewModel, LoginUiState
            ├── home/            # HomeScreen
            ├── prediction/      # PredictionScreen, StudentDetailScreen, componentes
            ├── profile/         # ProfileScreen, ProfileViewModel
            ├── splash/          # SplashScreen
            ├── main/            # MainScreen (shell con BottomBar)
            ├── navigation/      # MinervaNavHost, Routes
            └── theme/           # Color, Type, Theme, Animations, Modifiers
```

---

## Instalación — Backend web

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
pip install -r requirements.txt
```

### Variables de entorno

Crea un archivo `.env` en la raíz:

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

El repositorio no incluye el archivo `.pkl`. Antes de arrancar el servidor por primera vez:

```bash
python ml/train.py
```

Esto crea `models/minerva_model.pkl` con una accuracy aproximada del 82 %.

### Ejecutar el servidor

```bash
uvicorn main:app --reload
```

La aplicación queda disponible en `http://127.0.0.1:8000`.

---

## Instalación — App Android

### Requisitos previos

- Android Studio Hedgehog o superior
- JDK 17
- Dispositivo o emulador con Android 8.0+ (API 26)

### Configuración

1. Copia el archivo `android/local.properties` (o créalo si no existe) y añade:

```properties
MINERVA_BASE_URL=http://10.0.2.2:8000/
SUPABASE_URL=https://<tu-proyecto>.supabase.co
SUPABASE_ANON_KEY=<tu-anon-key>
```

> `10.0.2.2` es la IP del host en el emulador de Android. Cámbiala por la IP de tu máquina si usas un dispositivo físico.

2. Abre el proyecto en Android Studio desde la carpeta `android/`.
3. Deja que Gradle sincronice las dependencias.
4. Selecciona la variante `debug` y lanza en el emulador o dispositivo.

### Generar APK / AAB de release

```bash
cd android
./gradlew assembleRelease      # genera APK firmado
./gradlew bundleRelease        # genera AAB para Google Play
```

El APK firmado queda en `android/app/build/outputs/apk/release/`. La última versión publicada es **v0.1.0-alpha** y está disponible en las [GitHub Releases](https://github.com/pach24/Minerva/releases).

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

**App Android:**

```
Splash → (sesión activa) → Main Shell
       → (sin sesión)    → Login → Main Shell
                                      ├── Home       (bienvenida)
                                      ├── Evaluar    → cargar CSV → predicción → detalle alumno
                                      └── Opciones   (perfil de usuario)
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

**Cuerpo de la petición:**
```json
[
  {
    "nombre": "Ana García",
    "asistencia": 78,
    "nota_practicas": 5.8,
    "nota_ra1": 7.0,
    "nota_ra2": 6.5,
    "nota_ra3": 5.0,
    "nota_ra4": 6.0,
    "nota_ra5": 4.5,
    "nota_ra6": 5.5,
    "nota_ra7": 7.0,
    "nota_ra8": 5.0,
    "nota_ra9": 6.0,
    "feedback_profesor": "Participa pero se distrae con facilidad"
  }
]
```

**Respuesta:**
```json
[
  {
    "nombre": "Ana García",
    "asistencia": 78,
    "prob_aprobado": 61.0,
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

El CSV que se sube (web o app Android) debe incluir una fila de cabecera. Las columnas obligatorias son el nombre y los 9 RAs; el resto son opcionales pero mejoran la predicción.

```
id,nombre,asistencia,nota_practicas,ra1,ra2,ra3,ra4,ra5,ra6,ra7,ra8,ra9,confianza,motivacion_escolar,recursos_casa,nivel_educativo_familia,feedback_profesor
```

**Columnas obligatorias:**

| Columna | Alias aceptados | Tipo | Rango |
|---|---|---|---|
| `nombre` | `alumno`, `name` | Texto | Nombre completo |
| `asistencia` | — | Número | 0 – 100 (%) o 0 – 1 |
| `ra1` | `nota_ra1` | Número | 0 – 10 · RA1 Estructura de programas |
| `ra2` | `nota_ra2` | Número | 0 – 10 · RA2 POO básica |
| `ra3` | `nota_ra3` | Número | 0 – 10 · RA3 Estructuras de datos |
| `ra4` | `nota_ra4` | Número | 0 – 10 · RA4 Algoritmos y eficiencia |
| `ra5` | `nota_ra5` | Número | 0 – 10 · RA5 Tratamiento de ficheros |
| `ra6` | `nota_ra6` | Número | 0 – 10 · RA6 Interfaces gráficas |
| `ra7` | `nota_ra7` | Número | 0 – 10 · RA7 Herencia y polimorfismo |
| `ra8` | `nota_ra8` | Número | 0 – 10 · RA8 Acceso a bases de datos |
| `ra9` | `nota_ra9` | Número | 0 – 10 · RA9 Desarrollo de proyectos |

**Columnas opcionales:**

| Columna | Alias aceptados | Tipo | Rango |
|---|---|---|---|
| `id` | — | Texto | Identificador del alumno |
| `nota_practicas` | — | Número | 0 – 10 (si falta se usa la media de RAs) |
| `confianza` | — | Entero | 0 – 2 |
| `motivacion_escolar` | `motivacion` | Entero | 1 – 4 |
| `recursos_casa` | `recursos` | Entero | 0 – 1 |
| `nivel_educativo_familia` | `nivel_educativo` | Entero | 1 – 6 |
| `feedback_profesor` | `feedback` | Texto | Observación libre del profesor |

---

## Modelo de Machine Learning

### Features del modelo

**Notas de Resultados de Aprendizaje (entrada directa):**

| Feature | Descripción | Rango |
|---|---|---|
| `nota_ra1` | RA1 · Estructura de programas | 0 – 10 |
| `nota_ra2` | RA2 · POO básica | 0 – 10 |
| `nota_ra3` | RA3 · Estructuras de datos | 0 – 10 |
| `nota_ra4` | RA4 · Algoritmos y eficiencia | 0 – 10 |
| `nota_ra5` | RA5 · Tratamiento de ficheros | 0 – 10 |
| `nota_ra6` | RA6 · Interfaces gráficas | 0 – 10 |
| `nota_ra7` | RA7 · Herencia y polimorfismo | 0 – 10 |
| `nota_ra8` | RA8 · Acceso a bases de datos | 0 – 10 |
| `nota_ra9` | RA9 · Desarrollo de proyectos | 0 – 10 |

**Features académicas base:**

| Feature | Descripción | Rango |
|---|---|---|
| `ratio_asistencia` | Ratio de asistencia (0–1) o porcentaje (0–100, se normaliza) | 0 – 1 |
| `notas_practicas` | Nota media de prácticas | 0 – 10 |
| `feedback_score` | NLP sobre el texto del profesor | 0 – 1 |

**Features derivadas (calculadas automáticamente):**

| Feature | Descripción |
|---|---|
| `nota_media` | Media de los 9 RAs |
| `pct_ras_superados` | Porcentaje de RAs con nota ≥ 5 |
| `tendencia_progreso` | Media RA7-9 − media RA1-3 (progresión del alumno) |
| `media_ras_teoricos` | Media de RA1, RA2, RA4, RA7 |
| `media_ras_practicos` | Media de RA3, RA5, RA6, RA8, RA9 |
| `gap_teoria_practica` | Diferencia teoría − práctica |
| `ra1_bloqueante` | 1 si RA1 < 5 (RA bloqueante de la asignatura) |

**Features opcionales (pueden estar vacías ~30% de los casos):**

| Feature | Descripción | Rango |
|---|---|---|
| `confianza` | Nivel de confianza del alumno | 0 – 2 |
| `motivacion_escolar` | Motivación escolar | 1 – 4 |
| `recursos_casa` | Recursos disponibles en casa | 0 – 1 |
| `nivel_educativo_familia` | Nivel educativo familiar | 1 – 6 |

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

Al entrenar (`python ml/train.py`), el script ejecuta una evaluación completa antes de guardar el modelo:

| Métrica | Umbral mínimo | Resultado actual |
|---|---|---|
| Accuracy (test) | ≥ 0.80 | 0.820 |
| Recall mínimo por clase | ≥ 0.75 | Suspende 0.84 · Aprueba 0.81 |
| F1-score mínimo por clase | ≥ 0.75 | Suspende 0.78 · Aprueba 0.85 |
| Diferencia train-test | < 0.05 | 0.035 |
| Veredicto | — | **MODELO APTO** |

### NLP para feedback del profesor

El módulo `ml/feedback.py` convierte el texto libre del docente en un score numérico:

- Sin texto → `0.5` (neutro)
- Detecta palabras positivas (`esfuerza`, `participa`, `excelente`…) y negativas (`distraído`, `falta`, `abandona`…)
- Score = `positivas / (positivas + negativas)`
- Sin palabras reconocidas → `0.5`

---

## Arquitectura Android

La app sigue **Clean Architecture** con tres capas bien separadas:

```
presentation/   ← Jetpack Compose + ViewModels (Hilt) + Navigation
domain/         ← Use Cases + interfaces de repositorios + modelos de dominio
data/           ← Implementaciones Retrofit, DataStore, CsvParser, DTOs
```

**Flujo de datos:**

```
Compose UI  ←→  ViewModel  ←→  UseCase  ←→  Repository  ←→  Retrofit / DataStore
```

**Gestión de sesión:**

- El token de acceso se persiste en `SessionDataStore` (DataStore Preferences).
- `AuthInterceptor` añade el header `Authorization: Bearer <token>` a cada petición.
- `TokenAuthenticator` refresca automáticamente el token cuando la API devuelve 401.

---

## Pendiente / Roadmap

- [ ] Suite de tests (pytest para backend, JUnit/Compose tests para Android)
- [ ] Gráficos de evolución temporal por alumno
- [ ] Ajuste de pesos del modelo por parte del profesor (CU-5)
- [ ] Exportación de informes en PDF
- [ ] Dockerización del backend
- [ ] Pantalla de estadísticas globales del grupo en la app Android
- [ ] Publicación en Google Play (track interno)

---

## Licencia

Proyecto desarrollado con fines académicos.

---

<div align="center">

**Minerva** — *Predicting educational outcomes through intelligent systems.*

</div>
