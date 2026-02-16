"""
Ingeniería de características - Transformación de features
"""
import pandas as pd
import numpy as np
from sklearn.preprocessing import StandardScaler
from typing import Tuple


class FeatureEngineer:
    """Transforma datos crudos en features optimizadas"""

    def __init__(self):
        self.scaler = StandardScaler()
        self.is_fitted = False

    def transform(self, df: pd.DataFrame, fit: bool = False) -> np.ndarray:
        """
        Aplica feature engineering

        Args:
            df: DataFrame con datos de estudiantes
            fit: Si True, ajusta scaler (solo en training)

        Returns:
            Array numpy con features normalizadas
        """
        df = df.copy()

        # Features derivadas
        df['ratio_ra'] = df['ra_completados'] / 20.0
        df['asistencia_norm'] = df['asistencia'] / 100.0
        df['engagement'] = (df['participacion'] + df['asistencia_norm'] * 10) / 2

        # Seleccionar features finales
        feature_cols = [
            'nota_media',
            'asistencia_norm',
            'nota_practicas',
            'ratio_ra',
            'participacion',
            'engagement'
        ]

        X = df[feature_cols].values

        # Normalizar
        if fit:
            X = self.scaler.fit_transform(X)
            self.is_fitted = True
        elif self.is_fitted:
            X = self.scaler.transform(X)
        else:
            raise ValueError("Scaler no ajustado. Usa fit=True primero.")

        return X

    def get_feature_names(self) -> list:
        """Retorna nombres de features"""
        return [
            'nota_media',
            'asistencia_norm',
            'nota_practicas',
            'ratio_ra',
            'participacion',
            'engagement'
        ]
