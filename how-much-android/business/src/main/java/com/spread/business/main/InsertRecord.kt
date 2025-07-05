package com.spread.business.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SegmentedButtonDefaults.IconSize
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.spread.business.R
import com.spread.common.DATE_FORMAT_YEAR_MONTH_DAY_STR
import com.spread.common.nowCalendar
import com.spread.common.timeInMillisToDateStr
import com.spread.db.money.MoneyRecord
import com.spread.db.money.MoneyType
import com.spread.db.service.Money
import com.spread.ui.MoneyInput
import com.spread.ui.YearMonthDayPicker
import com.spread.ui.toDp
import com.spread.ui.underline
import java.util.Calendar

@Composable
fun InsertRecord(
    onSave: (MoneyRecord) -> Unit,
    onCancel: () -> Unit
) {
    val calendar by remember { mutableStateOf(nowCalendar) }
    val categoryState = rememberCategoryState(
        options = listOf(
            MoneyType.Expense to R.drawable.ic_expense,
            MoneyType.Income to R.drawable.ic_income
        )
    )
    var remarkInputText by remember { mutableStateOf("") }
    var valueInputText by remember { mutableStateOf("") }
    Column(
        modifier = Modifier.padding(start = 10.dp, end = 10.dp, bottom = 10.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Date(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .align(alignment = Alignment.CenterHorizontally),
            calendar = calendar,
            categoryState = categoryState,
            remarkInputText = remarkInputText,
            valueInputText = valueInputText,
            onSave = onSave,
            onCancel = onCancel,
        )
        Category(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(), state = categoryState
        )
        RemarkAndMoney(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            remarkInputText = remarkInputText,
            onRemarkInputTextChange = { remarkInputText = it },
            valueInput = valueInputText,
            onNewValue = { valueInputText = it }
        )
    }
}

@Composable
fun RemarkAndMoney(
    modifier: Modifier = Modifier,
    remarkInputText: String,
    onRemarkInputTextChange: (String) -> Unit,
    valueInput: String,
    onNewValue: (String) -> Unit
) {
    val screenWidth = LocalWindowInfo.current.containerSize.width
    val minMoneyWidth = screenWidth / 3
    val maxMoneyWidth = screenWidth * 2 / 3

    val textMeasurer = rememberTextMeasurer()
    val textStyle = LocalTextStyle.current
    val originTextWidth = textMeasurer.measure(
        text = "0.00",
        style = textStyle
    ).size.width
    val measuredTextWidth = remember(valueInput) {
        textMeasurer.measure(
            text = valueInput.ifBlank { "0" },
            style = textStyle
        ).size.width
    }

    val moneyFieldWidth = remember(measuredTextWidth) {
        val target = measuredTextWidth + minMoneyWidth - originTextWidth
        target.coerceIn(minMoneyWidth, maxMoneyWidth)
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min),
        verticalAlignment = Alignment.CenterVertically
    ) {
        OutlinedTextField(
            modifier = Modifier
                .weight(1f)
                .padding(end = 5.dp)
                .fillMaxHeight(),
            value = remarkInputText,
            onValueChange = onRemarkInputTextChange,
            label = { Text("备注") },
            singleLine = true
        )

        MoneyInput(
            modifier = Modifier
                .width(moneyFieldWidth.toDp())
                .fillMaxHeight(),
            onNewValue = onNewValue,
            label = { Text("金额") }
        )
    }
}


