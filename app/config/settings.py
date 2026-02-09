"""
Configuración centralizada
"""
from pydantic_settings import BaseSettings
from functools import lru_cache


class Settings(BaseSettings):
    # ✅ Usar el usuario "admin" de tu pgAdmin
    DATABASE_URL: str = "postgresql://admin:latararallevaunvestidoblancollenodecascabeles@localhost:5432/minerva_db"

    SECRET_KEY: str = "latararallevaunvestidoblancollenodecascabeles"
    MODEL_PATH: str = "models/minerva_model.pkl"
    MIN_ACCURACY: float = 0.80
    TEST_SIZE: float = 0.2
    RANDOM_STATE: int = 42
    NUM_SYNTHETIC_STUDENTS: int = 1000

    class Config:
        env_file = ".env"
        extra = "ignore"


@lru_cache()
def get_settings():
    return Settings()


settings = get_settings()
