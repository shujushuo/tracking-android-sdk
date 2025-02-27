package com.shujushuo.tracking.sdk

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.provider.Settings
import android.util.Log
import com.github.gzuliyujiang.oaid.DeviceIdentifier
import com.github.gzuliyujiang.oaid.DeviceID
import com.shujushuo.tracking.sdk.TrackingSdk.log
import java.util.UUID

class DeviceInfoManager() {

    companion object {
        const val PREFS_NAME = "device_prefs"
        const val KEY_INSTALL_ID = "install_id"
        const val KEY_OAID = "oaid"
        const val KEY_ANDROID_ID = "android_id"
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
    fun getOAID(app: Application): String? {
        var oaid =
            app.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).getString(KEY_OAID, null)
        if (oaid.isNullOrBlank()) {
            if (DeviceID.supportedOAID(app)) {
                oaid = DeviceIdentifier.getOAID(app)
            } else {
                log("获取 OAID 失败")
            }

            if (!oaid.isNullOrBlank()) {
                app.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit()
                    .putString(KEY_OAID, oaid).apply()
            }
        }
        return oaid.takeIf { !it.isNullOrBlank() }
    }

    fun setOAID(app: Application, oaid: String?) {
        if (!oaid.isNullOrBlank()) {
            app.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit()
                .putString(KEY_OAID, oaid).apply()
        }
    }


    /**
     * 获取 installId，并缓存
     * 格式为 "timestamp_oaid" 或 "timestamp_androidId"
     */
    fun getInstallId(app: Application): String {
        var installId = app.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_INSTALL_ID, null)
        if (installId == null) {
            val timestamp = System.currentTimeMillis()
            val oaid = getOAID(app)
            val androidId = getAndroidId(app)
            val identifier =
                oaid ?: (androidId?.ifEmpty { UUID.randomUUID().toString() })
            installId = "${timestamp}_$identifier"
            app.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit()
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
