package com.minerva.app.data.csv

import com.minerva.app.domain.model.Student
import javax.inject.Inject

class CsvStudentParser @Inject constructor() {

    fun parse(csvText: String): List<Student> {
        val lines = csvText.trim().split('\n').map { it.trim() }.filter { it.isNotEmpty() }
        if (lines.size < 2) return emptyList()

        val headers = lines[0].split(',').map { it.trim().lowercase() }

        return lines.drop(1).mapNotNull { line ->
            val vals = line.split(',').map { it.trim() }
            val obj = mutableMapOf<String, String>()
            headers.forEachIndexed { i, h -> obj[h] = if (i < vals.size) vals[i] else "" }

            val nombre = obj.resolve("nombre", "alumno", "name") ?: return@mapNotNull null
            if (nombre.isBlank()) return@mapNotNull null

            val rawAsistencia = obj["asistencia"]?.toDoubleOrNull() ?: 0.0
            val ratioAsistencia = if (rawAsistencia > 1.0) rawAsistencia / 100.0 else rawAsistencia

            Student(
                id = obj["id"] ?: "",
                nombre = nombre,
                asistencia = rawAsistencia,
                ratioAsistencia = ratioAsistencia,
                notaMedia = obj.resolveDouble("nota_media", "media", "nota"),
                participacion = obj.resolve("participacion", "participación") ?: "Media",
                notaPracticas = obj.resolveDoubleOrNull("nota_practicas"),
                notaRa1 = obj.resolveDoubleOrNull("ra1", "nota_ra1"),
                notaRa2 = obj.resolveDoubleOrNull("ra2", "nota_ra2"),
                notaRa3 = obj.resolveDoubleOrNull("ra3", "nota_ra3"),
                notaRa4 = obj.resolveDoubleOrNull("ra4", "nota_ra4"),
                notaRa5 = obj.resolveDoubleOrNull("ra5", "nota_ra5"),
                notaRa6 = obj.resolveDoubleOrNull("ra6", "nota_ra6"),
                notaRa7 = obj.resolveDoubleOrNull("ra7", "nota_ra7"),
                notaRa8 = obj.resolveDoubleOrNull("ra8", "nota_ra8"),
                notaRa9 = obj.resolveDoubleOrNull("ra9", "nota_ra9"),
                confianza = obj.resolveIntOrNull("confianza"),
                motivacionEscolar = obj.resolveIntOrNull("motivacion", "motivacion_escolar"),
                recursosCasa = obj.resolveIntOrNull("recursos", "recursos_casa"),
                nivelEducativoFamilia = obj.resolveIntOrNull("nivel_educativo", "nivel_educativo_familia"),
                feedbackProfesor = obj.resolve("feedback", "feedback_profesor") ?: ""
            )
        }
    }

    private fun Map<String, String>.resolve(vararg keys: String): String? =
        keys.firstNotNullOfOrNull { k -> this[k]?.takeIf { it.isNotEmpty() } }

    private fun Map<String, String>.resolveDouble(vararg keys: String): Double =
        keys.firstNotNullOfOrNull { k -> this[k]?.toDoubleOrNull() } ?: 0.0

    private fun Map<String, String>.resolveDoubleOrNull(vararg keys: String): Double? =
        keys.firstNotNullOfOrNull { k -> this[k]?.toDoubleOrNull() }

    private fun Map<String, String>.resolveIntOrNull(vararg keys: String): Int? =
        keys.firstNotNullOfOrNull { k -> this[k]?.toIntOrNull() }
}
