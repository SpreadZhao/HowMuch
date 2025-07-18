package com.spread.business.outside

import android.widget.Toast
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.spread.business.main.RecordEdit
import com.spread.db.service.Money
import kotlinx.coroutines.launch

@Composable
fun QuickAddRecordSurface(modifier: Modifier = Modifier, onCancel: () -> Unit) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    RecordEdit(
        modifier = modifier,
        onSave = { record, insert ->
            if (!insert) {
                return@RecordEdit
            }
            scope.launch {
                Money.insertRecords(record)
            }.invokeOnCompletion {
                Toast.makeText(context, "New record added", Toast.LENGTH_SHORT).show()
            }
        },
        onCancel = onCancel
    )
}