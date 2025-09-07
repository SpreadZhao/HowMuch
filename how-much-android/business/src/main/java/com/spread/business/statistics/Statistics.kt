package com.spread.business.statistics

import android.icu.util.Calendar
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.spread.db.money.MoneyRecord
import com.spread.db.money.MoneyType
import com.spread.ui.ColumnChart
import com.spread.ui.R
import com.spread.ui.TextConstants
import com.spread.ui.toDp
import kotlinx.coroutines.launch
import java.math.BigDecimal

@Composable
fun StatisticsPanel(
    modifier: Modifier = Modifier,
    records: List<MoneyRecord>,
    recentRecords: List<MoneyRecord>
) {
    val expense = records.filter {
        it.type == MoneyType.Expense
    }.sumOf {
        it.value
    }
    val income = records.filter {
        it.type == MoneyType.Income
    }.sumOf {
        it.value
    }
    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { 2 }
    )
    HorizontalPager(
        modifier = modifier.toggleNextPage(pagerState),
        state = pagerState,
        verticalAlignment = Alignment.Top
    ) { page ->
        if (page == 0) {
            FlowRow(
                modifier = Modifier.wrapContentSize(),
                verticalArrangement = Arrangement.Center,
            ) {
                if (expense > BigDecimal.ZERO) {
                    StatisticItem(
                        iconId = R.drawable.ic_expense,
                        description = "Current Month Expense",
                        text = "-$expense"
                    )
                }
                if (income > BigDecimal.ZERO) {
                    Spacer(modifier = Modifier.width(30.dp))
                    StatisticItem(
                        iconId = R.drawable.ic_income,
                        description = "Current Month Income",
                        text = "+$income"
                    )
                }
                if (income > BigDecimal.ZERO && expense > BigDecimal.ZERO) {
                    Spacer(modifier = Modifier.width(30.dp))
                    StatisticItem(
                        iconId = R.drawable.ic_balance,
                        description = "Current Month Balance",
                        text = balance(income = income, expense = expense)
                    )
                }
            }
        } else {
            ColumnChart(
                data = sumForRecentRecords(recentRecords)
            )
        }
    }
}

@Composable
fun StatisticItem(
    modifier: Modifier = Modifier,
    iconId: Int,
    description: String,
    text: String
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Icon(
            modifier = Modifier.size(TextConstants.FONT_SIZE_H4.toDp()),
            painter = painterResource(id = iconId),
            contentDescription = description
        )
        Text(
            modifier = Modifier.padding(start = 8.dp),
            text = text,
            style = LocalTextStyle.current.copy(
                fontSize = TextConstants.FONT_SIZE_H4
            )
        )
    }
}

private fun balance(income: BigDecimal, expense: BigDecimal): String {
    return if (income > expense) {
        "+${income - expense}"
    } else {
        "-${expense - income}"
    }
}

private fun sumForRecentRecords(records: List<MoneyRecord>): Map<Int, BigDecimal> {
    val calendar = Calendar.getInstance()

    // 先按天分组
    val grouped = records.groupBy { record ->
        calendar.timeInMillis = record.date
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)

        // 计算当天与今天的天数差
        val dayStart = calendar.timeInMillis
        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.timeInMillis

        ((dayStart - today) / (24 * 60 * 60 * 1000)).toInt()
    }.mapValues { (_, dailyRecords) ->
        dailyRecords
            .filter { it.type == MoneyType.Expense }
            .sumOf { it.value }
    }

    // 补齐最近 7 天
    val result = mutableMapOf<Int, BigDecimal>()
    for (i in -6..0) {
        result[i] = grouped[i] ?: BigDecimal.ZERO
    }

    return result
}

@Composable
private fun Modifier.toggleNextPage(state: PagerState) = kotlin.run {
    val scope = rememberCoroutineScope()
    this then Modifier.clickable {
        scope.launch {
            state.scrollToPage((state.currentPage + 1) % state.pageCount)
        }
    }
}