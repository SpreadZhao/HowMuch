package com.spread.common

import android.content.Context
import java.io.File
import java.io.PrintWriter
import java.io.StringWriter
import java.util.Date

class LocalCrashHandler(
    private val context: Context,
    private val onCrash: (Throwable) -> Unit
) : Thread.UncaughtExceptionHandler {

    private val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()

    fun init() {
        Thread.setDefaultUncaughtExceptionHandler(this)
    }

    override fun uncaughtException(t: Thread, e: Throwable) {
        try {
            saveCrashLog(e)
            onCrash(e)
        } catch (ignored: Exception) {
            saveCrashLog(ignored)
        } finally {
            defaultHandler?.uncaughtException(t, e)
        }
    }

    private fun saveCrashLog(e: Throwable) {
        val log = StringWriter().apply {
            e.printStackTrace(PrintWriter(this))
        }.toString()

        val file = File(context.getExternalFilesDir(null), "crash.log")
        file.appendText("\n${Date()}:\n$log\n")
    }
}
