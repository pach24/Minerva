package com.minerva.app.domain.usecase

import com.minerva.app.domain.model.AuthSession
import com.minerva.app.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveSessionUseCase @Inject constructor(private val repo: AuthRepository) {
    operator fun invoke(): Flow<AuthSession?> = repo.sessionFlow
}
