package com.minerva.app.data.repository

import com.minerva.app.core.Result
import com.minerva.app.data.local.SessionDataStore
import com.minerva.app.data.remote.SupabaseAuthApi
import com.minerva.app.data.remote.dto.LoginRequestDto
import com.minerva.app.domain.model.AuthSession
import com.minerva.app.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import retrofit2.HttpException
import javax.inject.Inject
import javax.inject.Named

class AuthRepositoryImpl @Inject constructor(
    private val supabaseAuthApi: SupabaseAuthApi,
    private val sessionDataStore: SessionDataStore,
    @Named("supabase_anon_key") private val anonKey: String
) : AuthRepository {

    override val sessionFlow: Flow<AuthSession?> = sessionDataStore.sessionFlow

    override suspend fun login(email: String, password: String): Result<AuthSession> {
        return try {
            val dto = supabaseAuthApi.signIn(
                apiKey = anonKey,
                body = LoginRequestDto(email, password)
            )
            val session = AuthSession(dto.access_token, dto.user.email)
            sessionDataStore.saveSession(session.accessToken, session.email)
            Result.Success(session)
        } catch (e: HttpException) {
            Result.Error("Credenciales incorrectas (${e.code()})", e)
        } catch (e: Exception) {
            Result.Error("Error de conexión: ${e.message}", e)
        }
    }

    override suspend fun logout() {
        sessionDataStore.clearSession()
    }
}
