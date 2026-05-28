package com.minerva.app.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
data class SupabaseTokenDto(
    val access_token: String,
    val refresh_token: String,
    val expires_in: Int,
    val user: SupabaseUserDto
)

@Serializable
data class SupabaseUserDto(
    val email: String
)
