package com.minerva.app.data.remote

import com.minerva.app.data.remote.dto.LoginRequestDto
import com.minerva.app.data.remote.dto.RefreshRequestDto
import com.minerva.app.data.remote.dto.SupabaseTokenDto
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface SupabaseAuthApi {
    @POST("auth/v1/token")
    suspend fun signIn(
        @Query("grant_type") grantType: String = "password",
        @Header("apikey") apiKey: String,
        @Body body: LoginRequestDto
    ): SupabaseTokenDto

    @POST("auth/v1/token")
    suspend fun refresh(
        @Query("grant_type") grantType: String = "refresh_token",
        @Header("apikey") apiKey: String,
        @Body body: RefreshRequestDto
    ): SupabaseTokenDto
}
