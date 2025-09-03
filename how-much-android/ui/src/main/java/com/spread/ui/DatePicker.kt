package com.spread.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.launch
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InlineDatePicker(
    displayMode: DisplayMode,
    showToggleMode: Boolean,
    onDateSelected: (Long) -> Unit
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = System.currentTimeMillis(),
        initialDisplayMode = displayMode
    )

    Box {
        DatePicker(
            title = null,
            headline = null,
            state = datePickerState,
            showModeToggle = showToggleMode
        )
    }

}

@Composable
fun YearPicker(
    modifier: Modifier = Modifier,
    year: Int,
    onConfirm: (Int) -> Unit
) {
    val years = (2000..2100).toList()

    val yearState = rememberLazyListState(initialFirstVisibleItemIndex = years.indexOf(year))

    var selectedYear by remember { mutableIntStateOf(year) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            // 年选择器
            PickerList(
                items = years,
                state = yearState,
                label = "年",
                onSelectedItemChanged = {
                    selectedYear = it
                }
            )
        }

        Spacer(Modifier.height(16.dp))

        Button(onClick = {
            onConfirm(selectedYear)
        }) {
            Text("确定")
        }
    }

}

@Composable
fun YearMonthPicker(
    year: Int,
    month: Int,
    onConfirm: (Int, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val years = (2000..2100).toList()
    val months = (1..12).toList()

    val yearState = rememberLazyListState(initialFirstVisibleItemIndex = years.indexOf(year))
    val monthState = rememberLazyListState(initialFirstVisibleItemIndex = months.indexOf(month + 1))

    var selectedYear by remember { mutableIntStateOf(year) }
    var selectedMonth by remember { mutableIntStateOf(month) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            // 年选择器
            PickerList(
                items = years,
                state = yearState,
                label = "年",
                onSelectedItemChanged = {
                    selectedYear = it
                }
            )

            // 月选择器
            PickerList(
                items = months,
                state = monthState,
                label = "月",
                onSelectedItemChanged = {
                    selectedMonth = it - 1
                }
            )
        }

        Spacer(Modifier.height(16.dp))

        Button(onClick = {
            onConfirm(selectedYear, selectedMonth)
        }) {
            Text("确定")
        }
    }

}

@Composable
fun YearMonthDayPicker(
    year: Int,
    month: Int,
    day: Int,
    onConfirm: (Int, Int, Int) -> Unit
) {
    val years = (2000..2100).toList()
    val months = (1..12).toList()

    var selectedYear by remember { mutableIntStateOf(year) }
    var selectedMonth by remember { mutableIntStateOf(month) }
    var selectedDay by remember { mutableIntStateOf(day) }

    // 动态根据年/月计算对应天数
    val days = remember(selectedYear, selectedMonth) {
        mutableStateListOf<Int>().apply {
            addAll(1..getDaysInMonth(selectedYear, selectedMonth))
        }
    }

    val yearState = rememberLazyListState(initialFirstVisibleItemIndex = years.indexOf(year))
    val monthState = rememberLazyListState(initialFirstVisibleItemIndex = months.indexOf(month + 1))
    val dayState = rememberLazyListState(initialFirstVisibleItemIndex = days.indexOf(day))

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(300.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            PickerList(
                items = years,
                state = yearState,
                label = "年",
                onSelectedItemChanged = { selectedYear = it }
            )

            PickerList(
                items = months,
                state = monthState,
                label = "月",
                onSelectedItemChanged = { selectedMonth = it - 1 }
            )

            PickerList(
                items = days,
                state = dayState,
                label = "日",
                onSelectedItemChanged = { selectedDay = it }
            )
        }

        Spacer(Modifier.height(16.dp))

        Button(onClick = {
            onConfirm(selectedYear, selectedMonth, selectedDay)
        }) {
            Text("确定")
        }
    }
}

@Composable
fun PickerList(
    items: List<Int>,
    state: LazyListState,
    label: String,
    onSelectedItemChanged: (Int) -> Unit
) {
    val itemHeight = 40.dp
    val visibleItemsCount = 5
    val paddingVertical = itemHeight * (visibleItemsCount / 2)
    val scope = rememberCoroutineScope()

    var currentSelected by remember { mutableIntStateOf(-1) }
    var isInScrollAnim by remember { mutableStateOf(false) }

    val select: CoroutineScope.(Boolean, Int) -> Unit = remember(items, currentSelected) {
        { scrollAnim,
          index ->
            items.getOrNull(index)?.let { target ->
                if (target != currentSelected) {
//                    if (!scrollAnim && currentSelected != -1) {
//                        performHapticFeedback(context)
//                    }
                    currentSelected = target
                    onSelectedItemChanged(target)
                }
                if (scrollAnim) {
                    launch {
                        isInScrollAnim = true
                        state.animateScrollToItem(index)
                        isInScrollAnim = false
                    }
                }
            }
        }
    }

    LaunchedEffect(state, items, currentSelected) {
        launch {
            snapshotFlow { state.firstVisibleItemIndex }
                .filterNot { isInScrollAnim }
                .collect {
                    val targetIndex = state.firstVisibleItemIndex
                    select(false, targetIndex)
                }
        }
        launch {
            snapshotFlow { state.isScrollInProgress }
                .distinctUntilChanged()
                .filterNot { it || isInScrollAnim }
                .collect {
                    val index = items.indexOf(currentSelected)
                    state.animateScrollToItem(index)
                }
        }
    }

    Box(
        modifier = Modifier
            .height(itemHeight * visibleItemsCount)
            .width(100.dp)
    ) {
        LazyColumn(
            state = state,
            // always select middle item
            contentPadding = PaddingValues(vertical = paddingVertical),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            itemsIndexed(items) { index, item ->
                Text(
                    text = "$item $label",
                    maxLines = 1,
                    modifier = Modifier
                        .height(itemHeight)
                        .fillMaxWidth()
                        .wrapContentHeight(align = Alignment.CenterVertically)
                        .selectable(
                            selected = item == currentSelected,
                            onClick = {
                                scope.select(true, index)
                            }
                        ),
                    textAlign = TextAlign.Center,
                    textDecoration = if (item == currentSelected) TextDecoration.Underline else TextDecoration.None
                )
            }
        }

    }
}

private fun getDaysInMonth(year: Int, month: Int): Int {
    val calendar = Calendar.getInstance().apply {
        clear()
        set(Calendar.YEAR, year)
        set(Calendar.MONTH, month)
        set(Calendar.DAY_OF_MONTH, 1)
    }
    return calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
}
