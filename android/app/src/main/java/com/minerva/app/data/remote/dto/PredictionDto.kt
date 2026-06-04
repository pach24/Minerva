package com.minerva.app.data.remote.dto

import com.minerva.app.domain.model.Prediction
import com.minerva.app.domain.model.RiskLevel
import com.minerva.app.domain.model.Student
import kotlinx.serialization.Serializable

@Serializable
data class PredictionDto(
    val id: String = "",
    val nombre: String = "",
    val asistencia: Double = 0.0,
    val ratio_asistencia: Double = 0.0,
    val nota_media: Double = 0.0,
    val participacion: String = "Media",
    val nota_practicas: Double? = null,
    val nota_ra1: Double? = null,
    val nota_ra2: Double? = null,
    val nota_ra3: Double? = null,
    val nota_ra4: Double? = null,
    val nota_ra5: Double? = null,
    val nota_ra6: Double? = null,
    val nota_ra7: Double? = null,
    val nota_ra8: Double? = null,
    val nota_ra9: Double? = null,
    val confianza: Int? = null,
    val motivacion_escolar: Int? = null,
    val recursos_casa: Int? = null,
    val nivel_educativo_familia: Int? = null,
    val feedback_profesor: String = "",
    val prob_aprobado: Double = 0.0,
    val nivel_riesgo: String = "Medio",
    val pct_ras_superados: Double? = null
)

fun PredictionDto.toDomain(): Prediction {
    val student = Student(
        id = id,
        nombre = nombre,
        asistencia = asistencia,
        ratioAsistencia = ratio_asistencia,
        notaMedia = nota_media,
        participacion = participacion,
        notaPracticas = nota_practicas,
        notaRa1 = nota_ra1,
        notaRa2 = nota_ra2,
        notaRa3 = nota_ra3,
        notaRa4 = nota_ra4,
        notaRa5 = nota_ra5,
        notaRa6 = nota_ra6,
        notaRa7 = nota_ra7,
        notaRa8 = nota_ra8,
        notaRa9 = nota_ra9,
        confianza = confianza,
        motivacionEscolar = motivacion_escolar,
        recursosCasa = recursos_casa,
        nivelEducativoFamilia = nivel_educativo_familia,
        feedbackProfesor = feedback_profesor
    )
    return Prediction(
        student = student,
        probAprobado = prob_aprobado,
        nivelRiesgo = RiskLevel.fromString(nivel_riesgo)
    )
}
