"""
Generador de datos sintéticos educativos
"""
import numpy as np
import pandas as pd
from faker import Faker
from app.config.settings import settings


class SyntheticDataGenerator:
    """Genera datos sintéticos correlacionados de estudiantes"""

    def __init__(self, num_students: int = None):
        self.num_students = num_students or settings.NUM_SYNTHETIC_STUDENTS
        self.fake = Faker('es_ES')
        np.random.seed(settings.RANDOM_STATE)

    def generate(self) -> pd.DataFrame:
        """
        Genera dataset sintético con correlaciones realistas

        Returns:
            DataFrame con estudiantes sintéticos
        """
        students = []

        for _ in range(self.num_students):
            # Nota media base (distribución normal)
            nota_base = np.random.normal(6.0, 2.0)
            nota_base = max(0, min(10, nota_base))

            # Asistencia correlacionada con nota (r ≈ 0.65)
            asistencia = nota_base * 10 + np.random.normal(0, 15)
            asistencia = max(0, min(100, asistencia))

            # RAs completados correlacionado con nota (r ≈ 0.75)
            ra_completados = int(nota_base * 2 + np.random.normal(0, 3))
            ra_completados = max(0, min(20, ra_completados))

            # Nota prácticas similar a nota media (r ≈ 0.80)
            nota_practicas = nota_base + np.random.normal(0, 1)
            nota_practicas = max(0, min(10, nota_practicas))

            # Participación aleatoria con sesgo positivo
            participacion = np.random.randint(3, 11)

            # Variable objetivo: aprobado si nota >= 5
            aprobado = 1 if nota_base >= 5.0 else 0

            student = {
                'nombre': self.fake.name(),
                'nota_media': round(nota_base, 2),
                'asistencia': round(asistencia, 2),
                'nota_practicas': round(nota_practicas, 2),
                'ra_completados': ra_completados,
                'participacion': participacion,
                'aprobado': aprobado
            }
            students.append(student)

        df = pd.DataFrame(students)

        # Guardar dataset
        df.to_csv("data/synthetic_students.csv", index=False)
        print(f"✅ Generados {len(df)} estudiantes sintéticos")
        print(f"   - Aprobados: {df['aprobado'].sum()} ({df['aprobado'].mean():.1%})")

        return df
