package com.spread.business.statistics

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.spread.business.R
import com.spread.db.money.MoneyRecord
import com.spread.db.money.MoneyType
import com.spread.ui.TextConstants
import com.spread.ui.toDp
import java.math.BigDecimal

@Composable
fun StatisticsPanel(
    modifier: Modifier = Modifier,
    records: List<MoneyRecord>
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
    var minHeight by remember { mutableIntStateOf(0) }
    Row(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .onGloballyPositioned {
                    minHeight = it.size.height
                },) {
            if (expense > BigDecimal.ZERO) {
                StatisticItem(
                    iconId = R.drawable.ic_expense,
                    description = "Current Month Expense",
                    text = "-$expense"
                )
            }
            if (income > BigDecimal.ZERO) {
                StatisticItem(
                    iconId = R.drawable.ic_income,
                    description = "Current Month Income",
                    text = "+$income"
                )
            }
            if (income > BigDecimal.ZERO && expense > BigDecimal.ZERO) {
                StatisticItem(
                    iconId = R.drawable.ic_balance,
                    description = "Current Month Balance",
                    text = balance(income = income, expense = expense)
                )
            }
        }
//        ColumnChart(
//            modifier = Modifier
//                .wrapContentWidth()
//                .height(with(LocalDensity.current) { minHeight.toDp() })
//                .padding(start = 16.dp)
//        )
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