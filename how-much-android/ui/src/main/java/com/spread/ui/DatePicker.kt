package com.spread.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InlineDatePicker(
    onDateSelected: (Long) -> Unit
) {
    val datePickerState = rememberDatePickerState()

    Box {
        DatePicker(
            state = datePickerState,
            showModeToggle = false
        )
        Button(
            modifier = Modifier.align(Alignment.TopEnd),
            onClick = {
                datePickerState.selectedDateMillis?.let {
                    onDateSelected(it)
                }
            }
        ) {
            Text(text = "OK")
        }
    }

}