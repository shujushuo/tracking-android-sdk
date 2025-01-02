package com.shujushuo.tracking.sdk

import android.content.Context
import android.util.Log
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class EventRepository(context: Context) {

    private val eventDao = AppDatabase.getDatabase(context).eventDao()
    private val apiService = RetrofitClient.apiService
    private val retryLock = Mutex()

    suspend fun uploadEvent(event: EventEntity) {
        retryFailedEvents()
        Log.d("TrackingSdk", "开始请求服务器")
        try {
            val response = apiService.uploadEvent(event)
            Log.d("TrackingSdk", "Response Code: ${response.code()}")
            Log.d("TrackingSdk", "Response Message: ${response.message()}")
            Log.d("TrackingSdk", "Response Body: ${response.body()}")
            Log.d("TrackingSdk", "Response Error Body: ${response.errorBody()?.string()}")
            if (!response.isSuccessful) {
                // 上传失败，保存到本地
                Log.d("TrackingSdk", "上传失败，保存到本地")

                saveEventLocally(event)
            } else {
                Log.d("TrackingSdk", "上传成功")
            }
        } catch (e: Exception) {
            Log.e("TrackingSdk", "上传事件失败: ${e.message}")
            e.printStackTrace()
            // 网络异常，保存到本地
            saveEventLocally(event)
        }
    }

    private suspend fun saveEventLocally(event: EventEntity) {
        eventDao.insert(event)
        // 可以添加更多日志或错误处理
    }

    suspend fun retryFailedEvents() {
        retryLock.withLock {
            val failedEvents = eventDao.getAllEvents()
            for (eventEntity in failedEvents) {
                try {
                    val response = apiService.uploadEvent(eventEntity)
                    if (response.isSuccessful) {
                        eventDao.deleteEventsByIds(listOf(eventEntity.id))
                        Log.d("retryFailedEvents", "开始请求服务器")
                        // 可以添加更多日志或成功处理
                    }
                } catch (e: Exception) {
                    // 仍然失败，保留在本地
                    // 可根据需要添加重试策略或日志记录
                }
            }
        }
    }
}
