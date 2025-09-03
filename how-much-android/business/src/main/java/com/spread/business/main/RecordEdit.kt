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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.spread.business.main.category.CategoryPanel
import com.spread.common.DATE_FORMAT_YEAR_MONTH_DAY_STR
import com.spread.common.expression.isDigitsOnly
import com.spread.common.nowCalendar
import com.spread.common.timeInMillisToDateStr
import com.spread.db.category.CategoryItem
import com.spread.db.money.MoneyRecord
import com.spread.db.money.MoneyType
import com.spread.db.service.Money
import com.spread.db.suggestion.SuggestionItem
import com.spread.ui.EasyTextField
import com.spread.ui.IconConstants
import com.spread.ui.MoneyInput
import com.spread.ui.MoneyInput2
import com.spread.ui.R
import com.spread.ui.TextConstants
import com.spread.ui.YearMonthDayPicker
import com.spread.ui.rememberMoneyInputState
import com.spread.ui.toDp
import java.math.BigDecimal
import java.util.Calendar

@Composable
fun RecordEdit(
    modifier: Modifier = Modifier,
    recordEditState: MainViewModel.RecordEditState,
    onSave: (MoneyRecord, Boolean) -> Unit,
    onCancel: () -> Unit
) {
    val record by recordEditState.recordFlow.collectAsState()
    val calendar by recordEditState.calendarFlow.collectAsState()
    val moneyInputState = rememberMoneyInputState()
    val (value, err) = moneyInputState.expressionData
    val expression = moneyInputState.inputExpression
    LaunchedEffect(value) {
        if (value == null) {
            record?.value?.let {
                // no input, or a invalid expression
                recordEditState.updateMoney(it)
                return@LaunchedEffect
            }
        }
        recordEditState.updateMoney(value)
    }
    LaunchedEffect(record) {
        recordEditState.updateCategory(record?.category ?: "")
        recordEditState.updateMoneyType(record?.type ?: MoneyType.Expense)
        recordEditState.updateRemark(record?.remark ?: "")
    }
    Column(
        modifier = modifier.padding(start = 10.dp, end = 10.dp, bottom = 10.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Header(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .align(alignment = Alignment.CenterHorizontally),
            recordEditState = recordEditState,
            calendar = calendar,
            record = record,
            onSave = onSave,
            onCancel = onCancel,
        )
        val categories by recordEditState.categoryRepository.dataFlow.collectAsState()
        Category(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            recordEditState = recordEditState,
            categories = categories
        )
        Remark(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(),
            recordEditState = recordEditState
        )
        MoneyExpr(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 5.dp),
            expression = expression,
            initial = record?.value,
            value = value
        )
        MoneyInput2(inputState = moneyInputState)
    }
}

@Composable
fun MoneyExpr(
    modifier: Modifier = Modifier,
    expression: String,
    initial: BigDecimal? = null,
    value: BigDecimal?
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        if (expression.isBlank()) {
            Spacer(modifier = Modifier.weight(1f))
            if (initial == null) {
                Text(
                    text = "How much?",
                    fontSize = TextConstants.FONT_SIZE_H3,
                    fontStyle = FontStyle.Italic
                )
            } else {
                Text(
                    text = "How much(${initial})?",
                    fontSize = TextConstants.FONT_SIZE_H3,
                    fontStyle = FontStyle.Italic
                )
            }
            Spacer(modifier = Modifier.weight(1f))
        } else {
            Text(
                text = expression,
                color = if (value != null) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.error,
                maxLines = 1,
                overflow = TextOverflow.StartEllipsis,
                fontSize = TextConstants.FONT_SIZE_H3,
                fontStyle = FontStyle.Italic
            )
            if (value != null && !expression.isDigitsOnly()) {
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "=${value}",
                    maxLines = 1,
                    overflow = TextOverflow.StartEllipsis,
                    fontSize = TextConstants.FONT_SIZE_H3,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun Remark(
    modifier: Modifier = Modifier,
    recordEditState: MainViewModel.RecordEditState
) {
    val remark by recordEditState.remarkInputFlow.collectAsState()
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Icon(
            modifier = Modifier.size(IconConstants.ICON_SIZE_NORMAL),
            painter = painterResource(id = R.drawable.ic_remark),
            contentDescription = "Remark"
        )
        EasyTextField(
            modifier = Modifier
                .weight(1f)
                .padding(end = 5.dp),
            value = remark,
            onValueChange = {
                recordEditState.updateRemark(it)
            },
        )
        val suggestions by recordEditState.suggestionRepository.dataFlow.collectAsState()
        if (suggestions.isNotEmpty()) {
            VerticalDivider(
                modifier = Modifier
                    .height(20.dp)
                    .padding(horizontal = 5.dp)
            )
            RemarkSuggestions(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 5.dp),
                recordEditState = recordEditState,
                suggestions = suggestions
            )
        }
    }
}

@Composable
private fun RemarkSuggestions(
    modifier: Modifier = Modifier,
    recordEditState: MainViewModel.RecordEditState,
    suggestions: List<SuggestionItem>
) {
    LazyRow(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // TODO: sort by freq/time in settings
        suggestions.sortedByDescending { it.useCount }.forEach { suggestion ->
            if (suggestion.text.isBlank()) {
                return@forEach
            }
            item {
                SuggestionItem(
                    text = suggestion.text,
                    freq = suggestion.useCount,
                    onClick = {
                        recordEditState.updateRemark(suggestion.text)
                    }
                )
            }
        }
    }
}

@Composable
private fun SuggestionItem(
    text: String,
    freq: Int,
    onClick: () -> Unit
) {
    Box(modifier = Modifier.wrapContentSize()) {
        SuggestionChip(
            modifier = Modifier
                .wrapContentSize()
                .padding(end = 5.dp),
            onClick = onClick,
            label = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = text,
                        maxLines = 1,
                        textAlign = TextAlign.Center
                    )
                    VerticalDivider(modifier = Modifier
                        .height(10.dp)
                        .padding(horizontal = 5.dp))
                    Text(
                        text = "x$freq",
                        maxLines = 1,
                        textAlign = TextAlign.Center,
                        fontSize = TextConstants.FONT_SIZE_H5
                    )
                }
            }
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
            initialValue = valueInput,
            onNewValue = onNewValue,
            label = { Text("金额") }
        )
    }
}


