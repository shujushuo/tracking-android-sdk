package com.shujushuo.tracking.sdk

import android.content.Context
import androidx.work.*
import java.util.concurrent.TimeUnit

object Scheduler {
    fun scheduleRetryFailedEvents(context: Context) {
        val retryWorkRequest = PeriodicWorkRequestBuilder<RetryFailedEventsWorker>(
            5, TimeUnit.SECONDS // 最小重复间隔为15分钟
        )
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "RetryFailedEvents",
            ExistingPeriodicWorkPolicy.KEEP,
            retryWorkRequest
        )
    }
}
