package com.minerva.app.data.remote

import com.minerva.app.data.remote.dto.ProfileDto
import com.minerva.app.data.remote.dto.UpdateProfileDto
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.PATCH
import retrofit2.http.Query

/**
 * Acceso directo a PostgREST de Supabase (tabla `profiles`).
 *
 * El `Authorization: Bearer <jwt>` lo añade el [AuthInterceptor] del cliente
 * `supabase_rest`; aquí solo pasamos la `apikey`. La RLS garantiza que el
 * usuario solo lea/edite su propia fila, por eso el GET no necesita filtro:
 * devuelve únicamente el perfil del usuario autenticado.
 */
interface SupabaseRestApi {

    @GET("rest/v1/profiles")
    suspend fun getMyProfile(
        @Header("apikey") apiKey: String,
        @Query("select") select: String = "id,nombre"
    ): List<ProfileDto>

    @Headers("Prefer: return=representation")
    @PATCH("rest/v1/profiles")
    suspend fun updateProfile(
        @Header("apikey") apiKey: String,
        @Query("id") idFilter: String,
        @Body body: UpdateProfileDto
    ): List<ProfileDto>
}
