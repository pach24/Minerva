"""
Modelos SQLAlchemy - Representación de tablas en BD
"""
from sqlalchemy import Column, Integer, String, Float, DateTime, Boolean
from sqlalchemy.sql import func
from app.config.database import Base


class Student(Base):
    __tablename__ = "students"

    id = Column(Integer, primary_key=True, index=True)
    nombre = Column(String, nullable=False)
    nota_media = Column(Float, nullable=False)
    asistencia = Column(Float, nullable=False)
    nota_practicas = Column(Float, nullable=False)
    ra_completados = Column(Integer, nullable=False)
    participacion = Column(Integer, nullable=False)
    created_at = Column(DateTime(timezone=True), server_default=func.now())
    updated_at = Column(DateTime(timezone=True), onupdate=func.now())


class Prediction(Base):
    __tablename__ = "predictions"

    id = Column(Integer, primary_key=True, index=True)
    student_id = Column(Integer, nullable=False)
    probabilidad_aprobado = Column(Float, nullable=False)
    riesgo = Column(String, nullable=False)
    aprobado_predicho = Column(Boolean, nullable=False)
    created_at = Column(DateTime(timezone=True), server_default=func.now())
