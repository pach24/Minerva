"""
Genera datos sintéticos y entrena el modelo SGDClassifier.
Ejecutar una vez: python ml/train.py
"""
import numpy as np
import pandas as pd
import pickle
import os
from sklearn.linear_model import SGDClassifier
from sklearn.preprocessing import StandardScaler
from sklearn.pipeline import Pipeline
from sklearn.model_selection import train_test_split
from sklearn.metrics import accuracy_score

RANDOM_STATE = int(os.getenv("RANDOM_STATE", 42))
N = int(os.getenv("NUM_SYNTHETIC_STUDENTS", 1000))
TEST_SIZE = float(os.getenv("TEST_SIZE", 0.2))
MODEL_PATH = os.getenv("MODEL_PATH", "models/minerva_model.pkl")

PART_MAP = {"Alta": 1.0, "Media": 0.67, "Baja": 0.33, "Nula": 0.0}


def generate_data(n: int, seed: int) -> pd.DataFrame:
    rng = np.random.default_rng(seed)

    asistencia       = rng.uniform(10, 100, n)
    nota_media       = rng.uniform(1, 10, n)
    participacion    = rng.choice(list(PART_MAP.values()), n, p=[0.25, 0.40, 0.25, 0.10])
    nota_practicas   = rng.uniform(0, 10, n)

    # Score sintético que determina si aprueba
    score = (
        asistencia / 100 * 0.35 +
        nota_media / 10  * 0.45 +
        participacion    * 0.10 +
        nota_practicas / 10 * 0.10
    )
    noise = rng.normal(0, 0.06, n)
    aprobado = ((score + noise) >= 0.50).astype(int)

    return pd.DataFrame({
        "asistencia":     asistencia,
        "nota_media":     nota_media,
        "participacion":  participacion,
        "nota_practicas": nota_practicas,
        "aprobado":       aprobado,
    })


def train():
    df = generate_data(N, RANDOM_STATE)

    X = df[["asistencia", "nota_media", "participacion", "nota_practicas"]]
    y = df["aprobado"]

    X_train, X_test, y_train, y_test = train_test_split(
        X, y, test_size=TEST_SIZE, random_state=RANDOM_STATE
    )

    model = Pipeline([
        ("scaler", StandardScaler()),
        ("clf", SGDClassifier(loss="log_loss", max_iter=1000, random_state=RANDOM_STATE)),
    ])
    model.fit(X_train, y_train)

    acc = accuracy_score(y_test, model.predict(X_test))
    print(f"Accuracy: {acc:.3f} ({int(acc*100)}%)")

    os.makedirs(os.path.dirname(MODEL_PATH), exist_ok=True)
    with open(MODEL_PATH, "wb") as f:
        pickle.dump(model, f)
    print(f"Modelo guardado en {MODEL_PATH}")
    return acc


if __name__ == "__main__":
    from dotenv import load_dotenv
    load_dotenv()
    train()
