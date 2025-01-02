package com.shujushuo.tracking.sdk

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters

class RetryFailedEventsWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        val repository = EventRepository(applicationContext)
        return try {
            repository.retryFailedEvents()
            Result.success()
        } catch (e: Exception) {
            Result.retry()
        }
    }
}
