package com.minerva.app.presentation.prediction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.minerva.app.core.Result
import com.minerva.app.domain.model.Student
import com.minerva.app.domain.usecase.LogoutUseCase
import com.minerva.app.domain.usecase.ParseCsvUseCase
import com.minerva.app.domain.usecase.PredictStudentsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PredictionViewModel @Inject constructor(
    private val parseCsvUseCase: ParseCsvUseCase,
    private val predictStudentsUseCase: PredictStudentsUseCase,
    private val logoutUseCase: LogoutUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<PredictionUiState>(PredictionUiState.Idle)
    val uiState: StateFlow<PredictionUiState> = _uiState.asStateFlow()

    fun loadCsv(csvText: String) {
        val students = parseCsvUseCase(csvText)
        _uiState.value = if (students.isEmpty())
            PredictionUiState.Error("El CSV no contiene estudiantes válidos. Asegúrate de que tiene columna 'nombre'.")
        else
            PredictionUiState.CsvLoaded(students)
    }

    fun predict(students: List<Student>) {
        viewModelScope.launch {
            _uiState.value = PredictionUiState.Predicting
            _uiState.value = when (val result = predictStudentsUseCase(students)) {
                is Result.Success -> PredictionUiState.Results(result.data)
                is Result.Error -> PredictionUiState.Error(result.message, students)
            }
        }
    }

    fun logout() {
        viewModelScope.launch { logoutUseCase() }
    }

    fun reset() {
        _uiState.value = PredictionUiState.Idle
    }
}
