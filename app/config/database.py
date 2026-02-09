"""
Configuración de PostgreSQL con SQLAlchemy
"""
from sqlalchemy import create_engine
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy.orm import sessionmaker

from app.config.settings import settings

engine = create_engine(settings.DATABASE_URL, pool_pre_ping=True)
SessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)
Base = declarative_base()
DATABASE_URL: str = "postgresql://postgres:tu_contraseña@localhost:5432/minerva_db"


def get_db():
    """Dependency para inyectar sesión de BD"""

    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()
