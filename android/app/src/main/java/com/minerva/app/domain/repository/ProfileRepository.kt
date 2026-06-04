package com.minerva.app.domain.repository

import com.minerva.app.core.Result
import com.minerva.app.domain.model.Profile
import kotlinx.coroutines.flow.StateFlow

interface ProfileRepository {
    /** Perfil cacheado en memoria; lo observan Inicio y Opciones. */
    val profile: StateFlow<Profile?>

    /** Recarga el perfil desde Supabase y actualiza la caché. */
    suspend fun refresh(): Result<Profile?>

    /** Actualiza el nombre del usuario y refleja el cambio en la caché. */
    suspend fun updateName(nombre: String): Result<Profile>

    /** Limpia la caché (al cerrar sesión). */
    fun clear()
}
