package com.minerva.app.data.remote

import com.minerva.app.data.remote.dto.PredictionDto
import com.minerva.app.data.remote.dto.StudentRequestDto
import retrofit2.http.Body
import retrofit2.http.POST

interface MinervaApi {
    @POST("api/predecir")
    suspend fun predecir(@Body students: List<StudentRequestDto>): List<PredictionDto>
}
