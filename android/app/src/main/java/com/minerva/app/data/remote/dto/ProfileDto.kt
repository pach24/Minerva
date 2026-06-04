package com.minerva.app.data.remote.dto

import com.minerva.app.domain.model.Profile
import kotlinx.serialization.Serializable

@Serializable
data class ProfileDto(
    val id: String = "",
    val nombre: String? = null
)

@Serializable
data class UpdateProfileDto(
    val nombre: String
)

fun ProfileDto.toDomain(): Profile = Profile(id = id, nombre = nombre)
