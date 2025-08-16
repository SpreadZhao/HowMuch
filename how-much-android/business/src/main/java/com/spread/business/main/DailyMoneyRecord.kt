package com.spread.business.main

import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.spread.common.DATE_FORMAT_MONTH_DAY_STR
import com.spread.common.DATE_FORMAT_YEAR_MONTH_DAY_TIME_STR
import com.spread.common.dateStr
import com.spread.common.timeInMillisToDateStr
import com.spread.db.money.MoneyRecord
import com.spread.db.money.MoneyType
import com.spread.db.service.Money
import com.spread.ui.EasyTextField
import com.spread.ui.TextConstants
import com.spread.ui.underline
import kotlinx.coroutines.launch
import java.util.Date

@Composable
fun MoneyRecordCardForOneDay(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel,
    records: List<MoneyRecord>,
    blinkingRecord: MoneyRecord? = null
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight(),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = Date(records.first().date).dateStr(DATE_FORMAT_MONTH_DAY_STR),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )
        for (record in records) {
            RecordItem(
                modifier = Modifier.padding(10.dp),
                viewModel = viewModel,
                record = record,
                blink = record.id == blinkingRecord?.id
            )
        }
    }
}

@Composable
fun RecordItem(
    modifier: Modifier = Modifier,
    viewModel: MainViewModel,
    record: MoneyRecord,
    blink: Boolean = false
) {
    val scope = rememberCoroutineScope()
//    var showDetail by remember { mutableStateOf(false) }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .underline(
                strokeWidth = 0.5.dp,
                color = MaterialTheme.colorScheme.onSurface
            )
            .then(if (blink) Modifier.background(Color.Red) else Modifier)
            .combinedClickable(
                onClick = {
                    viewModel.showEditRecordDialog(record)
                },
                onLongClick = {
                    scope.launch {
                        Money.deleteRecords(record)
                        viewModel.showSnackbar(
                            message = "Delete successfully",
                            actionLabel = "Undo",
                            withDismissAction = true
                        ) { result ->
                            if (result == SnackbarResult.ActionPerformed) {
                                // TODO: why this not work?
                                // scope.launch {
                                //     Money.insertRecords(record)
                                // }
                                // Now I have to make this lambda suspend
                                Money.insertRecords(record)
                            }
                        }
                    }
                }
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.wrapContentSize()
        ) {
            Text(
                text = record.category,
                fontSize = TextConstants.FONT_SIZE_H3
            )
            record.remark.takeIf { it.isNotBlank() }?.let {
                Text(
                    text = it,
                    fontSize = TextConstants.FONT_SIZE_H5
                )
            }
        }
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = "${if (record.type == MoneyType.Expense) "-" else "+"}${record.value}",
            fontSize = TextConstants.FONT_SIZE_H2
        )
    }
//    if (showDetail) {
//        RecordDetailDialog(record = record, viewModel, onDismissRequest = { showDetail = false })
//    }
}

@Composable
fun RecordDetailDialog(
    record: MoneyRecord,
    viewModel: MainViewModel,
    onDismissRequest: () -> Unit
) {
    var inputCategory by remember { mutableStateOf(record.category) }
    var inputRemark by remember { mutableStateOf(record.remark) }
    var inputValue by remember { mutableStateOf(record.value.toString()) }
    Dialog(onDismissRequest = { onDismissRequest() }) {
        Card(
            modifier = Modifier
                .wrapContentSize(),
            shape = RoundedCornerShape(16.dp),
        ) {
            Row(
                modifier = Modifier
                    .wrapContentSize()
            ) {
                Column(
                    modifier = Modifier
                        .wrapContentSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    RecordMemberItem(
                        modifier = Modifier.wrapContentSize(),
                        key = "Date",
                        value = timeInMillisToDateStr(
                            record.date,
                            DATE_FORMAT_YEAR_MONTH_DAY_TIME_STR
                        ),
                        onValueChange = {}
                    )
                    RecordMemberItem(
                        modifier = Modifier.wrapContentSize(),
                        key = "Category",
                        value = inputCategory,
                        onValueChange = { inputCategory = it }
                    )
                    RecordMemberItem(
                        modifier = Modifier.wrapContentSize(),
                        key = "Remark",
                        value = inputRemark,
                        onValueChange = { inputRemark = it }
                    )
                    RecordMemberItem(
                        modifier = Modifier.wrapContentSize(),
                        key = "Value",
                        value = inputValue,
                        onValueChange = { inputValue = it }
                    )
                }
                IconButton(
                    modifier = Modifier.padding(top = 10.dp, end = 10.dp),
                    onClick = {
                        onDismissRequest()
                        viewModel.showEditRecordDialog(record)
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit Record"
                    )
                }
            }
        }
    }
}

@Composable
fun RecordMemberItem(
    modifier: Modifier,
    key: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    if (key.isBlank() || value.isBlank()) {
        return
    }
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        EasyTextField(
            modifier = Modifier.wrapContentSize(),
            value = value,
            onValueChange = onValueChange,
            enabled = false
        )
    }
}