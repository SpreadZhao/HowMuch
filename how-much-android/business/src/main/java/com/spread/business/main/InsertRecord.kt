package com.spread.business.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.spread.business.R
import com.spread.common.DATE_FORMAT_YEAR_MONTH_DAY_STR
import com.spread.common.nowCalendar
import com.spread.common.timeInMillisToDateStr
import com.spread.ui.YearMonthDayPicker
import com.spread.ui.bottomBorder
import java.util.Calendar

@Composable
fun InsertRecord() {
    val typeState = rememberSegmentedButtonState(
        options = listOf("Income", "Expense")
    )
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(horizontal = 10.dp, vertical = 5.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Date(
            modifier = Modifier
                .wrapContentSize()
                .align(alignment = Alignment.CenterHorizontally)
        )
        Category(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight(), state = typeState
        )
    }
}

@Composable
fun Date(modifier: Modifier) {
    val calendar by remember { mutableStateOf(nowCalendar) }
    var showPicker by remember { mutableStateOf(false) }
    TextButton(
        modifier = modifier,
        onClick = {
            showPicker = true
        }
    ) {
        Text(text = timeInMillisToDateStr(calendar.timeInMillis, DATE_FORMAT_YEAR_MONTH_DAY_STR))
    }
    if (showPicker) {
        AlertDialog(
            onDismissRequest = {
                showPicker = false
            },
            title = {
                Text(text = "Select Date")
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
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Icon(
            modifier = Modifier.weight(1f),
            painter = painterResource(id = R.drawable.ic_categories),
            contentDescription = null,
        )
        BasicTextField(
            modifier = Modifier.weight(2f),
            value = state.categoryInputText,
            textStyle = TextStyle(fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface),
            onValueChange = { state.categoryInputText = it },
            cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
            singleLine = true,
            decorationBox = {
                Box(
                    modifier = Modifier
                        .bottomBorder(
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
}

@Composable
fun rememberSegmentedButtonState(
    options: List<String>
): CategoryState {
    return rememberSaveable(saver = CategoryState.Saver) {
        CategoryState(options)
    }
}

class CategoryState(
    val options: List<String>,
    selectedIndex: Int = 0,
    categoryInputText: String = ""
) {
    var selectedIndex by mutableIntStateOf(selectedIndex)
    var categoryInputText by mutableStateOf(categoryInputText)

    companion object {
        val Saver: Saver<CategoryState, *> = listSaver(
            save = { listOf(it.options, it.selectedIndex, it.categoryInputText) },
            restore = {
                @Suppress("UNCHECKED_CAST")
                CategoryState(it[0] as List<String>, it[1] as Int, it[2] as String)
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
        state.options.forEachIndexed { index, label ->
            SegmentedButton(
                modifier = Modifier.wrapContentSize(),
                shape = SegmentedButtonDefaults.itemShape(
                    index = index,
                    count = state.options.size
                ),
                onClick = { state.selectedIndex = index },
                selected = index == state.selectedIndex,
                label = { Text(text = label, fontSize = 10.sp) }
            )
        }
    }
}
