package com.shujushuo.tracking.sdk

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings
import android.util.Log
import com.github.gzuliyujiang.oaid.DeviceIdentifier
import com.github.gzuliyujiang.oaid.DeviceID
import com.shujushuo.tracking.sdk.TrackingSdk.log
import java.util.UUID

class DeviceInfoManager() {

    companion object {

        private const val PREFS_NAME = "device_prefs"
        private const val KEY_INSTALL_ID = "install_id"
        private const val KEY_OAID = "oaid"
        private const val KEY_ANDROID_ID = "android_id"
    }

//    private val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    /**
     * 获取 Android ID，并缓存
     */
    fun getAndroidId(context: Context): String? {

        var androidId = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_ANDROID_ID, null)
        if (androidId == null) {
            androidId =
                Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
                    ?: null
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit()
                .putString(KEY_ANDROID_ID, androidId).apply()
        }
        return androidId
    }

    /**
     * 获取 OAID，并缓存
     */
    fun getOAID(context: Context): String? {
        var oaid =
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).getString(KEY_OAID, null)
        if (oaid == null) {
            if (DeviceID.supportedOAID(context)) {
                oaid = DeviceIdentifier.getOAID(context)
            } else {
                log("获取 OAID 失败")
            }

            if (oaid != null) {
                context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit()
                    .putString(KEY_OAID, oaid).apply()
            }
        }
        return oaid
    }

    /**
     * 获取 installId，并缓存
     * 格式为 "timestamp_oaid" 或 "timestamp_androidId"
     */
    fun getInstallId(context: Context): String {
        var installId = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_INSTALL_ID, null)
        if (installId == null) {
            val timestamp = System.currentTimeMillis()
            val oaid = getOAID(context)
            val androidId = getAndroidId(context)
            val identifier =
                oaid ?: (androidId?.ifEmpty { UUID.randomUUID().toString() })
            installId = "${timestamp}_$identifier"
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit()
                .putString(KEY_INSTALL_ID, installId).apply()
        }
        return installId
    }

    /**
     * 清除缓存（例如在需要重置 installId 时）
     */
    fun clearCache(context: Context) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit().clear().apply()
    }
}
