package com.shujushuo.tracking.app

import android.app.Application
import com.shujushuo.tracking.sdk.TrackingSdk

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        TrackingSdk.setLoggingEnabled(true)
        TrackingSdk.initialize(this, "http://10.1.67.15:8090/", "APPID")
    }
}