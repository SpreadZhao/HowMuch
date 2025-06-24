package com.spread.debug

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.spread.db.money.MoneyType
import com.spread.db.service.Money
import com.spread.migrate.MigrateButton
import com.spread.ui.InlineDatePicker
import com.spread.ui.SelectionDropdownMenu
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private const val ROUTE_DEBUG_MAIN = "debug_main"
private const val ROUTE_DEBUG_ALL = "debug_all"
private const val ROUTE_DEBUG_CURR_MONTH = "debug_curr_month"
private const val ROUTE_DEBUG_MIGRATE = "debug_migrate"

@Composable
fun DebugSurface() {

    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = ROUTE_DEBUG_MAIN
    ) {
        composable(ROUTE_DEBUG_MAIN) { DebugMain(navController) }
        composable(ROUTE_DEBUG_ALL) { DebugAll() }
        composable(ROUTE_DEBUG_CURR_MONTH) { DebugCurrMonth() }
        composable(ROUTE_DEBUG_MIGRATE) { DebugMigrate() }
    }
}

@Composable
fun DebugMain(
    navController: NavController
) {
    LazyColumn {
        item {
            Button(
                onClick = {
                    navController.navigate(ROUTE_DEBUG_ALL)
                }
            ) {
                Text("All Records")
            }
        }
        item {
            Button(
                onClick = {
                    navController.navigate(ROUTE_DEBUG_CURR_MONTH)
                }
            ) {
                Text("Current Month")
            }
        }
        item {
            Button(
                onClick = {
                    navController.navigate(ROUTE_DEBUG_MIGRATE)
                }
            ) {
                Text("Migrate")
            }
        }
    }
}

@Composable
fun DebugAll() {

    val viewModel: DebugViewModel = viewModel()
    val scope = rememberCoroutineScope()

    var categoryInput by remember { mutableStateOf("") }
    var typeInput by remember { mutableStateOf(MoneyType.Expense) }
    var valueInput by remember { mutableStateOf("") }
    var todayRecordsStr by remember { mutableStateOf("") }
    val recordsStr by viewModel.allMoneyRecordsFlow.collectAsState()
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
                scope.launch(Dispatchers.IO) {
                    val record = Money.buildMoneyRecord {
                        date = System.currentTimeMillis()
                        category = categoryInput
                        type = typeInput
                        value = valueInput.toDouble()
                    }
                    Money.insertRecords(record)
                }
            }) {
                Text("Add Record")
            }
            Text(recordsStr)
        }

        item {
            InlineDatePicker(
                onDateSelected = {
                    scope.launch {
                        val records = Money.getRecordsOfDay(it)
                        todayRecordsStr = records.joinToString()
                    }
                }
            )
            Text(todayRecordsStr)
        }

    }
}

@Composable
fun DebugCurrMonth() {
    val scope = rememberCoroutineScope()
    val viewModel: DebugViewModel = viewModel()
    val currMonthRecords by viewModel.currMonthMoneyRecordsFlow.collectAsState()
    LazyColumn {
        for (record in currMonthRecords) {
            item {
                Text(record.toString())
                HorizontalDivider()
            }
        }
    }
}

@Composable
fun DebugMigrate() {
    MigrateButton()
}
