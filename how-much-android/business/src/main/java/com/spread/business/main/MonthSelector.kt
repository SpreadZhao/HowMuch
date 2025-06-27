package com.spread.business.main

import android.graphics.RenderEffect
import android.graphics.Shader
import android.os.Build
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asComposeRenderEffect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.spread.common.performHapticFeedback
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import java.util.Calendar

@Composable
fun MonthSelector(
    year: Int,
    month: Int,
    onMonthChange: (Int, Int) -> Unit,
) {
    var showPicker by remember { mutableStateOf(false) }

    Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(onClick = {
            // 上一月
            val cal = Calendar.getInstance().apply {
                set(Calendar.YEAR, year)
                set(Calendar.MONTH, month)
                add(Calendar.MONTH, -1)
            }
            onMonthChange(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH))
        }) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Previous Month")
        }

        Text(
            text = "${year}年${month + 1}月",
            modifier = Modifier
                .clickable { showPicker = true }
                .padding(horizontal = 16.dp)
        )

        IconButton(onClick = {
            // 下一月
            val cal = Calendar.getInstance().apply {
                set(Calendar.YEAR, year)
                set(Calendar.MONTH, month)
                add(Calendar.MONTH, 1)
            }
            onMonthChange(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH))
        }) {
            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next Month")
        }
    }

    if (showPicker) {
        AlertDialog(
            onDismissRequest = { showPicker = false },
            title = { Text("选择年月") },
            text = {
                YearMonthPicker(
                    year = year,
                    month = month,
                    onConfirm = { y, m ->
                        onMonthChange(y, m)
                        showPicker = false
                    }
                )
            },
            confirmButton = {},
            dismissButton = {}
        )
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
fun PickerList(
    items: List<Int>,
    state: LazyListState,
    label: String,
    onSelectedItemChanged: (Int) -> Unit
) {
    val context = LocalContext.current
    val itemHeight = 40.dp
    val visibleItemsCount = 5
    val paddingVertical = itemHeight * (visibleItemsCount / 2)
    val density = LocalDensity.current

    var currentSelected by remember { mutableIntStateOf(-1) }

    LaunchedEffect(state) {
        launch {
            snapshotFlow { state.firstVisibleItemScrollOffset }
                .collect {
                    val targetIndex = scrollTargetIndex(state, density, itemHeight)
                    items.getOrNull(targetIndex)?.let { selected ->
                        if (selected != currentSelected) {
                            if (currentSelected != -1) {
                                performHapticFeedback(context)
                            }
                            currentSelected = selected
                            onSelectedItemChanged(selected)
                        }
                    }
                }
        }
        launch {
            snapshotFlow { state.isScrollInProgress }
                .distinctUntilChanged()
                .filter { !it }
                .collect {
                    val targetIndex = scrollTargetIndex(state, density, itemHeight)
                    launch {
                        state.animateScrollToItem(targetIndex)
                    }
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
            modifier = Modifier.matchParentSize(),
            contentPadding = PaddingValues(vertical = paddingVertical),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            items(items) { item ->
                Text(
                    text = "$item $label",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier
                        .height(itemHeight)
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        }

        // 顶部模糊遮罩
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .fillMaxWidth()
                    .height(itemHeight)
                    .graphicsLayer {
                        renderEffect = RenderEffect
                            .createBlurEffect(20f, 20f, Shader.TileMode.CLAMP)
                            .asComposeRenderEffect()
                    }
            )
        }

        // 底部模糊遮罩
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(itemHeight)
                    .graphicsLayer {
                        renderEffect = RenderEffect
                            .createBlurEffect(20f, 20f, Shader.TileMode.CLAMP)
                            .asComposeRenderEffect()
                    }
            )
        }

        // 中间选中边框
        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .width(100.dp)
                .height(itemHeight)
                .border(
                    1.dp,
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                )
        )
    }
}

private fun scrollTargetIndex(
    state: LazyListState,
    density: Density,
    itemHeight: Dp
): Int {
    val offsetPx = state.firstVisibleItemScrollOffset
    val thresholdPx = with(density) { itemHeight.toPx() / 2 }
    return if (offsetPx > thresholdPx) state.firstVisibleItemIndex + 1 else state.firstVisibleItemIndex
}