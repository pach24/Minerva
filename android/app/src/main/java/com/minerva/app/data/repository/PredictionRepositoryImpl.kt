package com.minerva.app.data.repository

import com.minerva.app.core.Result
import com.minerva.app.data.remote.MinervaApi
import com.minerva.app.data.remote.dto.StudentRequestDto
import com.minerva.app.data.remote.dto.toDomain
import com.minerva.app.domain.model.Prediction
import com.minerva.app.domain.model.Student
import com.minerva.app.domain.repository.PredictionRepository
import retrofit2.HttpException
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
            if (e.code() == 401)
                Result.Error("Sesión caducada. Por favor inicia sesión de nuevo.", e)
            else
                Result.Error("Error del servidor: ${e.code()}", e)
        } catch (e: Exception) {
            Result.Error("Error de red: ${e.message}", e)
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
