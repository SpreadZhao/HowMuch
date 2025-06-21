package com.spread.debug

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.spread.db.money.MoneyRecord
import com.spread.db.money.MoneyType
import com.spread.db.service.Money
import com.spread.ui.SelectionDropdownMenu
import java.math.BigDecimal
import kotlin.concurrent.thread

@Composable
fun DebugSurface() {

    var categoryInput by remember { mutableStateOf("") }
    var typeInput by remember { mutableStateOf(MoneyType.Expense) }
    var valueInput by remember { mutableStateOf("") }
    val recordsOutput = remember { mutableStateListOf<MoneyRecord>() }
    var recordsStr by remember { mutableStateOf("") }

    LazyColumn {

        item {
            TextField(
                value = categoryInput,
                onValueChange = { categoryInput = it },
                label = { Text("Category") }
            )
            TextField(
                value = valueInput,
                onValueChange = { valueInput = it },
                label = { Text("Value") }
            )
            SelectionDropdownMenu(
                items = listOf("Expense", "Income"),
                onSelect = { typeInput = MoneyType.valueOf(it) }
            )
            Button(onClick = {
                thread {
                    val record = MoneyRecord(
                        date = System.currentTimeMillis(),
                        category = categoryInput,
                        type = typeInput,
                        value = BigDecimal(valueInput)
                    )
                    Money.insertRecords(record)
                }
            }) {
                Text("Add Record")
            }
            Button(onClick = {
                thread {
                    recordsOutput.clear()
                    val records = Money.getAllRecords()
                    for (record in records) {
                        recordsOutput.add(record)
                    }
                    recordsStr = records.toString()
                }
            }) {
                Text("Get Records")
            }
            Text(recordsStr)
        }

    }

}