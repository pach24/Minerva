import pickle
import os
import pandas as pd
from ml.feedback import texto_a_score

MODEL_PATH = os.getenv("MODEL_PATH", "models/minerva_model.pkl")

PART_MAP = {"Alta": 1.0, "Media": 0.67, "Baja": 0.33, "Nula": 0.0}

FEATURES = [
    "asistencia", "nota_media", "participacion_score", "nota_practicas",
    "ra_herencia", "ra_polimorfismo", "ra_ficheros", "ra_interfaces",
    "feedback_score",
]

_model = None


def _load() -> dict:
    global _model
    if _model is None:
        with open(MODEL_PATH, "rb") as f:
            _model = pickle.load(f)
    return _model


def _build_row(s: dict) -> dict:
    return {
        "asistencia":          float(s.get("asistencia", 0)),
        "nota_media":          float(s.get("nota_media", 0)),
        "participacion_score": PART_MAP.get(s.get("participacion", "Media"), 0.67),
        "nota_practicas":      float(s.get("nota_practicas", s.get("nota_media", 0))),
        "ra_herencia":         float(s.get("ra_herencia", 0)),
        "ra_polimorfismo":     float(s.get("ra_polimorfismo", 0)),
        "ra_ficheros":         float(s.get("ra_ficheros", 0)),
        "ra_interfaces":       float(s.get("ra_interfaces", 0)),
        "feedback_score":      texto_a_score(s.get("feedback_profesor", "")),
    }


def predict_students(students: list[dict]) -> list[dict]:
    data   = _load()
    scaler = data["scaler"]
    clf    = data["clf"]

    X     = pd.DataFrame([_build_row(s) for s in students])[FEATURES]
    probs = clf.predict_proba(scaler.transform(X))[:, 1] * 100

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
    """Actualiza el modelo incrementalmente con datos etiquetados (partial_fit)."""
    global _model
    data   = _load()
    scaler = data["scaler"]
    clf    = data["clf"]

    X = pd.DataFrame([_build_row(s) for s in labeled_students])[FEATURES]
    y = [int(s["aprobado"]) for s in labeled_students]

    clf.partial_fit(scaler.transform(X), y, classes=[0, 1])

    with open(MODEL_PATH, "wb") as f:
        pickle.dump({"scaler": scaler, "clf": clf, "features": FEATURES}, f)
    _model = None