@Composable
fun Header(
    modifier: Modifier,
    recordEditState: MainViewModel.RecordEditState,
    calendar: Calendar,
    record: MoneyRecord? = null,
    onSave: (MoneyRecord, Boolean) -> Unit,
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
                modifier = Modifier.size(IconConstants.ICON_SIZE_NORMAL),
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
                val moneyRecord = Money.buildMoneyRecord(record) {
                    date = calendar.timeInMillis
                    type = recordEditState.moneyTypeFlow.value
                    remark = recordEditState.remarkInputFlow.value
                    value = recordEditState.moneyInputFlow.value
                    category = recordEditState.categoryInputFlow.value
                } ?: return@TextButton
                if (record == moneyRecord) {
                    onCancel()
                    return@TextButton
                }
                recordEditState.suggestionRepository.markSuggestionUsed(moneyRecord.remark)
                // TODO: need more precise check whether record is new or not
                onSave(moneyRecord, record == null)
            }
        ) {
            Text(text = "保存")
        }
    }
    if (showPicker) {
        AlertDialog(
            modifier = Modifier.padding(horizontal = 20.dp),
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
fun Category(
    modifier: Modifier,
    recordEditState: MainViewModel.RecordEditState,
    categories: List<CategoryItem>
) {
    val category by recordEditState.categoryInputFlow.collectAsState()
    Column(modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                modifier = Modifier
                    .size(IconConstants.ICON_SIZE_NORMAL)
                    .padding(end = 4.dp),
                painter = painterResource(id = R.drawable.ic_categories),
                contentDescription = null,
            )
            EasyTextField(
                modifier = Modifier.weight(2f),
                value = category,
                onValueChange = {
                    recordEditState.updateCategory(it)
                }
            )
            Spacer(modifier = Modifier.width(10.dp))
            SingleChoiceSegmentedButton(
                modifier = Modifier.wrapContentWidth(),
                recordEditState = recordEditState
            )
        }
        CategoryPanel(
            categories = categories,
            initialCategoryName = category,
        ) {
            recordEditState.updateCategory(it.text)
        }
    }
}

@Composable
fun SingleChoiceSegmentedButton(
    modifier: Modifier = Modifier,
    recordEditState: MainViewModel.RecordEditState
) {
    val options = listOf(
        MoneyType.Expense to R.drawable.ic_expense,
        MoneyType.Income to R.drawable.ic_income
    )
    val moneyType by recordEditState.moneyTypeFlow.collectAsState()
    SingleChoiceSegmentedButtonRow(modifier = modifier) {
        options.forEachIndexed { index, item ->
            SegmentedButton(
                modifier = Modifier.wrapContentSize(),
                shape = SegmentedButtonDefaults.itemShape(
                    index = index,
                    count = 2
                ),
                icon = {
                    Icon(
                        painter = painterResource(id = item.second),
                        contentDescription = null,
                        modifier = Modifier.size(SegmentedButtonDefaults.IconSize)
                    )
                },
                onClick = { recordEditState.updateMoneyType(options[index].first) },
                selected = options[index].first == moneyType,
                label = {
                    Text(text = options[index].first.name.substring(0..2))
                }
            )
        }
    }
}
