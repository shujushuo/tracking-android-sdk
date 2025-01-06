package com.shujushuo.tracking.app

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.shujushuo.tracking.sdk.DeviceInfoManager
import com.shujushuo.tracking.sdk.TrackingSdk

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val deviceInfoManager: DeviceInfoManager = TrackingSdk.deviceInfoManager

    val brand: String = android.os.Build.BRAND
    val model: String = android.os.Build.MODEL
    val version: String = android.os.Build.VERSION.RELEASE
    val oaid: String? = deviceInfoManager.getOAID(application)
    val androidId: String? = deviceInfoManager.getAndroidId(application)
    val installId: String = deviceInfoManager.getInstallId(application)
}
