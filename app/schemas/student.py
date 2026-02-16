# -*- coding: utf-8 -*-
"""
Schemas de validación Pydantic
"""
from pydantic import BaseModel, Field

class StudentCreate(BaseModel):
    nombre: str = Field(..., min_length=1, max_length=100)
    nota_media: float = Field(..., ge=0.0, le=10.0)
    asistencia: float = Field(..., ge=0.0, le=100.0)
    nota_practicas: float = Field(..., ge=0.0, le=10.0)
    ra_completados: int = Field(..., ge=0, le=20)
    participacion: int = Field(..., ge=0, le=10)

class PredictionResponse(BaseModel):
    student_id: int
    nombre: str
    probabilidad_aprobado: float
    riesgo: str
    aprobado_predicho: bool
