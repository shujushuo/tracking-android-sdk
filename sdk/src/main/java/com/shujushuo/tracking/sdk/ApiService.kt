package com.shujushuo.tracking.sdk

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("up")
    suspend fun uploadEvent(@Body event: EventEntity): Response<String>
}
