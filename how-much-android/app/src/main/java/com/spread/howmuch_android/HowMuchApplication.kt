package com.spread.howmuch_android

import android.app.Application
import android.content.Context
import android.content.Intent
import com.spread.common.HowMuch
import com.spread.common.LocalCrashHandler
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

        LocalCrashHandler(this) { throwable ->
            val intent = Intent(this, CrashActivity::class.java).apply {
                putExtra("error", throwable.stackTraceToString())
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
            }
            startActivity(intent)

            android.os.Process.killProcess(android.os.Process.myPid())
            exitProcess(1)
        }.init()
    }

}