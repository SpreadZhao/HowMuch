package com.spread.migrate

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher

internal object Migrate {

    fun migrateFromJson(launcher: ActivityResultLauncher<Intent>) {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "text/plain"
        }
        launcher.launch(intent)
    }

}