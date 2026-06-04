package com.minerva.app.domain.repository

import com.minerva.app.core.Result
import com.minerva.app.domain.model.AuthSession
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val sessionFlow: Flow<AuthSession?>
    suspend fun login(email: String, password: String): Result<AuthSession>
    suspend fun logout()
}
