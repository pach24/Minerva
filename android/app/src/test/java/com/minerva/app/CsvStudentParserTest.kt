package com.minerva.app

import com.minerva.app.data.csv.CsvStudentParser
import org.junit.Assert.*
import org.junit.Test

class CsvStudentParserTest {

    private val parser = CsvStudentParser()

    @Test
    fun `parses standard header columns`() {
        val csv = """
            id,nombre,asistencia,nota_media,participacion,nota_practicas,ra1,ra2,ra3,ra4,ra5,ra6,ra7,ra8,ra9,confianza,motivacion,recursos,nivel_educativo,feedback
            1,Ana García,85,7.5,Alta,8.0,8,7,6,9,7,5,8,7,6,4,3,2,3,Buen progreso
        """.trimIndent()

        val students = parser.parse(csv)
        assertEquals(1, students.size)
        val s = students[0]
        assertEquals("1", s.id)
        assertEquals("Ana García", s.nombre)
        assertEquals(85.0, s.asistencia, 0.001)
        assertEquals(0.85, s.ratioAsistencia, 0.001)
        assertEquals(7.5, s.notaMedia, 0.001)
        assertEquals("Alta", s.participacion)
        assertEquals(8.0, s.notaPracticas)
        assertEquals(8.0, s.notaRa1)
        assertEquals(4, s.confianza)
        assertEquals(3, s.motivacionEscolar)
        assertEquals(2, s.recursosCasa)
        assertEquals(3, s.nivelEducativoFamilia)
        assertEquals("Buen progreso", s.feedbackProfesor)
    }

    @Test
    fun `resolves alias columns alumno and media`() {
        val csv = """
            alumno,asistencia,media
            Carlos López,60,5.2
        """.trimIndent()

        val students = parser.parse(csv)
        assertEquals(1, students.size)
        assertEquals("Carlos López", students[0].nombre)
        assertEquals(5.2, students[0].notaMedia, 0.001)
    }

    @Test
    fun `asistencia greater than 1 is divided by 100`() {
        val csv = "nombre,asistencia\nTestStudent,75"
        val s = parser.parse(csv)[0]
        assertEquals(75.0, s.asistencia, 0.001)
        assertEquals(0.75, s.ratioAsistencia, 0.001)
    }

    @Test
    fun `asistencia already a ratio is kept as-is`() {
        val csv = "nombre,asistencia\nTestStudent,0.75"
        val s = parser.parse(csv)[0]
        assertEquals(0.75, s.asistencia, 0.001)
        assertEquals(0.75, s.ratioAsistencia, 0.001)
    }

    @Test
    fun `missing RA columns are null`() {
        val csv = "nombre,asistencia\nTestStudent,80"
        val s = parser.parse(csv)[0]
        assertNull(s.notaRa1)
        assertNull(s.notaRa9)
    }

    @Test
    fun `rows with empty nombre are skipped`() {
        val csv = """
            nombre,asistencia
            ,80
            ValidStudent,90
        """.trimIndent()
        val students = parser.parse(csv)
        assertEquals(1, students.size)
        assertEquals("ValidStudent", students[0].nombre)
    }

    @Test
    fun `empty CSV returns empty list`() {
        assertEquals(emptyList<Any>(), parser.parse(""))
    }

    @Test
    fun `header-only CSV returns empty list`() {
        assertEquals(emptyList<Any>(), parser.parse("nombre,asistencia"))
    }

    @Test
    fun `nota_ra alias is resolved`() {
        val csv = "nombre,nota_ra1,nota_ra2\nTestStudent,8.5,7.0"
        val s = parser.parse(csv)[0]
        assertEquals(8.5, s.notaRa1!!, 0.001)
        assertEquals(7.0, s.notaRa2!!, 0.001)
    }

    @Test
    fun `feedback alias is resolved`() {
        val csv = "nombre,feedback\nTestStudent,Great work"
        val s = parser.parse(csv)[0]
        assertEquals("Great work", s.feedbackProfesor)
    }
}
