package com.minerva.app.presentation.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.minerva.app.core.Result
import com.minerva.app.domain.usecase.LogoutUseCase
import com.minerva.app.domain.usecase.ObserveProfileUseCase
import com.minerva.app.domain.usecase.ObserveSessionUseCase
import com.minerva.app.domain.usecase.RefreshProfileUseCase
import com.minerva.app.domain.usecase.UpdateUserNameUseCase
import com.minerva.app.presentation.util.emailToDisplayName
import com.minerva.app.presentation.util.initialsFrom
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProfileUiState(
    val email: String = "",
    val displayName: String = "",
    val initials: String = "",
    /** Nombre real almacenado (vacío si aún no lo ha definido) — para prerrellenar la edición. */
    val rawName: String = "",
)

data class EditNameState(
    val isSaving: Boolean = false,
    val error: String? = null,
)

@HiltViewModel
class ProfileViewModel @Inject constructor(
    observeSessionUseCase: ObserveSessionUseCase,
    observeProfileUseCase: ObserveProfileUseCase,
    private val refreshProfileUseCase: RefreshProfileUseCase,
    private val updateUserNameUseCase: UpdateUserNameUseCase,
    private val logoutUseCase: LogoutUseCase,
) : ViewModel() {

    val uiState: StateFlow<ProfileUiState> =
        combine(observeSessionUseCase(), observeProfileUseCase()) { session, profile ->
            val email = session?.email.orEmpty()
            val nombre = profile?.nombre?.takeIf { it.isNotBlank() }
            val display = nombre ?: emailToDisplayName(email)
            ProfileUiState(
                email = email,
                displayName = display,
                initials = initialsFrom(display),
                rawName = nombre.orEmpty()
            )
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), ProfileUiState())

    private val _editState = MutableStateFlow(EditNameState())
    val editState: StateFlow<EditNameState> = _editState.asStateFlow()

    /** Se emite cuando el guardado termina con éxito → la pantalla cierra el diálogo. */
    private val _editSucceeded = MutableSharedFlow<Unit>()
    val editSucceeded: SharedFlow<Unit> = _editSucceeded.asSharedFlow()

    init {
        viewModelScope.launch { refreshProfileUseCase() }
    }

    fun updateName(nombre: String) {
        val clean = nombre.trim()
        if (clean.isBlank()) {
            _editState.value = EditNameState(error = "El nombre no puede estar vacío.")
            return
        }
        viewModelScope.launch {
            _editState.value = EditNameState(isSaving = true)
            when (val result = updateUserNameUseCase(clean)) {
                is Result.Success -> {
                    _editState.value = EditNameState()
                    _editSucceeded.emit(Unit)
                }
                is Result.Error -> _editState.value = EditNameState(error = result.message)
            }
        }
    }

    /** Reinicia el estado de edición al abrir el diálogo. */
    fun startEditing() {
        _editState.value = EditNameState()
    }

    fun logout() {
        viewModelScope.launch { logoutUseCase() }
    }
}
