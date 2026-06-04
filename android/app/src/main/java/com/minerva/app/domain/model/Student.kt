package com.minerva.app.domain.model

data class Student(
    val id: String = "",
    val nombre: String,
    val asistencia: Double = 0.0,
    val ratioAsistencia: Double = 0.0,
    val notaMedia: Double = 0.0,
    val participacion: String = "Media",
    val notaPracticas: Double? = null,
    val notaRa1: Double? = null,
    val notaRa2: Double? = null,
    val notaRa3: Double? = null,
    val notaRa4: Double? = null,
    val notaRa5: Double? = null,
    val notaRa6: Double? = null,
    val notaRa7: Double? = null,
    val notaRa8: Double? = null,
    val notaRa9: Double? = null,
    val confianza: Int? = null,
    val motivacionEscolar: Int? = null,
    val recursosCasa: Int? = null,
    val nivelEducativoFamilia: Int? = null,
    val feedbackProfesor: String = ""
)
