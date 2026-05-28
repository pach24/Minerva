package com.minerva.app.domain.model

data class AuthSession(
    val accessToken: String,
    val email: String
)
