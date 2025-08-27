package com.spread.business.outside

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.spread.ui.theme.HowMuchTheme1

class QuickAddRecordActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HowMuchTheme1(dynamicColor = false) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        QuickAddRecordSurface(
                            modifier = Modifier
                                .wrapContentSize()
                                .align(Alignment.Center),
                            onCancel = { finish() }
                        )
                    }
                }
            }
        }
    }

}