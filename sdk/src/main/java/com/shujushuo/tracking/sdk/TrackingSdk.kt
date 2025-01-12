package com.shujushuo.tracking.sdk

import android.app.Application
import android.content.Context
import android.os.Build
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.github.gzuliyujiang.oaid.DeviceIdentifier
import com.google.gson.GsonBuilder
import com.shujushuo.tracking.sdk.DeviceInfoManager.Companion.KEY_INSTALL_ID
import com.shujushuo.tracking.sdk.DeviceInfoManager.Companion.PREFS_NAME
import kotlinx.coroutines.delay
import java.util.Locale
import java.util.TimeZone

object TrackingSdk {

    private lateinit var application: Application
    private lateinit var repository: EventRepository
    lateinit var deviceInfoManager: DeviceInfoManager
    lateinit var appId: String
    lateinit var channelId: String
    lateinit var baseUrl: String
    private var loggingEnabled: Boolean = false

    /**
     * 初始化 SDK
     * @param context 应用上下文
     * @param config SDK 配置信息
     */
    fun initialize(app: Application, config: SdkConfig) {
        this.initializeWithoutDefaultEvent(app, config)
        val installId = this.application.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_INSTALL_ID, null)
        // 如果installid为null，证明是第一次安装，上报install
        if (installId == null) {
            log("第一次安装，上报install事件")
            this.trackInstall()
        } else {
            log("非第一次安装，不上报install事件")
        }
        this.trackStartup(3)
    }

    /**
     * 初始化 SDK
     * @param context 应用上下文
     * @param config SDK 配置信息
     */
    fun initializeWithoutDefaultEvent(app: Application, config: SdkConfig) {
        this.application = app
        appId = config.appId
        channelId = config.channelId
        baseUrl = config.baseUrl
        RetrofitClient.setBaseUrl(baseUrl)
        log("上报地址已更新为: $baseUrl")

        repository = EventRepository(app.applicationContext)
        DeviceIdentifier.register(app)
        deviceInfoManager = DeviceInfoManager()

        Scheduler.scheduleRetryFailedEvents(app.applicationContext)
        log("SDK 初始化完成，Base URL: ${config.baseUrl}, App ID: ${config.appId}")

    }


    /**
     * 上报安装事件
     * @param xcontext 事件上下文信息
     */
    fun trackInstall() {
        this.trackEvent(
            "install",
        )
    }

    fun trackStartup(delayMs: Long = 0) {
        this.trackEvent(
            "startup",
            delayMs = delayMs
        )
    }

    fun trackLogin(xwho: String, delayMs: Long = 0) {
        this.trackEvent(
            "login", xwho,
            delayMs = delayMs
        )
    }

    fun trackPayment(
        xwho: String,
        transactionid: String,
        paymenttype: String,
        currencytype: CurrencyType,
        currencyamount: Float,
        paymentstatus: Boolean = true,
        delayMs: Long = 0
    ) {
        val parameters = mapOf(
            "transactionid" to transactionid,
            "paymenttype" to paymenttype,
            "currencytype" to currencytype,
            "currencyamount" to currencyamount,
            "paymentstatus" to paymentstatus
        )
        this.trackEvent(
            "payment", xwho,
            parameters = parameters,
            delayMs = delayMs
        )
    }

    fun trackRegister(xwho: String, delayMs: Long = 0) {
        this.trackEvent(
            "register", xwho,
            delayMs = delayMs
        )
    }

    fun trackEvent(
        xwhat: String,
        xwho: String? = null,
        parameters: Map<String, Any?> = emptyMap(),
        delayMs: Long = 0
    ) {
        // 使用协程启动上传任务
        CoroutineScope(Dispatchers.IO).launch {
            if (delayMs > 0) {
                delay(delayMs) // 延迟指定毫秒数
            }

            val defaultXContext = createDefaultXContext(application.applicationContext)

            val combinedXContext = defaultXContext + parameters


            val event = EventEntity(
                appid = appId,
                xwho = xwho, // 可以根据需求动态设置
                xwhen = System.currentTimeMillis(),
                xwhat = xwhat,
                xcontext = combinedXContext
            )
            log("上报事件: $xwhat")
            repository.uploadEvent(event)
        }
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
     * 内部日志记录方法
     * @param message 日志信息
     */
    fun log(message: String) {
        if (loggingEnabled) {
            Log.d("TrackingSdk", message)
        }
    }

    /**
     * 内部日志记录方法
     * @param event 日志信息
     */
    fun log(event: EventEntity) {
        if (loggingEnabled) {
            Log.d("TrackingSdk", GsonBuilder().setPrettyPrinting().create().toJson(event))
        }
    }

    /**
     * 创建默认的 XContext
     * 这里可以根据实际情况填充默认值或从设备获取相关信息
     */
    private fun createDefaultXContext(context: Context): Map<String, Any?> {
        val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        val pkgVersion = packageInfo.versionName ?: "unknown"
        val pkgName = packageInfo.packageName
        return mapOf(
            "os" to "android",
            "os_version" to Build.VERSION.RELEASE,
            "platform" to "android",
            "brand" to Build.BRAND,
            "model" to Build.MODEL,
            "oaid" to deviceInfoManager.getOAID(context),
            "androidid" to deviceInfoManager.getAndroidId(context),
            "tz" to getTimeZoneOffset(),
            "installid" to deviceInfoManager.getInstallId(context),
            "channelid" to this.channelId,
            "pkg_name" to pkgName,
            "pkg_version" to pkgVersion,
            "sdk_version" to BuildConfig.SDK_VERSION
        )
    }

    private fun getTimeZoneOffset(): String {
        val timeZone = TimeZone.getDefault()
        val offsetInMillis = timeZone.getOffset(System.currentTimeMillis())
        val hours = offsetInMillis / (1000 * 60 * 60)
        // 如果需要保留分钟偏移量，可以进行调整
        return String.format(Locale.getDefault(), "%+d", hours)
    }
}
