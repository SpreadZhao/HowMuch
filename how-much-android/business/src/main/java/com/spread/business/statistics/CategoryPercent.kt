package com.spread.business.statistics

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.dp
import com.spread.db.money.MoneyRecord
import com.spread.db.money.MoneyType
import com.spread.ui.Nothing
import java.math.BigDecimal
import java.math.RoundingMode

@Composable
fun CategoryPercentList(
    modifier: Modifier = Modifier,
    allRecords: List<MoneyRecord>
) {

    val expenseRecords = allRecords.filter { it.type == MoneyType.Expense }
    val incomeRecords = allRecords.filter { it.type == MoneyType.Income }

    var option by remember { mutableIntStateOf(0) }

    Column(modifier = modifier) {
        SingleChoiceSegmentedButtonRow(
            modifier = Modifier
                .wrapContentSize()
                .align(Alignment.CenterHorizontally)
                .padding(bottom = 10.dp)
        ) {
            listOf("支出", "收入").forEachIndexed { index, item ->
                SegmentedButton(
                    modifier = Modifier.wrapContentSize(),
                    shape = SegmentedButtonDefaults.itemShape(
                        index = index,
                        count = 2
                    ),
                    selected = option == index,
                    onClick = {
                        option = index
                    },
                    label = {
                        Text(text = item)
                    }
                )
            }
        }
        val records = if (option == 0) expenseRecords else incomeRecords
        if (records.isEmpty()) {
            Nothing(modifier = Modifier.fillMaxSize(), iconSize = 100.dp)
        } else {
            records.groupBy { it.category }
                .map { (category, recordsOfCat) ->
                    val sumValue = recordsOfCat.sumOf { it.value }
                    val percent = sumValue
                        .divide(
                            records.sumOf { it.value },
                            4,
                            RoundingMode.HALF_UP
                        )
                    Triple(category, sumValue, percent)
                }
                .sortedByDescending { it.second } // 根据 sumValue 排序
                .forEach { (category, sumValue, percent) ->
                    CategoryPercentItem(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentHeight(),
                        category = category,
                        sumValue = sumValue,
                        percent = percent
                    )
                }
        }
    }
}

@Composable
private fun CategoryPercentItem(
    modifier: Modifier = Modifier,
    category: String,
    sumValue: BigDecimal,
    percent: BigDecimal
) {
    val percentFloat = percent.toFloat().coerceIn(0f, 1f)
    val bg = MaterialTheme.colorScheme.primaryContainer
    Row(
        modifier = modifier
            .drawBehind {
                // 进度条颜色
                val barColor = bg
                val barWidth = size.width * percentFloat
                drawRect(
                    color = barColor,
                    size = Size(width = barWidth, height = size.height)
                )
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = category)
        Text(
            modifier = Modifier.padding(start = 8.dp),
            text = percent.toPercentString()
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(text = sumValue.toPlainString())
    }
}

private fun BigDecimal.toPercentString(scale: Int = 2): String =
    this.multiply(BigDecimal(100))
        .setScale(scale, RoundingMode.HALF_UP)
        .toPlainString() + "%"