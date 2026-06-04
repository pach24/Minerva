package com.minerva.app.data.remote

import com.minerva.app.data.local.SessionDataStore
import com.minerva.app.data.remote.dto.RefreshRequestDto
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Provider

/**
 * Se invoca automáticamente cuando el backend responde 401.
 *
 * Renueva el access token usando el refresh token de Supabase y reintenta la
 * petición original. El refresh está sincronizado (single-flight) porque Supabase
 * **rota** el refresh token y es de un solo uso: si varias peticiones fallan a la
 * vez, solo una renueva y el resto reutiliza el token ya refrescado.
 */
class TokenAuthenticator @Inject constructor(
    private val sessionDataStore: SessionDataStore,
    private val supabaseAuthApiProvider: Provider<SupabaseAuthApi>,
    @Named("supabase_anon_key") private val anonKey: String
) : Authenticator {

    private val lock = Any()

    override fun authenticate(route: Route?, response: Response): Request? {
        // Evita bucles infinitos: si ya reintentamos una vez, nos rendimos.
        if (responseCount(response) >= 2) return null

        val failedToken = response.request.header("Authorization")
            ?.removePrefix("Bearer ")

        synchronized(lock) {
            val currentToken = runBlocking { sessionDataStore.getToken() }

            // Otro hilo ya renovó el token mientras esperábamos el lock:
            // reintenta directamente con el token actual sin volver a refrescar.
            if (currentToken != null && currentToken != failedToken) {
                return response.request.withBearer(currentToken)
            }

            val newToken = runBlocking { refreshToken() } ?: return null
            return response.request.withBearer(newToken)
        }
    }

    private suspend fun refreshToken(): String? {
        val refreshToken = sessionDataStore.getRefreshToken() ?: return null
        return try {
            val dto = supabaseAuthApiProvider.get().refresh(
                apiKey = anonKey,
                body = RefreshRequestDto(refreshToken)
            )
            sessionDataStore.saveSession(dto.access_token, dto.refresh_token, dto.user.email)
            dto.access_token
        } catch (e: Exception) {
            // Refresh token caducado/invalidado → fuerza re-login limpiando la sesión.
            sessionDataStore.clearSession()
            null
        }
    }

    private fun Request.withBearer(token: String): Request =
        newBuilder().header("Authorization", "Bearer $token").build()

    private fun responseCount(response: Response): Int {
        var count = 1
        var prior = response.priorResponse
        while (prior != null) {
            count++
            prior = prior.priorResponse
        }
        return count
    }
}
