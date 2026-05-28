import pickle
import os
import numpy as np
import pandas as pd
from ml.feedback import texto_a_score

MODEL_PATH = os.getenv("MODEL_PATH", "models/minerva_model.pkl")

FEATURES = [
    "nota_ra1", "nota_ra2", "nota_ra3", "nota_ra4", "nota_ra5",
    "nota_ra6", "nota_ra7", "nota_ra8", "nota_ra9",
    "ratio_asistencia", "notas_practicas", "feedback_score",
    "nota_media", "pct_ras_superados", "tendencia_progreso",
    "media_ras_teoricos", "media_ras_practicos", "gap_teoria_practica",
    "ra1_bloqueante",
    "confianza", "motivacion_escolar", "recursos_casa", "nivel_educativo_familia",
]

RA_NAMES = {
    "nota_ra1": "RA1 · Estructura de programas",
    "nota_ra2": "RA2 · POO básica",
    "nota_ra3": "RA3 · Estructuras de datos",
    "nota_ra4": "RA4 · Algoritmos y eficiencia",
    "nota_ra5": "RA5 · Tratamiento de ficheros",
    "nota_ra6": "RA6 · Interfaces gráficas",
    "nota_ra7": "RA7 · Herencia y polimorfismo",
    "nota_ra8": "RA8 · Acceso a bases de datos",
    "nota_ra9": "RA9 · Desarrollo de proyectos",
}

_model = None


def _load() -> dict:
    global _model
    if _model is None:
        with open(MODEL_PATH, "rb") as f:
            _model = pickle.load(f)
    return _model


def _build_row(s: dict) -> dict:
    ra = np.array([
        float(s.get("nota_ra1", 5) or 5),
        float(s.get("nota_ra2", 5) or 5),
        float(s.get("nota_ra3", 5) or 5),
        float(s.get("nota_ra4", 5) or 5),
        float(s.get("nota_ra5", 5) or 5),
        float(s.get("nota_ra6", 5) or 5),
        float(s.get("nota_ra7", 5) or 5),
        float(s.get("nota_ra8", 5) or 5),
        float(s.get("nota_ra9", 5) or 5),
    ])

    ratio_asistencia = s.get("ratio_asistencia") or s.get("asistencia") or 0
    ratio_asistencia = float(ratio_asistencia)
    if ratio_asistencia > 1.0:
        ratio_asistencia /= 100.0

    nota_media          = float(ra.mean())
    pct_ras_superados   = float((ra >= 5).sum() / 9)
    tendencia_progreso  = float(ra[6:9].mean() - ra[0:3].mean())
    media_ras_teoricos  = float(ra[[0, 1, 3, 6]].mean())
    media_ras_practicos = float(ra[[2, 4, 5, 7, 8]].mean())
    gap_teoria_practica = float(media_ras_teoricos - media_ras_practicos)
    ra1_bloqueante      = float(ra[0] < 5)

    notas_practicas = s.get("notas_practicas") or s.get("nota_practicas")
    notas_practicas = float(notas_practicas) if notas_practicas not in (None, '') else nota_media

    feedback_score = texto_a_score(s.get("feedback_profesor", ""))

    def optional(key):
        v = s.get(key)
        return float(v) if v not in (None, '', 'null') else np.nan

    return {
        "nota_ra1": ra[0], "nota_ra2": ra[1], "nota_ra3": ra[2],
        "nota_ra4": ra[3], "nota_ra5": ra[4], "nota_ra6": ra[5],
        "nota_ra7": ra[6], "nota_ra8": ra[7], "nota_ra9": ra[8],
        "ratio_asistencia":    ratio_asistencia,
        "notas_practicas":     notas_practicas,
        "feedback_score":      feedback_score,
        "nota_media":          nota_media,
        "pct_ras_superados":   pct_ras_superados,
        "tendencia_progreso":  tendencia_progreso,
        "media_ras_teoricos":  media_ras_teoricos,
        "media_ras_practicos": media_ras_practicos,
        "gap_teoria_practica": gap_teoria_practica,
        "ra1_bloqueante":      ra1_bloqueante,
        "confianza":               optional("confianza"),
        "motivacion_escolar":      optional("motivacion_escolar"),
        "recursos_casa":           optional("recursos_casa"),
        "nivel_educativo_familia": optional("nivel_educativo_familia"),
    }


def predict_students(students: list[dict]) -> list[dict]:
    data    = _load()
    imputer = data["imputer"]
    scaler  = data["scaler"]
    clf_rf  = data["clf_rf"]

    X     = pd.DataFrame([_build_row(s) for s in students])[FEATURES]
    X_imp = imputer.transform(X)
    X_sc  = scaler.transform(X_imp)
    probs = clf_rf.predict_proba(X_sc)[:, 1] * 100

    results = []
    for s, prob in zip(students, probs):
        prob = round(float(prob), 1)
        if prob >= 70:
            riesgo = "Bajo"
        elif prob >= 50:
            riesgo = "Medio"
        elif prob >= 30:
            riesgo = "Alto"
        else:
            riesgo = "Crítico"
        results.append({**s, "prob_aprobado": prob, "nivel_riesgo": riesgo})

    return results


def partial_update(labeled_students: list[dict]) -> None:
    """Actualiza el modelo incremental SGD con partial_fit."""
    global _model
    data    = _load()
    imputer = data["imputer"]
    scaler  = data["scaler"]
    clf_sgd = data["clf_sgd"]

    X = pd.DataFrame([_build_row(s) for s in labeled_students])[FEATURES]
    y = [int(s["aprobado"]) for s in labeled_students]

    X_imp = imputer.transform(X)
    X_sc  = scaler.transform(X_imp)
    clf_sgd.partial_fit(X_sc, y, classes=[0, 1])

    data["clf_sgd"] = clf_sgd
    with open(MODEL_PATH, "wb") as f:
        pickle.dump(data, f)
    _model = None
