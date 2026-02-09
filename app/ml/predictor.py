# -*- coding: utf-8 -*-
"""
Servicio de predicción
"""
from app.ml.model import MinervaModel
from app.schemas.student import StudentCreate
import pandas as pd
import os


class PredictionService:
    def __init__(self):
        model_path = 'models/minerva_model.pkl'

        if os.path.exists(model_path):
            try:
                self.model = MinervaModel.load()
                print("Modelo ML cargado correctamente")
            except Exception as e:
                print(f"Error cargando modelo: {e}")
                self.model = None
        else:
            print("Modelo no encontrado")
            self.model = None

    def predict(self, student: StudentCreate):
        """Predice para un estudiante"""
        if self.model is None or not self.model.is_trained:
            raise ValueError("Modelo no entrenado")

        # Crear DataFrame (sin nombre, no se usa en features)
        df = pd.DataFrame([{
            'nota_media': float(student.nota_media),
            'asistencia': float(student.asistencia),
            'nota_practicas': float(student.nota_practicas),
            'ra_completados': int(student.ra_completados),
            'participacion': int(student.participacion)
        }])

        # Prediccion
        proba = self.model.predict_proba(df)[0]
        prob_aprobado = float(proba[1])

        # Calcular riesgo
        if prob_aprobado >= 0.75:
            riesgo = "Bajo"
        elif prob_aprobado >= 0.60:
            riesgo = "Medio"
        elif prob_aprobado >= 0.40:
            riesgo = "Alto"
        else:
            riesgo = "Critico"

        return {
            'probabilidad_aprobado': round(prob_aprobado, 4),
            'riesgo': riesgo,
            'aprobado_predicho': prob_aprobado >= 0.5
        }
