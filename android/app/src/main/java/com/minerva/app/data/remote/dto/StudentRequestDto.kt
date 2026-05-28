package com.minerva.app.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class StudentRequestDto(
    val id: String = "",
    val nombre: String,
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
    val feedback_profesor: String = ""
)
