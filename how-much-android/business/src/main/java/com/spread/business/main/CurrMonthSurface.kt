package com.spread.business.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import java.util.Calendar

@Composable
fun CurrMonthSurface() {
    val viewModel: CurrMonthViewModel = viewModel()
    val currMonthRecords by viewModel.moneyRecordsFlow.collectAsState()
    val selectedMonth by viewModel.selectedMonthFlow.collectAsState()
    var monthInputText by remember { mutableStateOf(selectedMonth.plus(1).toString()) }
    Column {
        Row {
            TextField(
                value = monthInputText,
                onValueChange = {
                    monthInputText = it
                },
                label = { Text("Month") },
            )
            Button(
                onClick = {
                    viewModel.select(2025, monthInputText.toInt().minus(1))
                }
            ) {
                Text("ok")
            }
        }
        LazyColumn {
            currMonthRecords.groupBy {
                Calendar.getInstance().run {
                    timeInMillis = it.date
                    get(Calendar.DAY_OF_MONTH)
                }
            }.values.toList().asReversed().forEach { dailyRecords ->
                item {
                    MoneyRecordCardForOneDay(records = dailyRecords)
                }
            }
        }
    }
}