package com.spread.migrate

import android.app.Activity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import com.spread.common.dateStrToDate
import com.spread.common.json
import com.spread.common.optDouble
import com.spread.common.optString
import com.spread.db.money.MoneyRecord
import com.spread.db.money.MoneyType
import com.spread.db.service.Money
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject

@Composable
fun MigrateButton() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val contentResolver = context.contentResolver
            val intent = result.data
            val uri = intent?.data ?: return@rememberLauncherForActivityResult
            contentResolver.openInputStream(uri)?.use { stream ->
                val content = stream.bufferedReader().use { it.readText() }
                scope.launch(Dispatchers.IO) {
                    // TODO: parse without KSerializer
                    // TODO: insert result statistics
                    val jsonArray = json.parseToJsonElement(content) as? JsonArray ?: return@launch
                    for (jsonObject in jsonArray) {
                        if (jsonObject is JsonObject) {
                            val record = parseMoneyRecord(jsonObject) ?: continue
                            Money.insertRecord(record)
                        }
                    }
                }
            }
        }
    }

    Button(
        onClick = {
            Migrate.migrateFromJson(launcher)
        }
    ) {
        Text("Migrate")
    }
}

private fun parseMoneyRecord(obj: JsonObject): MoneyRecord? {
    val date = dateStrToDate(obj.optString("date"), Migrate.QJ.FORMAT_DATE)
    val category = obj.optString("category")
    val type = obj.optString("type")
    val remark = obj.optString("remark")
    val money = obj.optDouble("money")

    if (date == null || category.isNullOrEmpty() || type.isNullOrEmpty() || money <= 0) {
        return null
    }
    return Money.buildMoneyRecord {
        this.date = date.time
        this.category = category
        this.type = if (type == "收入") MoneyType.Income else MoneyType.Expense
        this.remark = remark ?: ""
        this.value = money.toString()
    }
}