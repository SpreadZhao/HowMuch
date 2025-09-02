package com.spread.howmuch_android

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.spread.ui.theme.HowMuchTheme1

class CrashActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val error = intent.getStringExtra("error") ?: "Unknown Error"

        setContent {
            HowMuchTheme1 {
                Scaffold(
                    topBar = {
                        TopAppBar(
                            title = { Text("应用发生错误") }
                        )
                    }
                ) { innerPadding ->
                    val context = LocalContext.current
                    Column(modifier = Modifier.padding(innerPadding)) {
                        CrashScreen(error = error) {
                            val intent = Intent(context, MainActivity::class.java).apply {
                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                            }
                            context.startActivity(intent)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun CrashScreen(error: String, onRestart: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Ops! Something went wrong TvT",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.align(Alignment.Start)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = error,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .weight(1f, fill = false)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRestart) {
            Text("重新启动应用")
        }
    }
}
