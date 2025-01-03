package com.shujushuo.tracking.sdk

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import androidx.room.TypeConverter

class XContextConverter {
    private val gson = Gson()

    // 将 Map 转换为 JSON 字符串
    @TypeConverter
    fun fromMap(xcontext: Map<String, Any>?): String? {
        return xcontext?.let { gson.toJson(it) }
    }

    // 将 JSON 字符串转换为 Map
    @TypeConverter
    fun toMap(xcontextString: String?): Map<String, Any>? {
        return xcontextString?.let {
            val type = object : TypeToken<Map<String, Any>>() {}.type
            gson.fromJson<Map<String, Any>>(it, type)
        }
    }
}