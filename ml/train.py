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
from sklearn.model_selection import train_test_split
from sklearn.metrics import accuracy_score

RANDOM_STATE = int(os.getenv("RANDOM_STATE", 42))
N            = int(os.getenv("NUM_SYNTHETIC_STUDENTS", 1000))
TEST_SIZE    = float(os.getenv("TEST_SIZE", 0.2))
MODEL_PATH   = os.getenv("MODEL_PATH", "models/minerva_model.pkl")

PART_MAP = {"Alta": 1.0, "Media": 0.67, "Baja": 0.33, "Nula": 0.0}

FEATURES = [
    "asistencia", "nota_media", "participacion_score", "nota_practicas",
    "ra_herencia", "ra_polimorfismo", "ra_ficheros", "ra_interfaces",
    "feedback_score",
]


def generate_data(n: int, seed: int) -> pd.DataFrame:
    rng = np.random.default_rng(seed)

    asistencia      = rng.uniform(10, 100, n)
    nota_media      = rng.uniform(1, 10, n)
    part_score      = rng.choice(list(PART_MAP.values()), n, p=[0.25, 0.40, 0.25, 0.10])
    nota_practicas  = rng.uniform(0, 10, n)
    ra_herencia     = rng.uniform(0, 10, n)
    ra_polimorfismo = rng.uniform(0, 10, n)
    ra_ficheros     = rng.uniform(0, 10, n)
    ra_interfaces   = rng.uniform(0, 10, n)
    feedback_score  = rng.uniform(0, 1, n)

    ra_media = (ra_herencia + ra_polimorfismo + ra_ficheros + ra_interfaces) / 4

    score = (
        asistencia / 100    * 0.25 +
        nota_media / 10     * 0.30 +
        part_score          * 0.08 +
        nota_practicas / 10 * 0.12 +
        ra_media / 10       * 0.15 +
        feedback_score      * 0.10
    )
    noise    = rng.normal(0, 0.05, n)
    aprobado = ((score + noise) >= 0.50).astype(int)

    return pd.DataFrame({
        "asistencia":        asistencia,
        "nota_media":        nota_media,
        "participacion_score": part_score,
        "nota_practicas":    nota_practicas,
        "ra_herencia":       ra_herencia,
        "ra_polimorfismo":   ra_polimorfismo,
        "ra_ficheros":       ra_ficheros,
        "ra_interfaces":     ra_interfaces,
        "feedback_score":    feedback_score,
        "aprobado":          aprobado,
    })


def train() -> float:
    df = generate_data(N, RANDOM_STATE)

    X = df[FEATURES]
    y = df["aprobado"]

    X_train, X_test, y_train, y_test = train_test_split(
        X, y, test_size=TEST_SIZE, random_state=RANDOM_STATE
    )

    scaler = StandardScaler()
    X_train_sc = scaler.fit_transform(X_train)
    X_test_sc  = scaler.transform(X_test)

    clf = SGDClassifier(loss="log_loss", max_iter=1000, random_state=RANDOM_STATE)
    clf.fit(X_train_sc, y_train)

    acc = accuracy_score(y_test, clf.predict(X_test_sc))
    print(f"Accuracy: {acc:.3f} ({int(acc * 100)}%)")

    os.makedirs(os.path.dirname(MODEL_PATH), exist_ok=True)
    with open(MODEL_PATH, "wb") as f:
        pickle.dump({"scaler": scaler, "clf": clf, "features": FEATURES}, f)
    print(f"Modelo guardado en {MODEL_PATH}")
    return acc


if __name__ == "__main__":
    from dotenv import load_dotenv
    load_dotenv()
    train()
