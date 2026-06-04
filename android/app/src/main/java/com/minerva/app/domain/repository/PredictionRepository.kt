package com.minerva.app.domain.repository

import com.minerva.app.core.Result
import com.minerva.app.domain.model.Prediction
import com.minerva.app.domain.model.Student

interface PredictionRepository {
    suspend fun predict(students: List<Student>): Result<List<Prediction>>
}
