POSITIVOS = [
    "esfuerza", "trabaja", "participa", "mejora", "atento",
    "responsable", "bueno", "excelente", "motivado", "constante",
    "puntual", "interesado", "comprometido", "avanza", "progresa",
]

NEGATIVOS = [
    "distraído", "distrae", "no entrega", "falta", "problemas",
    "suspende", "abandona", "desmotivado", "pasota", "desinterés",
    "irregular", "ausente", "descuida", "retraso", "conflicto",
]


def texto_a_score(texto: str) -> float:
    """Texto libre del profesor → score 0.0–1.0. Sin texto → 0.5 (neutro)."""
    if not texto or not texto.strip():
        return 0.5
    t = texto.lower()
    pos = sum(1 for k in POSITIVOS if k in t)
    neg = sum(1 for k in NEGATIVOS if k in t)
    if pos + neg == 0:
        return 0.5
    return round(pos / (pos + neg), 2)
