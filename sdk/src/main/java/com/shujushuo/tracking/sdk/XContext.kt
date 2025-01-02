package com.shujushuo.tracking.sdk

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName


data class XContext(
    @SerializedName("channelid")
    @Expose
    val channelId: String,

    @SerializedName("installid")
    @Expose
    val installId: String,

    @SerializedName("oaid")
    @Expose
    val oaid: String?,

    @SerializedName("androidid")
    @Expose
    val androidId: String,

    @SerializedName("brand")
    val brand: String,

    @SerializedName("model")
    @Expose
    val model: String,

    @SerializedName("os")
    @Expose
    val os: String,

    @SerializedName("platform")
    @Expose
    val platform: String,

    @SerializedName("os_version")
    @Expose
    val osVersion: String,

    @SerializedName("pkgname")
    @Expose
    val pkgName: String,

    @SerializedName("tz")
    @Expose
    val timezone: String,

    )