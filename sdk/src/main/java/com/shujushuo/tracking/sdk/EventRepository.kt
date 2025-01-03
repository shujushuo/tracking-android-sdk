package com.shujushuo.tracking.sdk

import android.content.Context
import com.shujushuo.tracking.sdk.TrackingSdk.log
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class EventRepository(context: Context) {

    private val eventDao = AppDatabase.getDatabase(context).eventDao()
    private val apiService = RetrofitClient.apiService
    private val retryLock = Mutex()

    suspend fun uploadEvent(event: EventEntity) {
        retryFailedEvents()
        log("开始请求服务器")
        try {
            val response = apiService.uploadEvent(event)
            log("Response Code: ${response.code()}")
            log("Response Body: ${response.body()}")
            if (!response.isSuccessful) {
                log("上传失败，保存到本地")
                saveEventLocally(event)
            } else {
                log("上传成功")
            }
        } catch (e: Exception) {
            log("上传事件失败: ${e.message}")
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
                        log("retryFailedEvents,开始请求服务器")
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
