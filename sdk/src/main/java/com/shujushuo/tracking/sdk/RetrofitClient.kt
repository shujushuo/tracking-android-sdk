package com.shujushuo.tracking.sdk

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    private var baseUrl: String = "http://localhost:8090/" // 默认值

    fun setBaseUrl(url: String) {
        baseUrl = url
        resetApiService()
    }

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS) // 设置连接超时
            .readTimeout(10, TimeUnit.SECONDS)    // 设置读取超时
            .writeTimeout(10, TimeUnit.SECONDS)   // 设置写入超时
            .retryOnConnectionFailure(true)       // 重试连接失败的请求
            .build()
    }

    @Volatile
    private var _apiService: ApiService? = null

    val apiService: ApiService
        get() = _apiService ?: synchronized(this) {
            _apiService ?: createApiService().also { _apiService = it }
        }

    private fun createApiService(): ApiService {
        val gson = GsonBuilder()
            .excludeFieldsWithoutExposeAnnotation() // 仅序列化带 @Expose 注解的字段
            .create()

        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(ApiService::class.java)
    }

    private fun resetApiService() {
        _apiService = null
    }
}
