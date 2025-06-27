package com.spread.business.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import java.util.Calendar

@Composable
fun CurrMonthSurface() {
    val viewModel: CurrMonthViewModel = viewModel()
    val currMonthRecords by viewModel.moneyRecordsFlow.collectAsState()
    val selectedMonth by viewModel.selectedMonthFlow.collectAsState()
    val selectedYear by viewModel.selectedYearFlow.collectAsState()
    Column(modifier = Modifier.padding(top = 30.dp)) {
        MonthSelector(
            year = selectedYear,
            month = selectedMonth,
            onMonthChange = { year, month ->
                viewModel.select(year, month)
            }
        )
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