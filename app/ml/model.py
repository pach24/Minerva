"""
Modelo de Machine Learning
"""
import joblib
import numpy as np
import pandas as pd
from sklearn.linear_model import SGDClassifier
from sklearn.model_selection import train_test_split, cross_val_score
from sklearn.preprocessing import StandardScaler
import os


class MinervaModel:
    def __init__(self):
        self.model = SGDClassifier(
            loss='log_loss',
            max_iter=1000,
            random_state=42,
            warm_start=True
        )
        self.scaler = StandardScaler()
        self.is_trained = False

    def _prepare_features(self, df: pd.DataFrame, fit: bool = False):
        """Prepara features"""
        X = df[['nota_media', 'asistencia', 'nota_practicas',
                'ra_completados', 'participacion']].values

        if fit:
            X = self.scaler.fit_transform(X)
        else:
            X = self.scaler.transform(X)

        return X

    def train(self, df: pd.DataFrame):
        """Entrena el modelo"""
        X = self._prepare_features(df, fit=True)
        y = df['aprobado'].values

        X_train, X_test, y_train, y_test = train_test_split(
            X, y, test_size=0.2, random_state=42, stratify=y
        )

        self.model.fit(X_train, y_train)
        self.is_trained = True

        train_acc = self.model.score(X_train, y_train)
        test_acc = self.model.score(X_test, y_test)

        # Cross-validation
        cv_scores = cross_val_score(self.model, X, y, cv=5)

        self.save()

        metrics = {
            'train_accuracy': float(train_acc),
            'test_accuracy': float(test_acc),
            'cv_mean': float(cv_scores.mean()),
            'cv_std': float(cv_scores.std()),
            'n_samples': len(df),
            'n_features': X.shape[1]
        }

        print(f"\n✅ Modelo entrenado:")
        print(f"   - Train accuracy: {train_acc * 100:.2f}%")
        print(f"   - Test accuracy: {test_acc * 100:.2f}%")
        print(f"   - CV mean: {cv_scores.mean() * 100:.2f}% (±{cv_scores.std() * 100:.2f}%)")

        return metrics

    def predict_proba(self, df: pd.DataFrame):
        """Predice probabilidades"""
        X = self._prepare_features(df, fit=False)
        return self.model.predict_proba(X)

    def save(self):
        """Guarda el modelo"""
        os.makedirs('models', exist_ok=True)
        joblib.dump({
            'model': self.model,
            'scaler': self.scaler,
            'is_trained': self.is_trained
        }, 'models/minerva_model.pkl')

    @classmethod
    def load(cls):
        """Carga el modelo"""
        instance = cls()
        data = joblib.load('models/minerva_model.pkl')
        instance.model = data['model']
        instance.scaler = data['scaler']
        instance.is_trained = data.get('is_trained', True)
        return instance