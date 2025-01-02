package com.shujushuo.tracking.sdk

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.os.Build
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import android.provider.Settings
import com.github.gzuliyujiang.oaid.DeviceIdentifier
import kotlinx.coroutines.delay
import java.util.TimeZone

object TrackingSdk {

    private lateinit var repository: EventRepository
    private lateinit var deviceInfoManager: DeviceInfoManager
    private var appId: String = "APPID" // 可以在初始化时设置
    private var channelId: String = "default"
    private var loggingEnabled: Boolean = false

    /**
     * 初始化 SDK
     * @param context 应用上下文
     * @param config SDK 配置信息
     */
    fun initialize(app: Application, config: SdkConfig) {
        appId = config.appId
        channelId = config.channelId
        setBaseUrl(config.baseUrl)
        repository = EventRepository(app.applicationContext)
        DeviceIdentifier.register(app)
        deviceInfoManager = DeviceInfoManager()

        Scheduler.scheduleRetryFailedEvents(app.applicationContext)
        log("SDK 初始化完成，Base URL: ${config.baseUrl}, App ID: ${config.appId}")
        this.trackInstallEvent(app.applicationContext)
    }


    /**
     * 上报安装事件
     * @param xcontext 事件上下文信息
     */
    fun trackInstallEvent(context: Context) {
        // 使用协程启动上传任务
        CoroutineScope(Dispatchers.IO).launch {
            val event = EventEntity(
                appid = appId,
                xwho = null, // 可以根据需求动态设置
                xwhen = System.currentTimeMillis(),
                xwhat = "install",
                xcontext = createDefaultXContext(context)
            )

            log("上报安装事件: install")
            repository.uploadEvent(event)
        }
    }

    /**
     * 跟踪并上报自定义事件
     * @param eventName 事件名称
     */
    fun trackEvent(eventName: String, context: Context, delayMs: Long = 0) {

        // 使用协程启动上传任务
        CoroutineScope(Dispatchers.IO).launch {
            if (delayMs > 0) {
                delay(delayMs) // 延迟指定毫秒数
            }

            val event = EventEntity(
                appid = appId,
                xwho = "example_xwho", // 可以根据需求动态设置
                xwhen = System.currentTimeMillis(),
                xwhat = eventName,
                xcontext = createDefaultXContext(context)
            )
            log("跟踪自定义事件: $eventName")
            repository.uploadEvent(event)
        }
    }

    /**
     * 动态设置上报地址
     * @param url 上报服务器的 Base URL
     */
    private fun setBaseUrl(url: String) {
        RetrofitClient.setBaseUrl(url)
        log("上报地址已更新为: $url")
    }

    /**
     * 启用或禁用 SDK 日志
     * @param enabled 是否启用日志
     */
    fun setLoggingEnabled(enabled: Boolean) {
        loggingEnabled = enabled
        log("日志功能已 ${if (enabled) "启用" else "禁用"}")
    }

    /**
     * 获取 SDK 版本信息
     * @return SDK 版本号
     */
    fun getSDKVersion(): String {
        return "1.0.0" // 版本号可以根据实际情况更新
    }

    /**
     * 内部日志记录方法
     * @param message 日志信息
     */
    private fun log(message: String) {
        if (loggingEnabled) {
            android.util.Log.d("TrackingSdk", message)
        }
    }

    /**
     * 创建默认的 XContext
     * 这里可以根据实际情况填充默认值或从设备获取相关信息
     */
    @SuppressLint("HardwareIds")
    private suspend fun createDefaultXContext(context: Context): XContext {
        return XContext(
            os = "android",
            osVersion = Build.VERSION.RELEASE,
            platform = "android",
            brand = Build.BRAND,
            oaid = deviceInfoManager.getOAID(context),
            androidId = deviceInfoManager.getAndroidId(context),
            timezone = getTimeZoneOffset(),
            installId = deviceInfoManager.getInstallId(context),
            model = Build.MODEL,
            channelId = this.channelId,
            pkgName = context.packageName
        )
    }

    private fun getTimeZoneOffset(): String {
        val timeZone = TimeZone.getDefault()
        val offsetInMillis = timeZone.getOffset(System.currentTimeMillis())
        val hours = offsetInMillis / (1000 * 60 * 60)
        // 如果需要保留分钟偏移量，可以进行调整
        return String.format("%+d", hours)
    }
}
