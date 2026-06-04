package com.minerva.app.presentation.prediction

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.minerva.app.core.Result
import com.minerva.app.domain.model.Student
import com.minerva.app.domain.usecase.ParseCsvUseCase
import com.minerva.app.domain.usecase.PredictStudentsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Estado de la evaluación (CSV → predicción → resultados). Su scope es la
 * entrada MAIN del nav externo (ver [com.minerva.app.presentation.main.MainScreen]),
 * por lo que Inicio, Evaluar y el detalle de alumno comparten el mismo estado.
 *
 * La sesión/logout NO se gestiona aquí: lo centraliza
 * [com.minerva.app.presentation.main.MainShellViewModel].
 */
@HiltViewModel
class PredictionViewModel @Inject constructor(
    private val parseCsvUseCase: ParseCsvUseCase,
    private val predictStudentsUseCase: PredictStudentsUseCase,
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

    fun reset() {
        _uiState.value = PredictionUiState.Idle
    }
}
