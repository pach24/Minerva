# -*- coding: utf-8 -*-
"""
Router de predicciones
"""
from fastapi import APIRouter, HTTPException, File, UploadFile
from app.schemas.student import StudentCreate
from app.ml.predictor import PredictionService
import pandas as pd
import io

router = APIRouter(prefix="/api", tags=["predictions"])

# Instanciar predictor
try:
    predictor = PredictionService()
except Exception as e:
    print(f"Error al inicializar PredictionService: {e}")
    predictor = None


@router.post("/predict")
async def predict_student(student: StudentCreate):
    """Predice para UN estudiante"""
    if predictor is None:
        raise HTTPException(
            status_code=503,
            detail="Servicio no disponible"
        )

    try:
        prediction = predictor.predict(student)

        return {
            "student_id": 0,
            "nombre": student.nombre,
            **prediction
        }

    except ValueError as e:
        raise HTTPException(status_code=400, detail=str(e))
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))


@router.post("/predict/batch")
async def predict_batch_csv(file: UploadFile = File(...)):
    """Predice para MULTIPLES estudiantes desde CSV"""
    if predictor is None:
        raise HTTPException(status_code=503, detail="Servicio no disponible")

    try:
        contents = await file.read()
        df = pd.read_csv(io.StringIO(contents.decode('utf-8')))

        required_cols = ['nombre', 'nota_media', 'asistencia', 'nota_practicas',
                         'ra_completados', 'participacion']
        missing_cols = [col for col in required_cols if col not in df.columns]

        if missing_cols:
            raise HTTPException(
                status_code=400,
                detail=f"Faltan columnas: {', '.join(missing_cols)}"
            )

        results = []
        for idx, row in df.iterrows():
            student = StudentCreate(
                nombre=str(row['nombre']),
                nota_media=float(row['nota_media']),
                asistencia=float(row['asistencia']),
                nota_practicas=float(row['nota_practicas']),
                ra_completados=int(row['ra_completados']),
                participacion=int(row['participacion'])
            )

            prediction = predictor.predict(student)

            results.append({
                "id": idx + 1,
                "nombre": student.nombre,
                "nota_media": student.nota_media,
                "asistencia": student.asistencia,
                "probabilidad_aprobado": prediction['probabilidad_aprobado'],
                "riesgo": prediction['riesgo'],
                "aprobado_predicho": prediction['aprobado_predicho']
            })

        return {
            "total_students": len(results),
            "predictions": results,
            "summary": {
                "bajo_riesgo": sum(1 for r in results if r['riesgo'] == 'Bajo'),
                "medio_riesgo": sum(1 for r in results if r['riesgo'] == 'Medio'),
                "alto_riesgo": sum(1 for r in results if r['riesgo'] == 'Alto'),
                "critico_riesgo": sum(1 for r in results if r['riesgo'] == 'Critico')
            }
        }

    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))


@router.get("/health")
async def health_check():
    """Health check"""
    return {
        "status": "ok",
        "model_loaded": predictor is not None and predictor.model is not None
    }
