package com.spread.business.main

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.spread.ui.TextConstants
import com.spread.ui.YearMonthPicker
import java.util.Calendar

@Composable
fun MonthSelector(
    modifier: Modifier = Modifier,
    year: Int,
    month: Int,
    onMonthChange: (Int, Int) -> Unit,
) {
    var showPicker by remember { mutableStateOf(false) }

    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = {
            // 上一月
            val cal = Calendar.getInstance().apply {
                set(Calendar.YEAR, year)
                set(Calendar.MONTH, month)
                add(Calendar.MONTH, -1)
            }
            onMonthChange(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH))
        }) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Previous Month")
        }

        Text(
            text = "${year}年${month + 1}月",
            modifier = Modifier
                .clickable { showPicker = true }
                .padding(horizontal = 16.dp),
            fontSize = TextConstants.FONT_SIZE_H1
        )

        IconButton(onClick = {
            // 下一月
            val cal = Calendar.getInstance().apply {
                set(Calendar.YEAR, year)
                set(Calendar.MONTH, month)
                add(Calendar.MONTH, 1)
            }
            onMonthChange(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH))
        }) {
            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next Month")
        }
    }

    if (showPicker) {
        AlertDialog(
            onDismissRequest = { showPicker = false },
            title = {
                Row {
                    Text("选择年月")
                    Spacer(modifier = Modifier.weight(1f))
                    TextButton(
                        onClick = {
                            val cal = Calendar.getInstance()
                            onMonthChange(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH))
                            showPicker = false
                        }
                    ) {
                        Text(text = "Today")
                    }
                }
            },
            text = {
                YearMonthPicker(
                    year = year,
                    month = month,
                    onConfirm = { y, m ->
                        onMonthChange(y, m)
                        showPicker = false
                    }
                )
            },
            confirmButton = {},
            dismissButton = {}
        )
    }
}

