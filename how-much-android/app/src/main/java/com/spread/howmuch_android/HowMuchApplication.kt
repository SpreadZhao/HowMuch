package com.spread.howmuch_android

import android.app.Application
import android.content.Context
import android.content.Intent
import com.spread.common.HowMuch
import org.acra.ACRA
import kotlin.system.exitProcess

class HowMuchApplication : Application() {

    init {
        HowMuch.application = this
    }

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
    }

    override fun onCreate() {
        super.onCreate()

        // 初始化 ACRA
        ACRA.init(this)

        Thread.setDefaultUncaughtExceptionHandler { _, throwable ->
            ACRA.errorReporter.handleException(throwable)

            // 启动错误页
            val intent = Intent(this, CrashActivity::class.java).apply {
                putExtra("error", throwable.stackTraceToString())
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            }
            startActivity(intent)

            // 杀掉崩溃进程，避免卡死
            android.os.Process.killProcess(android.os.Process.myPid())
            exitProcess(1)
        }
    }

}