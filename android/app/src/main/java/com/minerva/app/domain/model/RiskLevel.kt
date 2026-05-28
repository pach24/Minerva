package com.minerva.app.domain.model

enum class RiskLevel(val label: String) {
    BAJO("Bajo"),
    MEDIO("Medio"),
    ALTO("Alto"),
    CRITICO("Crítico");

    companion object {
        fun fromString(s: String): RiskLevel =
            entries.firstOrNull { it.label.equals(s, ignoreCase = true) } ?: MEDIO
    }
}
