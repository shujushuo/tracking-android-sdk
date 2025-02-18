package com.shujushuo.tracking.app

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import com.shujushuo.tracking.sdk.TrackingSdk

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        TrackingSdk.setLoggingEnabled(true)
        TrackingSdk.initialize(this, "https://tracking.wdyxgames.com/", "200_1165")
    }
}