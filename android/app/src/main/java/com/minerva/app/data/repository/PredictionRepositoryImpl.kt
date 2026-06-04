package com.minerva.app.data.repository

import com.minerva.app.core.Result
import com.minerva.app.data.remote.MinervaApi
import com.minerva.app.data.remote.dto.StudentRequestDto
import com.minerva.app.data.remote.dto.toDomain
import com.minerva.app.domain.model.Prediction
import com.minerva.app.domain.model.Student
import com.minerva.app.domain.repository.PredictionRepository
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class PredictionRepositoryImpl @Inject constructor(
    private val minervaApi: MinervaApi
) : PredictionRepository {

    override suspend fun predict(students: List<Student>): Result<List<Prediction>> {
        return try {
            val dtos = students.map { it.toRequestDto() }
            val results = minervaApi.predecir(dtos)
            Result.Success(results.map { it.toDomain() })
        } catch (e: HttpException) {
            when (e.code()) {
                401 -> Result.Error("Tu sesión ha caducado. Inicia sesión de nuevo.", e)
                in 500..599 -> Result.Error(
                    "El servidor no está disponible ahora mismo. Inténtalo de nuevo en unos segundos.", e
                )
                else -> Result.Error("No se pudo completar la predicción (código ${e.code()}).", e)
            }
        } catch (e: IOException) {
            Result.Error("Sin conexión con el servidor. Comprueba tu red e inténtalo de nuevo.", e)
        } catch (e: Exception) {
            Result.Error("Ha ocurrido un error inesperado al predecir.", e)
        }
    }

    private fun Student.toRequestDto() = StudentRequestDto(
        id = id,
        nombre = nombre,
        asistencia = asistencia,
        ratio_asistencia = ratioAsistencia,
        nota_media = notaMedia,
        participacion = participacion,
        nota_practicas = notaPracticas,
        nota_ra1 = notaRa1,
        nota_ra2 = notaRa2,
        nota_ra3 = notaRa3,
        nota_ra4 = notaRa4,
        nota_ra5 = notaRa5,
        nota_ra6 = notaRa6,
        nota_ra7 = notaRa7,
        nota_ra8 = notaRa8,
        nota_ra9 = notaRa9,
        confianza = confianza,
        motivacion_escolar = motivacionEscolar,
        recursos_casa = recursosCasa,
        nivel_educativo_familia = nivelEducativoFamilia,
        feedback_profesor = feedbackProfesor
    )
}
