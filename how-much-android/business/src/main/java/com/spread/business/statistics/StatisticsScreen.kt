package com.spread.business.statistics

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.spread.business.main.MainViewModel
import com.spread.db.money.MoneyRecord

@Composable
fun StatisticsScreen(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel,
    records: List<MoneyRecord>
) {
    CategoryPercentList(
        modifier = modifier,
        viewModel = viewModel,
        records = records
    )
}