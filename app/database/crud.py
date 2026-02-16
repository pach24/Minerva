"""
Operaciones CRUD - Capa de abstracción de datos
"""
from sqlalchemy.orm import Session
from app.database.models import Student, Prediction
from app.schemas.student import StudentCreate
from typing import List, Optional

def create_student(db: Session, student: StudentCreate) -> Student:
    """Crea nuevo estudiante en BD"""
    db_student = Student(**student.dict())
    db.add(db_student)
    db.commit()
    db.refresh(db_student)
    return db_student

def get_student(db: Session, student_id: int) -> Optional[Student]:
    """Obtiene estudiante por ID"""
    return db.query(Student).filter(Student.id == student_id).first()

def get_students(db: Session, skip: int = 0, limit: int = 100) -> List[Student]:
    """Lista estudiantes con paginación"""
    return db.query(Student).offset(skip).limit(limit).all()

def create_prediction(db: Session, student_id: int, proba: float, riesgo: str) -> Prediction:
    """Guarda predicción en BD"""
    prediction = Prediction(
        student_id=student_id,
        probabilidad_aprobado=proba,
        riesgo=riesgo,
        aprobado_predicho=(proba >= 0.5)
    )
    db.add(prediction)
    db.commit()
    db.refresh(prediction)
    return prediction

def get_latest_prediction(db: Session, student_id: int) -> Optional[Prediction]:
    """Obtiene última predicción de un estudiante"""
    return db.query(Prediction)\
        .filter(Prediction.student_id == student_id)\
        .order_by(Prediction.created_at.desc())\
        .first()
