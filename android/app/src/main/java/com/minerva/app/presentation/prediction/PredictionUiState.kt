package com.minerva.app.presentation.prediction

import com.minerva.app.domain.model.Prediction
import com.minerva.app.domain.model.Student

sealed class PredictionUiState {
    object Idle : PredictionUiState()
    data class CsvLoaded(val students: List<Student>) : PredictionUiState()
    object Predicting : PredictionUiState()
    data class Results(val predictions: List<Prediction>) : PredictionUiState()
    data class Error(val message: String, val prevStudents: List<Student>? = null) : PredictionUiState()
}
