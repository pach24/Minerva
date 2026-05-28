package com.minerva.app.domain.usecase

import com.minerva.app.domain.model.Student
import com.minerva.app.domain.repository.PredictionRepository
import javax.inject.Inject

class PredictStudentsUseCase @Inject constructor(private val repo: PredictionRepository) {
    suspend operator fun invoke(students: List<Student>) = repo.predict(students)
}
