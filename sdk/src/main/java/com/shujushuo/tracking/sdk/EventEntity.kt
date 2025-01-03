package com.shujushuo.tracking.sdk

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.google.gson.annotations.Expose

@Entity(tableName = "events")
data class EventEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0, // 使用自增主键
    @Expose
    val appid: String,
    @Expose
    val xwhat: String,
    @Expose
    val xwho: String?,
    @Expose
    val xwhen: Long,
    @TypeConverters(XContextConverter::class)
    @Expose
    val xcontext: Map<String, Any?> // 支持不同数据类型的 Map
)
