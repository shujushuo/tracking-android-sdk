package com.shujushuo.tracking.sdk

data class Event(
    val appid: String,
    val xwho: String?,
    val xwhen: Long,
    val xwhat: String,
    val xcontext: XContext
)