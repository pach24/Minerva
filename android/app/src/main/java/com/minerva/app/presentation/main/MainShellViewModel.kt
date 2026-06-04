package com.minerva.app.presentation.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.minerva.app.domain.usecase.ObserveProfileUseCase
import com.minerva.app.domain.usecase.ObserveSessionUseCase
import com.minerva.app.domain.usecase.RefreshProfileUseCase
import com.minerva.app.presentation.util.emailToDisplayName
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Estado del "shell" autenticado (barra inferior + pestañas).
 *
 * Centraliza la observación de la sesión: cuando se invalida —por logout manual
 * desde Opciones o por un refresh fallido en [com.minerva.app.data.remote.TokenAuthenticator]—
 * [loggedOut] emite `true` y el shell vuelve a la pantalla de login.
 *
 * Carga el perfil una vez al entrar (su scope es la entrada MAIN, nueva tras cada
 * login) y expone el nombre para el saludo de Inicio.
 */
@HiltViewModel
class MainShellViewModel @Inject constructor(
    observeSessionUseCase: ObserveSessionUseCase,
    observeProfileUseCase: ObserveProfileUseCase,
    private val refreshProfileUseCase: RefreshProfileUseCase,
) : ViewModel() {

    private val session = observeSessionUseCase()

    init {
        viewModelScope.launch { refreshProfileUseCase() }
    }

    val loggedOut: StateFlow<Boolean> = session
        .map { it == null }
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    val userName: StateFlow<String> = combine(session, observeProfileUseCase()) { session, profile ->
        profile?.nombre?.takeIf { it.isNotBlank() } ?: emailToDisplayName(session?.email.orEmpty())
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), "")
}
