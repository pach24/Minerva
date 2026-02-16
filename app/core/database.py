# database.py
from sqlalchemy import create_engine
from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy.orm import sessionmaker

# Se creará un archivo 'minerva.db' en tu carpeta
SQLALCHEMY_DATABASE_URL = "sqlite:///./minerva.db"

# Argumento connect_args necesario solo para SQLite
engine = create_engine(
    SQLALCHEMY_DATABASE_URL, connect_args={"check_same_thread": False}
)
SessionLocal = sessionmaker(autocommit=False, autoflush=False, bind=engine)

Base = declarative_base()

# Dependencia para usar en main.py (te da una sesión de BD y la cierra al acabar)
def get_db():
    db = SessionLocal()
    try:
        yield db
    finally:
        db.close()
