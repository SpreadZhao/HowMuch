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
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.calculateZoom
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
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
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.spread.business.statistics.CurrMonthStatistics
import com.spread.business.statistics.StatisticsScreen
import com.spread.db.service.Money
import kotlinx.coroutines.launch
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainSurface(viewModel: MainViewModel) {
    val scope = rememberCoroutineScope()
    val currMonthRecords by viewModel.currMonthMoneyRecordsFlow.collectAsState()
    val currYearRecords by viewModel.currYearMoneyRecordsFlow.collectAsState()
    val selectedMonth by viewModel.selectedMonthFlow.collectAsState()
    val selectedYear by viewModel.selectedYearFlow.collectAsState()
    val listState = rememberLazyListState()
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    val editRecordDialogState by viewModel.showEditRecordDialogFlow.collectAsState()
    val viewType by viewModel.viewTypeFlow.collectAsState()
    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                awaitEachGesture {
                    var zoomViewType: ViewType? = null
                    do {
                        val event = awaitPointerEvent(PointerEventPass.Initial)
                        val zoom = event.calculateZoom()    // no zoom == 1f
                        if (zoom > 1f) {
                            zoomViewType = viewModel.prevZoomInViewType
                            event.changes.forEach { it.consume() }
                        } else if (zoom < 1f) {
                            zoomViewType = ViewType.YearlyStatistics
                            event.changes.forEach { it.consume() }
                        }
                        // exit when **all** finger lifted up, thus ensure
                        // there's no event leaked to children
                    } while (event.changes.any { it.pressed })
                    if (zoomViewType != null) {
                        viewModel.changeViewType(zoomViewType)
                    }
                }
            }) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            if (viewType == ViewType.YearlyStatistics) {
                YearSelector(
                    modifier = Modifier.align(alignment = Alignment.CenterHorizontally),
                    year = selectedYear,
                    onYearChange = { year ->
                        viewModel.select(year, selectedMonth)
                    }
                )
            } else {
                MonthSelector(
                    modifier = Modifier.align(alignment = Alignment.CenterHorizontally),
                    year = selectedYear,
                    month = selectedMonth,
                    onMonthChange = { year, month ->
                        viewModel.select(year, month)
                    }
                )
            }
            CurrMonthStatistics(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(
                        horizontal = 20.dp,
                        vertical = 10.dp
                    )
                    .clickable {
                        if (viewType == ViewType.YearlyStatistics) {
                            return@clickable
                        }
                        if (viewType == ViewType.MonthlyStatistics) {
                            viewModel.changeViewType(ViewType.CurrMonthRecords)
                        } else {
                            viewModel.changeViewType(ViewType.MonthlyStatistics)
                        }
                    },
                currMonthRecords = currMonthRecords
            )
            Crossfade(
                targetState = viewType,
                label = "ViewType"
            ) { type ->
                var offset by remember { mutableFloatStateOf(0f) }
                val modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp)
                    .pointerInput(type) {
                        detectHorizontalDragGestures(
                            onDragEnd = {
                                if (offset > 100f) {
                                    viewModel.selectMonthDelta(-1)
                                } else if (offset < -100f) {
                                    viewModel.selectMonthDelta(1)
                                }
                                offset = 0f
                            }
                        ) { change, dragAmount ->
                            offset += dragAmount
                            change.consume()
                        }
                    }
                when (type) {
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
                                        viewModel = viewModel,
                                        records = dailyRecords
                                    )
                                }
                            }
                        }
                    }

                    ViewType.MonthlyStatistics -> {
                        StatisticsScreen(
                            modifier = modifier,
                            records = currMonthRecords
                        )
                    }

                    else -> {}
                }
            }

        }
        AnimatedVisibility(
            visible = !listState.isScrollInProgress && editRecordDialogState !is EditRecordDialogState.Show && viewType == ViewType.CurrMonthRecords,
            enter = scaleIn() + fadeIn(),
            exit = scaleOut() + fadeOut(),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 20.dp)
        ) {
            FloatingActionButton(
                onClick = {
                    viewModel.showEditRecordDialog()
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
        if (editRecordDialogState is EditRecordDialogState.Show) {
            ModalBottomSheet(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .imePadding(),
//                    .imeNestedScroll(),
                onDismissRequest = {
                    viewModel.hideEditRecordDialog()
                },
                sheetState = sheetState
            ) {
                RecordEdit(
                    record = (editRecordDialogState as? EditRecordDialogState.Show)?.record,
                    onSave = { record, insert ->
                        scope.launch {
                            sheetState.hide()
                            if (insert && !sheetState.isVisible) {
                                Money.insertRecords(record)
                            } else if (!insert && !sheetState.isVisible) {
                                Money.updateRecords(record)
                            }
                        }.invokeOnCompletion {
                            if (!sheetState.isVisible) {
                                viewModel.hideEditRecordDialog()
                            }
                        }
                    },
                    onCancel = {
                        scope.launch {
                            sheetState.hide()
                        }.invokeOnCompletion {
                            if (!sheetState.isVisible) {
                                viewModel.hideEditRecordDialog()
                            }
                        }
                    }
                )
            }
        }
    }
}