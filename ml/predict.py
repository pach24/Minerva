import pickle
import os
import numpy as np

MODEL_PATH = os.getenv("MODEL_PATH", "models/minerva_model.pkl")

PART_MAP = {"Alta": 1.0, "Media": 0.67, "Baja": 0.33, "Nula": 0.0}

_model = None


def _load():
    global _model
    if _model is None:
        with open(MODEL_PATH, "rb") as f:
            _model = pickle.load(f)
    return _model


def predict_students(students: list[dict]) -> list[dict]:
    """
    students: lista de dicts con keys: asistencia, nota_media, participacion, nota_practicas (opcional)
    returns: misma lista con prob_aprobado (0-100) y nivel_riesgo añadidos
    """
    model = _load()

    import pandas as pd
    X = pd.DataFrame([
        {
            "asistencia":     float(s.get("asistencia", 0)),
            "nota_media":     float(s.get("nota_media", 0)),
            "participacion":  PART_MAP.get(s.get("participacion", "Media"), 0.67),
            "nota_practicas": float(s.get("nota_practicas", s.get("nota_media", 5))),
        }
        for s in students
    ])

    probs = model.predict_proba(X)[:, 1] * 100

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
