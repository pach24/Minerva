"""
Router de administración - Entrenamiento del modelo
"""
from fastapi import APIRouter, BackgroundTasks, HTTPException
from app.ml.data_generator import SyntheticDataGenerator
from app.ml.model import MinervaModel
import os

router = APIRouter(prefix="/api/admin", tags=["admin"])

# Estado global del entrenamiento
training_status = {
    "is_training": False,
    "progress": 0,
    "message": "No iniciado",
    "metrics": None
}


def train_model_task():
    """
    Tarea de entrenamiento en background
    """
    global training_status

    try:
        training_status["is_training"] = True
        training_status["progress"] = 10
        training_status["message"] = "Generando datos sintéticos..."

        # 1. Generar datos sintéticos
        generator = SyntheticDataGenerator(num_students=1000)
        df = generator.generate()

        training_status["progress"] = 50
        training_status["message"] = "Entrenando modelo ML..."

        # 2. Entrenar modelo
        model = MinervaModel()
        metrics = model.train(df)

        training_status["progress"] = 100
        training_status["message"] = "Entrenamiento completado ✅"
        training_status["metrics"] = metrics
        training_status["is_training"] = False

        print("\n✅ Entrenamiento completado desde API")

    except Exception as e:
        training_status["is_training"] = False
        training_status["message"] = f"Error: {str(e)}"
        training_status["progress"] = 0
        print(f"❌ Error en entrenamiento: {e}")


@router.post("/train")
async def train_model(background_tasks: BackgroundTasks):
    """
    🧠 Entrena el modelo de ML

    Genera 1000 estudiantes sintéticos y entrena el modelo SGDClassifier
    """
    global training_status

    if training_status["is_training"]:
        raise HTTPException(
            status_code=409,
            detail="Entrenamiento ya en progreso. Espera a que termine."
        )

    # Reiniciar estado
    training_status = {
        "is_training": True,
        "progress": 0,
        "message": "Iniciando entrenamiento...",
        "metrics": None
    }

    # Ejecutar en background para no bloquear
    background_tasks.add_task(train_model_task)

    return {
        "status": "started",
        "message": "Entrenamiento iniciado en segundo plano"
    }


@router.get("/train/status")
async def get_training_status():
    """
    📊 Obtiene el estado actual del entrenamiento
    """
    return training_status


@router.get("/model/info")
async def get_model_info():
    """
    ℹ️ Información del modelo actual
    """
    model_path = "models/minerva_model.pkl"

    if not os.path.exists(model_path):
        return {
            "exists": False,
            "message": "Modelo no entrenado. Usa POST /api/admin/train"
        }

    try:
        from app.ml.model import MinervaModel
        model = MinervaModel.load()
        file_size = os.path.getsize(model_path) / 1024  # KB

        return {
            "exists": True,
            "is_trained": model.is_trained,
            "file_size_kb": round(file_size, 2),
            "path": model_path
        }
    except Exception as e:
        return {
            "exists": True,
            "error": str(e)
        }


@router.post("/data/generate")
async def generate_synthetic_data(num_students: int = 1000):
    """
    📊 Genera solo datos sintéticos sin entrenar

    Args:
        num_students: Número de estudiantes a generar (default: 1000)
    """
    try:
        generator = SyntheticDataGenerator(num_students=num_students)
        df = generator.generate()

        return {
            "status": "success",
            "students_generated": len(df),
            "approved_ratio": f"{df['aprobado'].mean():.1%}",
            "file": "data/synthetic_students.csv"
        }
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))
