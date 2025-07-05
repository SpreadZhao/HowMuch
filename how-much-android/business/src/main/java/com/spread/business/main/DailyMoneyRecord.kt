package com.spread.business.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.spread.common.DATE_FORMAT_MONTH_DAY_STR
import com.spread.common.dateStr
import com.spread.db.money.MoneyRecord
import com.spread.db.money.MoneyType
import com.spread.ui.TextConstants
import com.spread.ui.underline
import java.util.Date

@Composable
fun MoneyRecordCardForOneDay(
    modifier: Modifier = Modifier,
    records: List<MoneyRecord>
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
            RecordItem(modifier = Modifier.padding(10.dp), record = record)
        }
    }
}

@Composable
fun RecordItem(
    modifier: Modifier = Modifier,
    record: MoneyRecord
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .underline(
                strokeWidth = 0.5.dp,
                color = MaterialTheme.colorScheme.onSurface
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
}