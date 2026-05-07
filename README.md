<div align="center">

# 🧠 Minerva

### *Educational Intelligence Platform powered by Machine Learning*

Predict academic risk, analyze student performance, and empower educators with intelligent educational analytics.

<br>

![Python](https://img.shields.io/badge/Python-3.11+-3776AB?style=for-the-badge&logo=python&logoColor=white)
![FastAPI](https://img.shields.io/badge/FastAPI-0.109-009688?style=for-the-badge&logo=fastapi&logoColor=white)
![Scikit-Learn](https://img.shields.io/badge/Scikit--Learn-ML-F7931E?style=for-the-badge&logo=scikitlearn&logoColor=white)
![SQLite](https://img.shields.io/badge/SQLite-Database-003B57?style=for-the-badge&logo=sqlite&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Supported-4169E1?style=for-the-badge&logo=postgresql&logoColor=white)

<br>

![Status](https://img.shields.io/badge/Status-Active-success?style=flat-square)
![License](https://img.shields.io/badge/License-Educational-blue?style=flat-square)
![Architecture](https://img.shields.io/badge/Architecture-Modular-informational?style=flat-square)
![ML](https://img.shields.io/badge/Machine%20Learning-SGDClassifier-orange?style=flat-square)

</div>

---

# ✨ Overview

**Minerva** is an educational analytics platform designed to predict student academic risk using Machine Learning.

The platform combines:

- 📊 Predictive analytics
- 🧠 Machine Learning models
- 🏫 Educational performance tracking
- 📁 CSV batch processing
- 🎨 Modern dashboard UI
- ⚡ FastAPI backend architecture

Minerva helps educators identify students at risk before academic failure occurs.

---

# 🚀 Features

## 📈 Predictive Analytics
- Student risk prediction
- Pass probability estimation
- Risk classification system
- Historical prediction storage

## 🤖 Machine Learning
- Synthetic educational dataset generation
- Feature engineering pipeline
- SGDClassifier training
- Cross-validation metrics
- Persistent trained models

## 🌐 Web Platform
- FastAPI REST API
- Interactive dashboard
- CSV upload support
- Authentication system
- Responsive Apple-inspired UI

## 🗄️ Database Support
- SQLite integration
- PostgreSQL compatibility
- Relational data model
- Prediction persistence

---

# 🏗️ Architecture

```text
                ┌────────────────────┐
                │     Frontend UI    │
                │ HTML • CSS • JS    │
                └─────────┬──────────┘
                          │
                          ▼
                ┌────────────────────┐
                │      FastAPI       │
                │   REST Endpoints   │
                └─────────┬──────────┘
                          │
        ┌─────────────────┼─────────────────┐
        ▼                 ▼                 ▼
┌──────────────┐ ┌────────────────┐ ┌────────────────┐
│ Authentication│ │ ML Prediction │ │ Database Layer │
│   Sessions    │ │    Engine      │ │ SQLite/Postgres│
└──────────────┘ └────────────────┘ └────────────────┘
```

---

# 🛠️ Tech Stack

## Backend
| Technology | Purpose |
|---|---|
| FastAPI | API Framework |
| SQLAlchemy | ORM |
| Pydantic | Data validation |
| Uvicorn | ASGI server |

## Machine Learning
| Technology | Purpose |
|---|---|
| Scikit-learn | ML model training |
| Pandas | Data processing |
| NumPy | Numerical operations |
| Joblib | Model persistence |

## Frontend
| Technology | Purpose |
|---|---|
| HTML5 | Structure |
| CSS3 | Styling |
| JavaScript | Interaction |
| Jinja2 | Templating |

---

# 📂 Project Structure

```bash
pach24-minerva/
│
├── app/
│   ├── config/          # App configuration
│   ├── core/            # Core services
│   ├── database/        # CRUD & models
│   ├── ml/              # Machine Learning logic
│   ├── routers/         # API endpoints
│   └── schemas/         # Pydantic schemas
│
├── static/
│   ├── css/             # Frontend styling
│   └── scripts/         # Frontend scripts
│
├── templates/           # Jinja2 templates
│
├── main.py              # Entry point
└── requirements.txt
```

---

# ⚙️ Installation

## 1️⃣ Clone the repository

```bash
git clone https://github.com/yourusername/minerva.git
cd minerva
```

---

## 2️⃣ Create a virtual environment

### Windows

```bash
python -m venv venv
venv\Scripts\activate
```

### Linux / macOS

```bash
python3 -m venv venv
source venv/bin/activate
```

---

## 3️⃣ Install dependencies

```bash
pip install -r requirements.txt
```

---

# ▶️ Running the Project

Start the FastAPI server:

```bash
uvicorn main:app --reload
```

Application URL:

```text
http://127.0.0.1:8000
```

---

# 🔐 Default Credentials

```text
Username: admin
Password: admin
```

---

# 📡 API Endpoints

# 🎯 Prediction API

## Predict a single student

```http
POST /api/predict
```

### Example Request

```json
{
  "nombre": "John Doe",
  "nota_media": 7.8,
  "asistencia": 85,
  "nota_practicas": 8.1,
  "ra_completados": 16,
  "participacion": 7
}
```

---

## Batch prediction from CSV

```http
POST /api/predict/batch
```

Required CSV columns:

```text
nombre,nota_media,asistencia,nota_practicas,ra_completados,participacion
```

---

# 🧠 Machine Learning API

## Train model

```http
POST /api/admin/train
```

## Training status

```http
GET /api/admin/train/status
```

## Generate synthetic dataset

```http
POST /api/admin/data/generate
```

## Model information

```http
GET /api/admin/model/info
```

---

# 🧪 Machine Learning Pipeline

Minerva uses a complete ML workflow:

```text
Synthetic Data
      ↓
Feature Engineering
      ↓
Normalization
      ↓
Model Training
      ↓
Prediction Service
```

## Features Used

| Feature | Description |
|---|---|
| nota_media | Average grade |
| asistencia | Attendance |
| nota_practicas | Practical assignments |
| ra_completados | Completed learning outcomes |
| participacion | Participation score |
| engagement | Derived engagement metric |

---

# 🗄️ Database Design

Main tables:

| Table | Purpose |
|---|---|
| users | Authentication |
| students | Student records |
| academic_data | Educational metrics |
| predictions | Prediction history |

---

# 🎨 Frontend Experience

Minerva includes a modern educational dashboard inspired by Apple's design philosophy.

### UI Highlights
- Glassmorphism components
- Smooth transitions
- Responsive layout
- Risk visualization badges
- Interactive student drawer
- Minimalist design system

---


# 👨‍💻 Development Notes

Minerva follows a modular architecture approach:

- Clean separation of concerns
- Independent ML layer
- Reusable schemas
- Database abstraction
- Config-driven settings
- Scalable API structure

---

# 📜 License

This project was developed for educational and academic purposes.

---

<div align="center">

## 🌟 Minerva

### *Predicting educational outcomes through intelligent systems.*

<br>

Made with ❤️ using FastAPI & Machine Learning

</div>
