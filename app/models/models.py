from sqlalchemy import Column, Integer, String, Float, ForeignKey, DateTime
from sqlalchemy.orm import relationship
from datetime import datetime

# OJO AQUÍ: La ruta de importación ha cambiado
from app.core.database import Base


class Alumno(Base):
    __tablename__ = "alumnos"

    id = Column(Integer, primary_key=True, index=True)
    nombre = Column(String, index=True)
    edad = Column(Integer)
    curso = Column(String)
    # Ejemplo de campos extra según tu PDF
    nota_media = Column(Float, nullable=True)
    faltas_asistencia = Column(Integer, default=0)

    # Relación bidireccional
    predicciones = relationship("Prediccion", back_populates="alumno")


class Prediccion(Base):
    __tablename__ = "predicciones"

    id = Column(Integer, primary_key=True, index=True)
    alumno_id = Column(Integer, ForeignKey("alumnos.id"))
    probabilidad_aprobado = Column(Float)
    fecha_prediccion = Column(DateTime, default=datetime.utcnow)

    # Relación inversa
    alumno = relationship("Alumno", back_populates="predicciones")
