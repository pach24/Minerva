package com.minerva.app.data.repository

import com.minerva.app.core.Result
import com.minerva.app.data.remote.SupabaseRestApi
import com.minerva.app.data.remote.dto.UpdateProfileDto
import com.minerva.app.data.remote.dto.toDomain
import com.minerva.app.domain.model.Profile
import com.minerva.app.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Named

class ProfileRepositoryImpl @Inject constructor(
    private val api: SupabaseRestApi,
    @Named("supabase_anon_key") private val anonKey: String
) : ProfileRepository {

    private val _profile = MutableStateFlow<Profile?>(null)
    override val profile: StateFlow<Profile?> = _profile.asStateFlow()

    override suspend fun refresh(): Result<Profile?> {
        return try {
            val row = api.getMyProfile(anonKey).firstOrNull()?.toDomain()
            _profile.value = row
            Result.Success(row)
        } catch (e: HttpException) {
            Result.Error(httpMessage(e.code()), e)
        } catch (e: IOException) {
            Result.Error("Sin conexión al cargar tu perfil. Comprueba tu red.", e)
        } catch (e: Exception) {
            Result.Error("No se pudo cargar tu perfil.", e)
        }
    }

    override suspend fun updateName(nombre: String): Result<Profile> {
        val id = _profile.value?.id ?: (refresh() as? Result.Success)?.data?.id
        if (id == null) {
            return Result.Error("No se pudo cargar tu perfil. Inténtalo de nuevo.")
        }
        return try {
            val updated = api.updateProfile(anonKey, "eq.$id", UpdateProfileDto(nombre))
                .firstOrNull()?.toDomain()
                ?: return Result.Error("No se encontró tu perfil para actualizar.")
            _profile.value = updated
            Result.Success(updated)
        } catch (e: HttpException) {
            Result.Error(httpMessage(e.code()), e)
        } catch (e: IOException) {
            Result.Error("Sin conexión al guardar. Comprueba tu red e inténtalo de nuevo.", e)
        } catch (e: Exception) {
            Result.Error("No se pudo guardar el nombre.", e)
        }
    }

    override fun clear() {
        _profile.value = null
    }

    private fun httpMessage(code: Int): String = when (code) {
        401 -> "Tu sesión ha caducado. Inicia sesión de nuevo."
        in 500..599 -> "El servidor no está disponible ahora mismo. Inténtalo en unos segundos."
        else -> "No se pudo completar la operación (código $code)."
    }
}
