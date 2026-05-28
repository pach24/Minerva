package com.minerva.app.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.minerva.app.domain.usecase.ObserveSessionUseCase
import com.minerva.app.presentation.navigation.Routes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    observeSessionUseCase: ObserveSessionUseCase
) : ViewModel() {

    val startDestination: StateFlow<String?> = observeSessionUseCase()
        .map { session -> if (session != null) Routes.PREDICTION else Routes.LOGIN }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), null)
}
