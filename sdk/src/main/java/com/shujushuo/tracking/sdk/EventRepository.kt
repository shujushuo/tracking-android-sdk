package com.shujushuo.tracking.sdk

import android.content.Context
import com.shujushuo.tracking.sdk.TrackingSdk.log
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class EventRepository(context: Context, private var maxCacheSize: Int) {

    private val eventDao = AppDatabase.getDatabase(context).eventDao()
    private val apiService = RetrofitClient.apiService
    private val retryLock = Mutex()

    suspend fun uploadEvent(event: EventEntity, retryHistory: Boolean = true) {
        if (retryHistory)
            retryFailedEvents()
        log(event)
        log("开始请求服务器")
        try {
            val response = apiService.uploadEvent(event)
            log("Response Code: ${response.code()} Body: ${response.body()}")
            if (response.code() >= 500) {
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
        log("保存数据入库，最确保已存数据少于: $maxCacheSize")
        eventDao.insertWithLimit(event, maxCacheSize)
        log("当前已经积压数据: ${eventDao.getCount()}条")
    }

    suspend fun retryFailedEvents() {
        retryLock.withLock {
            val failedEvents = eventDao.getAllEvents()
            if (failedEvents.isNotEmpty()) {
                log("flushAllEvent 上传所有数据")
            }
            for (eventEntity in failedEvents) {
                try {
                    log("retryFailedEvents,开始请求服务器")
                    log(eventEntity)
                    val response = apiService.uploadEvent(eventEntity)
                    log("retryFailedEvents Response Code: ${response.code()} Body: ${response.body()}")
                    if (response.isSuccessful) {
                        log("retryFailedEvents 上传成功")
                        eventDao.deleteEventsByIds(listOf(eventEntity.id))
                    } else if (response.code() in 300..499) {
                        // 如果code 等于400，说明上报的数据有问题。也应该清除本地数据
                        log("retryFailedEvents,数据异常，删除本地数据")
                        eventDao.deleteEventsByIds(listOf(eventEntity.id))
                    } else {
                        log("retryFailedEvents 上传失败")
                    }
                } catch (e: Exception) {
                    log("retryFailedEvents上传事件失败: ${e.message}")
                }
            }
        }
    }
}
