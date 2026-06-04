package com.minerva.app.domain.model

data class Prediction(
    val student: Student,
    val probAprobado: Double,
    val nivelRiesgo: RiskLevel
)
