package com.spread.business.main

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.spread.business.statistics.CurrMonthStatistics
import com.spread.business.statistics.StatisticsScreen
import com.spread.db.money.MoneyRecord
import com.spread.db.service.Money
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.Calendar

private enum class ViewType {
    CurrMonthRecords,
    Statistics
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrMonthSurface() {
    val scope = rememberCoroutineScope()
    val viewModel: CurrMonthViewModel = viewModel()
    val currMonthRecords by viewModel.currMonthMoneyRecordsFlow.collectAsState()
    val selectedMonth by viewModel.selectedMonthFlow.collectAsState()
    val selectedYear by viewModel.selectedYearFlow.collectAsState()
    val listState = rememberLazyListState()
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    var showBottomSheet by remember { mutableStateOf(false) }
    var viewType by remember { mutableStateOf(ViewType.CurrMonthRecords) }
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            MonthSelector(
                modifier = Modifier.align(alignment = Alignment.CenterHorizontally),
                year = selectedYear,
                month = selectedMonth,
                onMonthChange = { year, month ->
                    viewModel.select(year, month)
                }
            )
            CurrMonthStatistics(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(
                        horizontal = 20.dp,
                        vertical = 10.dp
                    )
                    .clickable {
                        viewType = if (viewType == ViewType.CurrMonthRecords) {
                            ViewType.Statistics
                        } else {
                            ViewType.CurrMonthRecords
                        }
                    },
                currMonthRecords = currMonthRecords
            )
            Crossfade(
                targetState = viewType,
                label = "ViewType"
            ) {
                val modifier = Modifier.padding(horizontal = 20.dp)
                when (it) {
                    ViewType.CurrMonthRecords -> {
                        LazyColumn(
                            modifier = modifier,
                            state = listState
                        ) {
                            currMonthRecords.groupBy {
                                Calendar.getInstance().run {
                                    timeInMillis = it.date
                                    get(Calendar.DAY_OF_MONTH)
                                }
                            }.values.toList().asReversed().forEach { dailyRecords ->
                                item(key = dailyRecords.hashCode()) {
                                    MoneyRecordCardForOneDay(
                                        modifier = Modifier.animateItem(
                                            fadeInSpec = tween(durationMillis = 250),
                                            fadeOutSpec = tween(durationMillis = 100),
                                            placementSpec = spring(
                                                stiffness = Spring.StiffnessLow,
                                                dampingRatio = Spring.DampingRatioMediumBouncy
                                            )
                                        ),
                                        records = dailyRecords
                                    )
                                }
                            }
                        }
                    }

                    ViewType.Statistics -> {
                        StatisticsScreen(
                            modifier = modifier,
                            records = currMonthRecords
                        )
                    }
                }
            }
        }
        AnimatedVisibility(
            visible = !listState.isScrollInProgress && !showBottomSheet && viewType == ViewType.CurrMonthRecords,
            enter = scaleIn() + fadeIn(),
            exit = scaleOut() + fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 20.dp)
        ) {
            FloatingActionButton(
                onClick = {
                    showBottomSheet = true
                },
                containerColor = MaterialTheme.colorScheme.secondaryContainer,
                contentColor = MaterialTheme.colorScheme.secondary
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Money Record"
                )
            }
        }
        if (showBottomSheet) {
            ModalBottomSheet(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .imePadding(),
//                    .imeNestedScroll(),
                onDismissRequest = {
                    showBottomSheet = false
                },
                sheetState = sheetState
            ) {
                InsertRecord(
                    onSave = {
                        sheetState.dismiss(scope, it) {
                            showBottomSheet = false
                        }
                    },
                    onCancel = {
                        sheetState.dismiss(scope) {
                            showBottomSheet = false
                        }
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
private fun SheetState.dismiss(
    scope: CoroutineScope,
    record: MoneyRecord? = null,
    afterDismiss: () -> Unit
) {
    scope.launch {
        hide()
        if (record != null && !isVisible) {
            Money.insertRecords(record)
        }
    }.invokeOnCompletion {
        if (!isVisible) {
            afterDismiss()
        }
    }
}