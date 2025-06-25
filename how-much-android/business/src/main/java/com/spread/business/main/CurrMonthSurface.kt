package com.spread.business.main

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import java.util.Calendar

@Composable
fun CurrMonthSurface() {
    val viewModel: CurrMonthViewModel = viewModel()
    val currMonthRecords by viewModel.currMonthMoneyRecordsFlow.collectAsState()
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