package com.minerva.app.domain.usecase

import com.minerva.app.data.csv.CsvStudentParser
import com.minerva.app.domain.model.Student
import javax.inject.Inject

class ParseCsvUseCase @Inject constructor(private val parser: CsvStudentParser) {
    operator fun invoke(csvText: String): List<Student> = parser.parse(csvText)
}
