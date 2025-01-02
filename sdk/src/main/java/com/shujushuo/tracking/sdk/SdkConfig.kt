package com.shujushuo.tracking.sdk

data class SdkConfig(
    val baseUrl: String = "http://localhost:8090/",
    val appId: String,
    val channelId:String="default"
)
