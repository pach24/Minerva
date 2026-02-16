"""
Script para entrenar el modelo ML
Ejecutar: python scripts/train_model.py
"""
import sys
import os

sys.path.append(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))

from app.ml.data_generator import SyntheticDataGenerator
from app.ml.model import MinervaModel


def main():
    print("🚀 Iniciando entrenamiento del modelo Minerva...\n")

    # 1. Generar datos sintéticos
    print("📊 Generando datos sintéticos...")
    generator = SyntheticDataGenerator(num_students=1000)
    df = generator.generate()

    # 2. Entrenar modelo
    print("\n🧠 Entrenando modelo de Machine Learning...")
    model = MinervaModel()
    metrics = model.train(df)

    # 3. Mostrar resultados
    print("\n" + "=" * 50)
    print("✅ ENTRENAMIENTO COMPLETADO")
    print("=" * 50)
    print(f"Precisión en test: {metrics['test_accuracy']:.2%}")
    print(f"Cross-validation: {metrics['cv_mean']:.2%} (±{metrics['cv_std']:.2%})")
    print(f"Muestras de entrenamiento: {metrics['n_samples']}")
    print(f"Features utilizadas: {metrics['n_features']}")
    print(f"\n💾 Modelo guardado en: models/minerva_model.pkl")
    print("\n🎯 Ahora puedes usar el endpoint /api/predict")


if __name__ == "__main__":
    main()