@Composable
fun Date(
    modifier: Modifier,
    calendar: Calendar,
    categoryState: CategoryState,
    remarkInputText: String,
    valueInputText: String,
    onSave: (MoneyRecord) -> Unit,
    onCancel: () -> Unit,
) {
    var showPicker by remember { mutableStateOf(false) }
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {

        TextButton(
            onClick = onCancel
        ) {
            Text(text = "取消")
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = Icons.Default.DateRange,
                contentDescription = "Date",
            )
            TextButton(
                onClick = {
                    showPicker = true
                }
            ) {
                Text(
                    text = timeInMillisToDateStr(
                        calendar.timeInMillis,
                        DATE_FORMAT_YEAR_MONTH_DAY_STR
                    )
                )
            }
        }

        TextButton(
            onClick = {
                val moneyRecord = Money.buildMoneyRecord {
                    date = calendar.timeInMillis
                    type = categoryState.selectedOption.first
                    remark = remarkInputText
                    value = valueInputText
                    category = categoryState.categoryInputText
                }
                // TODO: close after save successfully
                onSave(moneyRecord)
            }
        ) {
            Text(text = "保存")
        }
    }
    if (showPicker) {
        AlertDialog(
            onDismissRequest = {
                showPicker = false
            },
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    TextButton(
                        onClick = {
                            calendar.apply {
                                clear()
                                timeInMillis = nowCalendar.timeInMillis
                                add(Calendar.DAY_OF_MONTH, -2)
                            }
                            showPicker = false
                        }
                    ) {
                        Text(text = "前天")
                    }
                    TextButton(
                        onClick = {
                            calendar.apply {
                                clear()
                                timeInMillis = nowCalendar.timeInMillis
                                add(Calendar.DAY_OF_MONTH, -1)
                            }
                            showPicker = false
                        }
                    ) {
                        Text(text = "昨天")
                    }
                    TextButton(
                        onClick = {
                            calendar.apply {
                                clear()
                                timeInMillis = nowCalendar.timeInMillis
                            }
                            showPicker = false
                        }
                    ) {
                        Text(text = "今天")
                    }
                }
            },
            text = {
                YearMonthDayPicker(
                    year = calendar.get(Calendar.YEAR),
                    month = calendar.get(Calendar.MONTH),
                    day = calendar.get(Calendar.DAY_OF_MONTH),
                    onConfirm = { y, m, d ->
                        calendar.set(y, m, d)
                        showPicker = false
                    }
                )
            },
            confirmButton = {}
        )
    }
}

@Composable
fun Category(modifier: Modifier, state: CategoryState) {
    Column(modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                modifier = Modifier
                    .size(24.dp)
                    .padding(end = 4.dp),
                painter = painterResource(id = R.drawable.ic_categories),
                contentDescription = null,
            )
            BasicTextField(
                modifier = Modifier.weight(2f),
                value = state.categoryInputText,
                textStyle = LocalTextStyle.current.copy(
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurface
                ),
                onValueChange = { state.categoryInputText = it },
                cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                singleLine = true,
                decorationBox = {
                    Box(
                        modifier = Modifier
                            .underline(
                                strokeWidth = 1.dp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            .padding(2.dp)
                    ) {
                        it()
                    }
                }
            )
            Spacer(modifier = Modifier.width(10.dp))
            SingleChoiceSegmentedButton(modifier = Modifier.wrapContentWidth(), state = state)
        }
        CategorySurface()
    }
}

@Composable
fun rememberCategoryState(
    options: List<Pair<MoneyType, Int>>
): CategoryState {
    return rememberSaveable(saver = CategoryState.Saver) {
        CategoryState(options)
    }
}

class CategoryState(
    val options: List<Pair<MoneyType, Int>>,
    selectedIndex: Int = 0,
    categoryInputText: String = ""
) {
    var selectedIndex by mutableIntStateOf(selectedIndex)
    var categoryInputText by mutableStateOf(categoryInputText)
    val selectedOption: Pair<MoneyType, Int>
        get() = options[selectedIndex]

    companion object {
        val Saver: Saver<CategoryState, *> = listSaver(
            save = { listOf(it.options, it.selectedIndex, it.categoryInputText) },
            restore = {
                @Suppress("UNCHECKED_CAST")
                CategoryState(it[0] as List<Pair<MoneyType, Int>>, it[1] as Int, it[2] as String)
            }
        )
    }
}

@Composable
fun SingleChoiceSegmentedButton(
    modifier: Modifier = Modifier,
    state: CategoryState
) {
    SingleChoiceSegmentedButtonRow(modifier = modifier) {
        state.options.forEachIndexed { index, item ->
            SegmentedButton(
                modifier = Modifier.wrapContentSize(),
                shape = SegmentedButtonDefaults.itemShape(
                    index = index,
                    count = state.options.size
                ),
                icon = {
                },
                onClick = { state.selectedIndex = index },
                selected = index == state.selectedIndex,
                label = {
                    Icon(
                        painter = painterResource(id = item.second),
                        contentDescription = null,
                        modifier = Modifier.size(IconSize)
                    )
                }
            )
        }
    }
}
