"""
Genera datos sintéticos y entrena el modelo RandomForest + SGDClassifier.
Ejecutar: python ml/train.py
"""
import numpy as np
import pandas as pd
import pickle
import os
import json
from sklearn.ensemble import RandomForestClassifier
from sklearn.linear_model import SGDClassifier
from sklearn.preprocessing import StandardScaler
from sklearn.impute import SimpleImputer
from sklearn.model_selection import train_test_split, GridSearchCV, StratifiedKFold, cross_val_score
from sklearn.metrics import (
    accuracy_score, classification_report, confusion_matrix,
    cohen_kappa_score, precision_score, recall_score, f1_score,
)

RANDOM_STATE = int(os.getenv("RANDOM_STATE", 42))
N            = int(os.getenv("NUM_SYNTHETIC_STUDENTS", 1000))
TEST_SIZE    = float(os.getenv("TEST_SIZE", 0.2))
MODEL_PATH   = os.getenv("MODEL_PATH", "models/minerva_model.pkl")
METRICS_PATH = os.getenv("METRICS_PATH", "model_metrics.json")

FEATURES = [
    # 9 notas RA
    "nota_ra1", "nota_ra2", "nota_ra3", "nota_ra4", "nota_ra5",
    "nota_ra6", "nota_ra7", "nota_ra8", "nota_ra9",
    # 3 features académicas base
    "ratio_asistencia", "notas_practicas", "feedback_score",
    # 6 features derivadas
    "nota_media", "pct_ras_superados", "tendencia_progreso",
    "media_ras_teoricos", "media_ras_practicos", "gap_teoria_practica",
    # 1 bloqueante
    "ra1_bloqueante",
    # 4 opcionales (pueden ser NaN)
    "confianza", "motivacion_escolar", "recursos_casa", "nivel_educativo_familia",
]


def _derived(ra: np.ndarray) -> dict:
    """Calcula las 7 features derivadas a partir de la matriz de 9 RAs (n × 9)."""
    nota_media         = ra.mean(axis=1)
    pct_ras_superados  = (ra >= 5).sum(axis=1) / 9
    tendencia_progreso = ra[:, 6:9].mean(axis=1) - ra[:, 0:3].mean(axis=1)
    media_ras_teoricos  = ra[:, [0, 1, 3, 6]].mean(axis=1)   # RA1,2,4,7
    media_ras_practicos = ra[:, [2, 4, 5, 7, 8]].mean(axis=1) # RA3,5,6,8,9
    gap_teoria_practica = media_ras_teoricos - media_ras_practicos
    ra1_bloqueante      = (ra[:, 0] < 5).astype(float)
    return {
        "nota_media":          nota_media,
        "pct_ras_superados":   pct_ras_superados,
        "tendencia_progreso":  tendencia_progreso,
        "media_ras_teoricos":  media_ras_teoricos,
        "media_ras_practicos": media_ras_practicos,
        "gap_teoria_practica": gap_teoria_practica,
        "ra1_bloqueante":      ra1_bloqueante,
    }


def generate_data(n: int, seed: int) -> pd.DataFrame:
    rng = np.random.default_rng(seed)

    # Base RAs con correlaciones: RAs avanzados correlacionan con básicos
    ra_base = rng.uniform(0, 10, n)
    ra = np.column_stack([
        np.clip(ra_base + rng.normal(0, 1.5, n), 0, 10),       # RA1
        np.clip(ra_base + rng.normal(0.2, 1.5, n), 0, 10),     # RA2
        np.clip(ra_base + rng.normal(-0.3, 2.0, n), 0, 10),    # RA3
        np.clip(ra_base + rng.normal(0.1, 1.8, n), 0, 10),     # RA4
        np.clip(ra_base + rng.normal(-0.5, 2.0, n), 0, 10),    # RA5
        np.clip(ra_base + rng.normal(-0.2, 1.8, n), 0, 10),    # RA6
        np.clip(ra_base + rng.normal(0.3, 2.0, n), 0, 10),     # RA7
        np.clip(ra_base + rng.normal(-0.1, 2.0, n), 0, 10),    # RA8
        np.clip(ra_base + rng.normal(0.4, 1.9, n), 0, 10),     # RA9
    ])

    ratio_asistencia = rng.uniform(0.40, 1.0, n)

    d = _derived(ra)
    notas_practicas = np.clip(d["media_ras_practicos"] + rng.normal(0, 1, n), 0, 10)
    feedback_score  = rng.uniform(0, 1, n)

    # Opcionales: ~30% NaN (alumnos sin cuestionario)
    def optional_with_nans(vals):
        mask = rng.random(n) < 0.30
        out = vals.astype(float)
        out[mask] = np.nan
        return out

    confianza               = optional_with_nans(rng.integers(0, 3, n).astype(float))
    motivacion_escolar      = optional_with_nans(rng.integers(1, 5, n).astype(float))
    recursos_casa           = optional_with_nans(rng.integers(0, 2, n).astype(float))
    nivel_educativo_familia = optional_with_nans(rng.integers(1, 7, n).astype(float))

    # Etiqueta: normativa FP
    aprobado = (
        (d["nota_media"] >= 5.0) &
        (ratio_asistencia >= 0.75) &
        (d["pct_ras_superados"] >= 0.60)
    ).astype(int)

    df = pd.DataFrame({
        "nota_ra1": ra[:, 0], "nota_ra2": ra[:, 1], "nota_ra3": ra[:, 2],
        "nota_ra4": ra[:, 3], "nota_ra5": ra[:, 4], "nota_ra6": ra[:, 5],
        "nota_ra7": ra[:, 6], "nota_ra8": ra[:, 7], "nota_ra9": ra[:, 8],
        "ratio_asistencia": ratio_asistencia,
        "notas_practicas":  notas_practicas,
        "feedback_score":   feedback_score,
        **d,
        "confianza":               confianza,
        "motivacion_escolar":      motivacion_escolar,
        "recursos_casa":           recursos_casa,
        "nivel_educativo_familia": nivel_educativo_familia,
        "aprobado": aprobado,
    })
    return df


