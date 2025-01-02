package com.shujushuo.tracking.sdk

import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private var baseUrl: String = "http://localhost:8090/" // 默认值

    fun setBaseUrl(url: String) {
        baseUrl = url
    }

    val apiService: ApiService by lazy {
        val gson = GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation() // 仅序列化带 @Expose 注解的字段
            .create()

        Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(ApiService::class.java)
    }
}
