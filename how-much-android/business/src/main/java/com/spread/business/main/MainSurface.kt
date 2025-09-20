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
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetValue
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import com.spread.business.statistics.StatisticsPanel
import com.spread.business.statistics.StatisticsScreen
import com.spread.db.money.MoneyRecord
import com.spread.db.service.groupByDay
import com.spread.ui.detectZoomGesture
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainSurface(viewModel: MainViewModel) {
    val viewType by viewModel.viewTypeFlow.collectAsState()
    val records by when (viewType) {
        ViewType.YearlyStatistics -> viewModel.currYearMoneyRecordsFlow.collectAsState()
        else -> viewModel.currMonthMoneyRecordsFlow.collectAsState()
    }
    val selectedMonth by viewModel.selectedMonthFlow.collectAsState()
    val selectedYear by viewModel.selectedYearFlow.collectAsState()
    val recordsListState = rememberLazyListState()
    val editRecordDialogState by viewModel.showEditRecordDialogFlow.collectAsState()
    val recentRecords by viewModel.recentRecordsFlow.collectAsState()
    Box(
        modifier = Modifier
            .fillMaxSize()
            .detectZoomGesture(
                onZoomIn = {
                    viewModel.changeViewType(viewModel.prevZoomInViewType)
                },
                onZoomOut = {
                    viewModel.changeViewType(ViewType.YearlyStatistics)
                }
            )) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            SelectorHeader(
                viewModel = viewModel,
                viewType = viewType,
                selectedYear = selectedYear,
                selectedMonth = selectedMonth
            )
            if (records.isNotEmpty()) {
                StatisticsPanel(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(
                            horizontal = 20.dp,
                            vertical = 10.dp
                        ),
                    records = records,
                    recentRecords = recentRecords
                )
            }
            MainContent(
                viewModel = viewModel,
                viewType = viewType,
                records = records,
                recordsListState = recordsListState
            )
        }
        AnimatedVisibility(
            visible = !recordsListState.isScrollInProgress && editRecordDialogState !is EditRecordDialogState.Show && viewType == ViewType.CurrMonthRecords,
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
    }
    RecordBottomSheet(
        viewModel = viewModel,
        editRecordDialogState = editRecordDialogState
    )
}

@Composable
fun ColumnScope.SelectorHeader(
    viewModel: MainViewModel,
    viewType: ViewType,
    selectedYear: Int,
    selectedMonth: Int
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
}

@Composable
fun MainContent(
    viewModel: MainViewModel,
    viewType: ViewType,
    records: List<MoneyRecord>,
    recordsListState: LazyListState
) {
    var blinkingRecord: MoneyRecord? by remember { mutableStateOf(null) }
    LaunchedEffect(viewModel.saveSuccessActionFlow) {
        viewModel.saveSuccessActionFlow.collect {
            // scroll & blink
            val targetIndex = it?.first
            if (targetIndex != null) {
                recordsListState.animateScrollToItem(targetIndex)
            }
            val br = it?.second
            if (br != null) {
                delay(400L)
                blinkingRecord = br
                delay(200L)
                blinkingRecord = null
                delay(200L)
                blinkingRecord = br
                delay(200L)
                blinkingRecord = null
            }
        }
    }
    Crossfade(
        modifier = Modifier.fillMaxSize(),
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
                        val step = when {
                            offset > 100f -> -1
                            offset < -100f -> 1
                            else -> return@detectHorizontalDragGestures
                        }
                        if (viewType == ViewType.YearlyStatistics) {
                            viewModel.selectYearDelta(step)
                        } else {
                            viewModel.selectMonthDelta(step)
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
                    state = recordsListState
                ) {
                    records.groupByDay().forEach { dailyRecords ->
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
                                records = dailyRecords,
                                blinkingRecord = blinkingRecord
                            )
                        }
                    }
                    item {
                        Spacer(modifier = Modifier.height(100.dp))
                    }
                }
            }

            ViewType.MonthlyStatistics, ViewType.YearlyStatistics -> {
                StatisticsScreen(
                    modifier = modifier,
                    viewModel = viewModel,
                    records = records
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordBottomSheet(
    viewModel: MainViewModel,
    editRecordDialogState: EditRecordDialogState
) {
    if (editRecordDialogState !is EditRecordDialogState.Show) {
        return
    }
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
        confirmValueChange = {
            it != SheetValue.Hidden
        }
    )
    ModalBottomSheet(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .imePadding(),
        onDismissRequest = {
            viewModel.hideEditRecordDialog()
        },
        dragHandle = null,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Spacer(modifier = Modifier.height(15.dp))
            val scope = rememberCoroutineScope()
            RecordEdit(
                recordEditState = viewModel.recordEditState,
                onSave = { record, insert ->
                    scope.launch {
                        sheetState.hide()
                        viewModel.hideEditRecordDialog()
                        viewModel.handleRecordEdit(
                            insert = insert,
                            record = record
                        )
                    }
                },
                onCancel = {
                    scope.launch {
                        sheetState.hide()
                        viewModel.hideEditRecordDialog()
                    }
                }
            )
        }
    }
}