def train() -> float:
    df = generate_data(N, RANDOM_STATE)

    X = df[FEATURES]
    y = df["aprobado"]

    X_train, X_test, y_train, y_test = train_test_split(
        X, y, test_size=TEST_SIZE, random_state=RANDOM_STATE, stratify=y
    )

    imputer = SimpleImputer(strategy="median")
    X_train_imp = imputer.fit_transform(X_train)
    X_test_imp  = imputer.transform(X_test)

    scaler = StandardScaler()
    X_train_sc = scaler.fit_transform(X_train_imp)
    X_test_sc  = scaler.transform(X_test_imp)

    # SMOTE si clase minoritaria < 40%
    minority_ratio = y_train.value_counts(normalize=True).min()
    X_train_fit = X_train_sc
    y_train_fit = y_train
    if minority_ratio < 0.40:
        try:
            from imblearn.over_sampling import SMOTE
            sm = SMOTE(random_state=RANDOM_STATE)
            X_train_fit, y_train_fit = sm.fit_resample(X_train_sc, y_train)
            print(f"SMOTE aplicado (clase minoritaria era {minority_ratio:.1%})")
        except ImportError:
            print("imbalanced-learn no disponible, se omite SMOTE")

    # GridSearchCV sobre RandomForest
    param_grid = {
        "n_estimators":    [50, 100, 200],
        "max_depth":       [None, 5, 10, 15],
        "min_samples_split": [2, 5, 10],
        "class_weight":    ["balanced", None],
    }
    rf_base = RandomForestClassifier(random_state=RANDOM_STATE)
    cv_inner = StratifiedKFold(n_splits=5, shuffle=True, random_state=RANDOM_STATE)
    gs = GridSearchCV(rf_base, param_grid, cv=cv_inner, scoring="f1", n_jobs=-1, verbose=0)
    gs.fit(X_train_fit, y_train_fit)
    clf_rf = gs.best_estimator_

    # SGDClassifier para partial_fit incremental
    clf_sgd = SGDClassifier(loss="log_loss", max_iter=1000, random_state=RANDOM_STATE)
    clf_sgd.fit(X_train_fit, y_train_fit)

    # ── Evaluación ───────────────────────────────────────────────────────────
    y_pred_train = clf_rf.predict(X_train_sc)
    y_pred_test  = clf_rf.predict(X_test_sc)

    acc_train = accuracy_score(y_train, y_pred_train)
    acc_test  = accuracy_score(y_test, y_pred_test)
    acc_diff  = abs(acc_train - acc_test)

    prec  = precision_score(y_test, y_pred_test, zero_division=0)
    rec   = recall_score(y_test, y_pred_test, zero_division=0)
    f1    = f1_score(y_test, y_pred_test, zero_division=0)
    kappa = cohen_kappa_score(y_test, y_pred_test)
    cm    = confusion_matrix(y_test, y_pred_test)

    target_names = ["Suspende (0)", "Aprueba (1)"]
    report = classification_report(y_test, y_pred_test, target_names=target_names, zero_division=0, output_dict=True)

    # Cross-validation F1 en test split
    cv_outer = StratifiedKFold(n_splits=5, shuffle=True, random_state=RANDOM_STATE)
    cv_scores = cross_val_score(clf_rf, scaler.transform(imputer.transform(X)), y, cv=cv_outer, scoring="f1")

    print("=" * 60)
    print("              EVALUACIÓN DEL MODELO")
    print("=" * 60)
    print(f"\n--- Accuracy ---")
    print(f"  Train:      {acc_train:.4f}")
    print(f"  Test:       {acc_test:.4f}")
    print(f"  Diferencia: {acc_diff:.4f}")
    print(f"\n--- Métricas Test ---")
    print(f"  Precision:  {prec:.4f}")
    print(f"  Recall:     {rec:.4f}")
    print(f"  F1-score:   {f1:.4f}")
    print(f"  Kappa:      {kappa:.4f}")
    print(f"\n--- Classification Report ---")
    print(classification_report(y_test, y_pred_test, target_names=target_names, zero_division=0))
    print(f"--- Matriz de Confusión ---")
    print(f"{'':>18} Pred:Susp  Pred:Aprob")
    print(f"  Real: Suspende   {cm[0][0]:>5}       {cm[0][1]:>5}")
    print(f"  Real: Aprueba    {cm[1][0]:>5}       {cm[1][1]:>5}")
    print(f"\n--- Cross-Validation F1 (k=5) ---")
    print(f"  Scores: {[round(s, 4) for s in cv_scores]}")
    print(f"  Media:  {cv_scores.mean():.4f}  ±  {cv_scores.std():.4f}")
    print(f"\n--- Mejores Hiperparámetros ---")
    print(f"  {gs.best_params_}")

    recall_min = min(report["Suspende (0)"]["recall"], report["Aprueba (1)"]["recall"])
    f1_min     = min(report["Suspende (0)"]["f1-score"], report["Aprueba (1)"]["f1-score"])

    MIN_ACC    = float(os.getenv("MIN_ACCURACY", 0.80))
    MIN_RECALL = 0.75
    MIN_F1     = 0.75
    MAX_DIFF   = 0.05

    checks = [
        (f"Accuracy  >= {MIN_ACC:.2f}",           acc_test   >= MIN_ACC),
        (f"Recall    >= {MIN_RECALL:.2f} (min)", recall_min >= MIN_RECALL),
        (f"F1-score  >= {MIN_F1:.2f} (min)",     f1_min     >= MIN_F1),
        (f"Diff T/T  <  {MAX_DIFF:.2f} (overfit)", acc_diff  <  MAX_DIFF),
    ]

    print(f"\n--- Validación de Umbrales ---")
    all_pass = True
    for label, passed in checks:
        tag = "PASS" if passed else "FAIL"
        print(f"  [{tag}] {label}")
        if not passed:
            all_pass = False

    verdict = "MODELO APTO" if all_pass else "MODELO NO APTO"
    print(f"\n>>> VEREDICTO: {verdict} <<<")
    print("=" * 60)

    # Feature importances
    feat_imp = {
        feat: round(float(imp), 6)
        for feat, imp in zip(FEATURES, clf_rf.feature_importances_)
    }

    # Guardar métricas
    metrics = {
        "accuracy":          round(float(acc_test), 4),
        "precision":         round(float(prec), 4),
        "recall":            round(float(rec), 4),
        "f1_score":          round(float(f1), 4),
        "kappa":             round(float(kappa), 4),
        "confusion_matrix":  cm.tolist(),
        "feature_importances": feat_imp,
        "best_params":       gs.best_params_,
        "cv_f1_mean":        round(float(cv_scores.mean()), 4),
        "cv_f1_std":         round(float(cv_scores.std()), 4),
        "advertencia": (
            "Métricas calculadas sobre datos sintéticos. "
            "No representan precisión real sobre alumnos."
        ),
    }
    with open(METRICS_PATH, "w", encoding="utf-8") as f:
        json.dump(metrics, f, ensure_ascii=False, indent=2)
    print(f"Métricas guardadas en {METRICS_PATH}")

    os.makedirs(os.path.dirname(MODEL_PATH), exist_ok=True)
    with open(MODEL_PATH, "wb") as f:
        pickle.dump({
            "imputer":  imputer,
            "scaler":   scaler,
            "clf_rf":   clf_rf,
            "clf_sgd":  clf_sgd,
            "features": FEATURES,
        }, f)
    print(f"Modelo guardado en {MODEL_PATH}")
    return float(acc_test)


if __name__ == "__main__":
    from dotenv import load_dotenv
    load_dotenv()
    train()
