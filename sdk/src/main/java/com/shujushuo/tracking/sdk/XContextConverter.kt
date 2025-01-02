package com.shujushuo.tracking.sdk

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class XContextConverter {
    private val gson = Gson()

    @TypeConverter
    fun fromXContext(xcontext: XContext): String {
        return gson.toJson(xcontext)
    }

    @TypeConverter
    fun toXContext(data: String): XContext {
        val type = object : TypeToken<XContext>() {}.type
        return gson.fromJson(data, type)
    }
}
