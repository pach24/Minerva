<div align="center">

# Minerva

### *Plataforma de Inteligencia Educativa con Machine Learning*

Predice el riesgo académico, analiza el rendimiento de los estudiantes y apoya a los docentes con analítica inteligente — disponible como aplicación web y app nativa Android.

<br>

![Python](https://img.shields.io/badge/Python-3.12-3776AB?style=for-the-badge&logo=python&logoColor=white)
![FastAPI](https://img.shields.io/badge/FastAPI-0.124-009688?style=for-the-badge&logo=fastapi&logoColor=white)
![Scikit-Learn](https://img.shields.io/badge/Scikit--Learn-1.8-F7931E?style=for-the-badge&logo=scikitlearn&logoColor=white)
![Supabase](https://img.shields.io/badge/Supabase-Auth_+_DB-3ECF8E?style=for-the-badge&logo=supabase&logoColor=white)
![Android](https://img.shields.io/badge/Android-API_26+-3DDC84?style=for-the-badge&logo=android&logoColor=white)
![Kotlin](https://img.shields.io/badge/Kotlin-2.x-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)

<br>

![Status](https://img.shields.io/badge/Estado-Funcional-success?style=flat-square)
![Version](https://img.shields.io/badge/Versión-0.1.0--alpha-blue?style=flat-square)
![License](https://img.shields.io/badge/Licencia-Académica-blue?style=flat-square)
![ML](https://img.shields.io/badge/Modelo-RandomForest_+_SGD-orange?style=flat-square)
![F1](https://img.shields.io/badge/F1_(CV)-0.985-brightgreen?style=flat-square)

</div>

---

## Descripción general

**Minerva** es una plataforma de analítica educativa que predice el riesgo de suspenso de los estudiantes usando Machine Learning. Está pensada para profesores que quieren identificar alumnos en riesgo académico antes de que el problema sea irreversible.

El sistema ofrece dos clientes:

- **Aplicación web** (FastAPI + Jinja2): dashboard "Insights" con estética iOS, carga de datos vía CSV, predicción por lotes y panel de detalle premium por alumno.
- **App Android nativa** (Kotlin + Jetpack Compose): acceso móvil al mismo backend con autenticación, predicción por CSV y vista de detalle por alumno.

El backend combina un modelo **RandomForest** optimizado con GridSearchCV (más un SGDClassifier paralelo para actualización incremental), un pipeline de NLP ligero para interpretar el feedback del docente, persistencia del historial de predicciones en Supabase y una API REST compartida por ambos clientes.

---

## Características

**Predicción de riesgo académico**
- Clasifica a cada estudiante en cuatro niveles: Bajo, Medio, Alto y Crítico
- Calcula la probabilidad de aprobado como porcentaje
- Procesa un grupo completo de alumnos en una sola petición (batch)
- Guarda cada predicción en Supabase (`predicciones`) para consultar el historial por alumno

**Machine Learning**
- **RandomForestClassifier** ajustado con GridSearchCV (5-fold estratificado, scoring F1)
- **SGDClassifier** paralelo que permite actualización incremental (`partial_fit`) sin reentrenar
- 23 features: 9 RAs, 3 académicas base, 7 derivadas y 4 socioeducativas opcionales
- Imputación de valores ausentes (mediana) + StandardScaler + SMOTE opcional si hay desbalanceo
- Entrenado con 1 000 estudiantes sintéticos con correlaciones realistas y etiqueta según normativa FP
- Métricas completas exportadas a `model_metrics.json` y servidas en `/modelo/metricas`
- NLP ligero: convierte el feedback textual del profesor en un score numérico (0–1)

**Aplicación web — Insights**
- Carga de CSV propio, **datos de ejemplo con un clic** o descarga del CSV de muestra
- Franja de estadísticas del grupo: alumnos, media, asistencia media y alumnos en riesgo
- Tabla con avatar e iniciales, acento de color por riesgo, barra de probabilidad con porcentaje
- **Panel de detalle premium**: anillo de progreso animado con la probabilidad, tarjeta tintada según riesgo, métricas con icono, RAs como barras laterales y observación del profesor
- Overlay de carga con el logo de Minerva "respirando" (animación de pulso y resplandor)
- Menú contextual de usuario con el **nombre real de la cuenta** (Supabase `display_name`) y cierre de sesión
- Tipografía **Poppins** y estética iOS en toda la experiencia (login incluido)

**App Android**
- Login con Supabase Auth y persistencia de sesión con DataStore
- Renovación automática de token (TokenAuthenticator + OkHttp)
- Carga y parseo de CSV desde el almacenamiento del dispositivo
- Pantalla de predicción con badges de riesgo por color y animación del logo "respirando" durante el análisis
- Vista de detalle por alumno con desglose de métricas
- Arquitectura limpia: Splash → Login → Home / Evaluar / Opciones

**Autenticación**
- Web: sesión de servidor (`SessionMiddleware`) creada tras validar credenciales en Supabase; el nombre mostrado se obtiene de los metadatos del usuario (`display_name` → `full_name` → `name`)
- Android: token Bearer de Supabase **verificado localmente** en el backend mediante JWKS (ES256), sin llamadas extra por petición
- Refresco automático del token en la app Android

---

## Stack tecnológico

### Backend / Web

| Capa | Tecnología |
|---|---|
| Servidor web | FastAPI 0.124 + Uvicorn 0.38 |
| Plantillas | Jinja2 3.1 |
| Sesiones | Starlette `SessionMiddleware` |
| Autenticación | Supabase Auth (SDK 2.30) + PyJWT con JWKS (ES256) |
| Base de datos | Supabase PostgreSQL (tabla `predicciones`) |
| Machine Learning | scikit-learn 1.8 (RandomForest + SGDClassifier, GridSearchCV, SimpleImputer, StandardScaler) |
| Datos | pandas 3.0 + NumPy 2.4 |
| Persistencia del modelo | pickle (`models/minerva_model.pkl`) |
| Frontend web | HTML5, CSS3 modular, Vanilla JS, tipografía Poppins |

### App Android

| Capa | Tecnología |
|---|---|
| Lenguaje | Kotlin 2.x |
| UI | Jetpack Compose + Material 3 |
| Navegación | Navigation Compose |
| Inyección de dependencias | Hilt (Dagger) |
| Red | Retrofit 2 + OkHttp 4 + kotlinx.serialization |
| Sesiones | DataStore Preferences |
| Animaciones | Lottie Compose + animaciones Compose (`InfiniteTransition`) |
| Arquitectura | Clean Architecture (data / domain / presentation) |
| Min SDK | API 26 (Android 8.0) |
| Target SDK | API 35 (Android 15) |

---

## Estructura del proyecto

```
Minerva/
├── main.py                      # Rutas FastAPI, auth (sesión + JWT), API ML e historial
├── requirements.txt             # Dependencias Python
├── model_metrics.json           # Métricas del último entrenamiento (autogenerado)
├── datos_prueba.csv             # CSV de pruebas local
├── .env                         # Variables de entorno (no se sube al repo)
│
├── ml/
│   ├── train.py                 # Datos sintéticos + GridSearchCV + evaluación + export métricas
│   ├── predict.py               # Predicción por lotes y actualización incremental
│   └── feedback.py              # NLP: texto del profesor → score 0–1
│
├── models/
│   └── minerva_model.pkl        # Modelo serializado (imputer + scaler + RF + SGD + features)
│
├── templates/
│   ├── index.html               # Landing page
│   ├── login.html               # Acceso (iOS style, Poppins, etiquetas flotantes)
│   └── demo.html                # Dashboard Insights (protegido)
│
├── static/
│   ├── css/                     # Hojas de estilo modulares (demo, login, landing…)
│   ├── scripts/                 # script.js (landing) · profesor.js (legacy)
│   ├── media/                   # Logos, iconos, QR y fondos
│   └── samples/
│       └── alumnos_2dam.csv     # CSV de ejemplo (probar/descargar desde Insights)
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
            ├── splash/          # SplashScreen (Lottie)
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

# (Opcional) SMOTE para datasets desbalanceados durante el entrenamiento
pip install imbalanced-learn
```

### Variables de entorno

Crea un archivo `.env` en la raíz:

```env
SUPABASE_URL=https://<tu-proyecto>.supabase.co
SUPABASE_KEY=<tu-anon-key>
SESSION_SECRET=<cadena-aleatoria-larga>

MODEL_PATH=models/minerva_model.pkl
METRICS_PATH=model_metrics.json
MIN_ACCURACY=0.80
TEST_SIZE=0.2
RANDOM_STATE=42
NUM_SYNTHETIC_STUDENTS=1000
```

### Configuración en Supabase

1. **Tabla de historial** — crea la tabla `predicciones` (SQL Editor):

```sql
create table if not exists predicciones (
    id bigint generated always as identity primary key,
    fecha timestamptz default now(),
    alumno_id text,
    alumno_nombre text,
    asistencia numeric,
    nota_ra1 numeric, nota_ra2 numeric, nota_ra3 numeric,
    nota_ra4 numeric, nota_ra5 numeric, nota_ra6 numeric,
    nota_ra7 numeric, nota_ra8 numeric, nota_ra9 numeric,
    nota_media numeric,
    pct_ras_superados numeric,
    prob_aprobado numeric,
    nivel_riesgo text,
    feedback_profesor text
);
```

2. **Nombre visible del usuario** — el navbar muestra el nombre asociado a la cuenta. Rellénalo en *Authentication → Users → Display name*, o vía SQL:

```sql
update auth.users
set raw_user_meta_data = jsonb_set(
    coalesce(raw_user_meta_data, '{}'::jsonb),
    '{display_name}', '"Nombre Apellido"'
)
where email = 'profesor@centro.com';
```

### Entrenar el modelo inicial

El repositorio no incluye el archivo `.pkl`. Antes de arrancar el servidor por primera vez:

```bash
python ml/train.py
```

El script ejecuta GridSearchCV, imprime la evaluación completa (accuracy, recall, F1, kappa, matriz de confusión, cross-validation) y guarda el modelo en `models/minerva_model.pkl` y las métricas en `model_metrics.json`.

### Ejecutar el servidor

```bash
uvicorn main:app --reload
```

La aplicación queda disponible en `http://127.0.0.1:8000`.

---

## Instalación — App Android

### Requisitos previos

- Android Studio Hedgehog o superior
- JDK 17 (vale el JBR incluido con Android Studio)
- Dispositivo o emulador con Android 8.0+ (API 26)

### Configuración

1. Copia `android/local.properties.example` como `android/local.properties` y añade:

```properties
MINERVA_BASE_URL=http://10.0.2.2:8000/
SUPABASE_URL=https://<tu-proyecto>.supabase.co
SUPABASE_ANON_KEY=<tu-anon-key>
```

> `10.0.2.2` es la IP del host en el emulador de Android. Cámbiala por la IP de tu máquina si usas un dispositivo físico.

2. Abre el proyecto en Android Studio **desde la carpeta `android/`** (es un proyecto Gradle independiente).
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
1. Acceder a /            → Landing page con descripción de la plataforma
2. Clic en "Acceder"      → Login (/login) con campos estilo iOS
3. Autenticarse           → Supabase valida credenciales y crea la sesión
4. Insights (/demo)       → Subir CSV propio, probar los datos de ejemplo
                            o descargar el CSV de muestra
5. "Generar Predicción"   → POST /api/predecir → tabla con probabilidad y riesgo
6. Clic en un alumno      → Panel de detalle con anillo de probabilidad,
                            métricas, RAs y observación del profesor
7. Menú de usuario        → Cerrar sesión
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

| Método | Ruta | Auth | Descripción |
|---|---|---|---|
| `GET` | `/` | — | Landing page |
| `GET` | `/login` | — | Formulario de login |
| `POST` | `/login` | — | Autenticación vía Supabase (guarda el display name en sesión) |
| `GET` | `/logout` | — | Cierra sesión y redirige a `/login` |
| `GET` | `/demo` | Sesión | Dashboard Insights |
| `POST` | `/api/predecir` | Sesión o Bearer | Predicción por lotes + guardado en historial |
| `POST` | `/api/entrenar` | Sesión | Reentrenamiento completo del modelo |
| `POST` | `/api/actualizar` | Sesión | Actualización incremental (`partial_fit` sobre el SGD) |
| `GET` | `/modelo/metricas` | — | Métricas del último entrenamiento (`model_metrics.json`) |
| `GET` | `/api/historial/{alumno_id}` | Sesión o Bearer | Historial de predicciones de un alumno |

> Los endpoints marcados *Bearer* aceptan el JWT de Supabase de la app Android. El backend lo verifica **localmente** contra el JWKS público del proyecto (ES256) — sin llamadas de red por petición.

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

| Nivel | Probabilidad | Color en la UI |
|---|---|---|
| Bajo | ≥ 70 % | Verde |
| Medio | 50 – 69 % | Naranja |
| Alto | 30 – 49 % | Rojo |
| Crítico | < 30 % | Rojo intenso |

---

## Formato del CSV

El CSV que se sube (web o app Android) debe incluir una fila de cabecera. El parser web soporta campos entrecomillados con comas dentro (p. ej. feedbacks largos). Hay un CSV de ejemplo listo para usar en `static/samples/alumnos_2dam.csv` — también disponible desde la propia pantalla de Insights ("Probar con datos de ejemplo" / "Descargar datos de ejemplo").

```
id,nombre,asistencia,nota_practicas,ra1,ra2,ra3,ra4,ra5,ra6,ra7,ra8,ra9,feedback,confianza,motivacion,recursos,nivel_educativo
```

**Columnas obligatorias:**

| Columna | Alias aceptados | Tipo | Rango |
|---|---|---|---|
| `nombre` | `alumno`, `name` | Texto | Nombre completo |
| `asistencia` | — | Número | 0 – 100 (%) o 0 – 1 |
| `ra1`…`ra9` | `nota_ra1`…`nota_ra9` | Número | 0 – 10 |

**Columnas opcionales:**

| Columna | Alias aceptados | Tipo | Notas |
|---|---|---|---|
| `id` | — | Texto | Identificador del alumno (para el historial) |
| `nota_media` | `media`, `nota` | Número | Si falta, **se calcula como media de los RAs** |
| `nota_practicas` | — | Número | 0 – 10 |
| `confianza` | — | Entero | 0 – 2 |
| `motivacion_escolar` | `motivacion` | Entero | 1 – 4 |
| `recursos_casa` | `recursos` | Entero | 0 – 1 |
| `nivel_educativo_familia` | `nivel_educativo` | Entero | 1 – 6 |
| `feedback_profesor` | `feedback` | Texto | Observación libre (se procesa con NLP) |

---

## Modelo de Machine Learning

### Features del modelo (23)

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
| `ratio_asistencia` | Ratio de asistencia (se normaliza si llega como %) | 0 – 1 |
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

**Features socioeducativas opcionales (~30 % de valores ausentes, imputados con mediana):**

| Feature | Descripción | Rango |
|---|---|---|
| `confianza` | Nivel de confianza del alumno | 0 – 2 |
| `motivacion_escolar` | Motivación escolar | 1 – 4 |
| `recursos_casa` | Recursos disponibles en casa | 0 – 1 |
| `nivel_educativo_familia` | Nivel educativo familiar | 1 – 6 |

### Pipeline de entrenamiento

```
Generación de datos sintéticos (1 000 alumnos, RAs correlacionados,
etiqueta según normativa FP: media ≥ 5, asistencia ≥ 75 %, RAs superados ≥ 60 %)
           ↓
   Train / Test split estratificado (80 / 20)
           ↓
   SimpleImputer (mediana) → StandardScaler
           ↓
   SMOTE si la clase minoritaria < 40 % (opcional, requiere imbalanced-learn)
           ↓
   GridSearchCV sobre RandomForest (n_estimators, max_depth,
   min_samples_split, class_weight · 5-fold estratificado · scoring F1)
           ↓
   SGDClassifier(loss="log_loss") entrenado en paralelo → partial_fit incremental
           ↓
   Evaluación completa + validación de umbrales + cross-validation externa
           ↓
   Serialización (.pkl: imputer + scaler + RF + SGD + features)
   y export de métricas (model_metrics.json → GET /modelo/metricas)
```

### Evaluación del modelo

Resultados del último entrenamiento (`python ml/train.py`):

| Métrica | Umbral mínimo | Resultado |
|---|---|---|
| Accuracy (test) | ≥ 0.80 | **0.995** |
| Precision | — | 1.000 |
| Recall | ≥ 0.75 | 0.976 |
| F1-score | ≥ 0.75 | 0.988 |
| Cohen's Kappa | — | 0.985 |
| F1 cross-validation (k=5) | — | 0.985 ± 0.009 |
| Diferencia train-test | < 0.05 | OK |
| Veredicto | — | **MODELO APTO** |

Las features más influyentes son `ratio_asistencia` (37 %), `pct_ras_superados` (12 %) y `nota_media` (11 %).

> ⚠️ **Advertencia:** las métricas se calculan sobre datos sintéticos y no representan la precisión real sobre alumnos. La fiabilidad mejorará al incorporar datos históricos reales.

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
- El backend valida el JWT localmente contra el JWKS de Supabase (ES256).

---

## Pendiente / Roadmap

- [ ] Suite de tests (pytest para backend, JUnit/Compose tests para Android)
- [ ] Gráficos de evolución temporal por alumno (el historial ya se persiste en `predicciones`)
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
