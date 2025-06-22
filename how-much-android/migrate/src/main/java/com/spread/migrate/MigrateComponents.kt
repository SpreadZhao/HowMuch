package com.spread.migrate

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch

@Composable
fun MigrateButton() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val contentResolver = context.contentResolver
            val intent = result.data
            val uri = intent?.data ?: return@rememberLauncherForActivityResult
            contentResolver.openInputStream(uri)?.use { stream ->
                val content = stream.bufferedReader().use { it.readText() }
                scope.launch {
                    // TODO: parse without KSerializer
                }
            }
        }
    }

    Button(
        onClick = {
            Migrate.migrateFromJson(launcher)
        }
    ) {
        Text("Migrate")
    }
}