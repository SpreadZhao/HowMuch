package com.spread.business.statistics

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.spread.db.money.MoneyRecord

@Composable
fun StatisticsScreen(
    modifier: Modifier = Modifier,
    records: List<MoneyRecord>
) {
    CategoryPercentList(
        modifier = modifier,
        records = records
    )
}