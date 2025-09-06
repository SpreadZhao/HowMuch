package com.spread.business.statistics

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.dp
import com.spread.common.timeInMillisToDateStr
import com.spread.db.money.MoneyRecord
import com.spread.db.money.MoneyType
import com.spread.ui.Nothing
import java.math.BigDecimal
import java.math.RoundingMode

@Composable
fun CategoryPercentList(
    modifier: Modifier = Modifier,
    records: List<MoneyRecord>
) {

    val expenseRecords = records.filter { it.type == MoneyType.Expense }
    val incomeRecords = records.filter { it.type == MoneyType.Income }

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
        LazyColumn {
            data class PercentItem(
                val records: List<MoneyRecord>,
                val category: String,
                val sumValue: BigDecimal,
                val percent: BigDecimal
            )
            val displayRecords = if (option == 0) expenseRecords else incomeRecords
            if (displayRecords.isEmpty()) {
                item {
                    Nothing(modifier = Modifier.fillMaxSize(), iconSize = 100.dp)
                }
            } else {
                displayRecords.groupBy { it.category }
                    .map { (category, recordsOfCat) ->
                        val sumValue = recordsOfCat.sumOf { it.value }
                        val percent = sumValue
                            .divide(
                                displayRecords.sumOf { it.value },
                                4,
                                RoundingMode.HALF_UP
                            )
                        PercentItem(
                            records = recordsOfCat,
                            category = category,
                            sumValue = sumValue,
                            percent = percent
                        )
                    }
                    .sortedByDescending { it.sumValue } // 根据 sumValue 排序
                    .forEach { (records, category, sumValue, percent) ->
                        item {
                            CategoryPercentItem(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentHeight()
                                    .padding(bottom = 2.dp),
                                records = records,
                                category = category,
                                sumValue = sumValue,
                                percent = percent
                            )
                        }
                    }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CategoryPercentItem(
    modifier: Modifier = Modifier,
    records: List<MoneyRecord>,
    category: String,
    sumValue: BigDecimal,
    percent: BigDecimal
) {
    val percentFloat = percent.toFloat().coerceIn(0f, 1f)
    val bg = MaterialTheme.colorScheme.primaryContainer
    var showRecords by remember { mutableStateOf(false) }
    Row(
        modifier = modifier
            .clickable {
                showRecords = !showRecords
            }
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
    if (showRecords) {
        ModalBottomSheet(
            onDismissRequest = { showRecords = false },
        ) {
            LazyColumn {
                items(records) { record ->
                    Text(text = "${timeInMillisToDateStr(record.date)} ${record.category} ${record.remark} ${record.value}")
                }
            }
        }
    }
}

private fun BigDecimal.toPercentString(scale: Int = 2): String =
    this.multiply(BigDecimal(100))
        .setScale(scale, RoundingMode.HALF_UP)
        .toPlainString() + "%"