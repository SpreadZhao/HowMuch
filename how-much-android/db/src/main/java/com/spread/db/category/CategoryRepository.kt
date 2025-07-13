package com.spread.db.category

import android.content.Context
import android.util.Log
import com.spread.common.json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class CategoryRepository(val context: Context) {

    companion object {
        private const val TAG: String = "CategoryRepository"
        private const val FILE_PATH: String = "category.json"

        // 预设分类
        private val defaultCategories = Category(
            itemList = mutableListOf(
                CategoryItem("餐饮", "ic_food"),
                CategoryItem("购物", "ic_shopping"),
                CategoryItem("交通", "ic_transport"),
                CategoryItem("住房", "ic_housing"),
                CategoryItem("娱乐", "ic_entertainment"),
                CategoryItem("医疗", "ic_medical"),
                CategoryItem("教育", "ic_education"),
                CategoryItem("旅行", "ic_transport"),
                CategoryItem("人情", "ic_gift"),
                CategoryItem("工资", "ic_salary"),
                CategoryItem("奖金", "ic_bonus"),
                CategoryItem("投资", "ic_investment")
            )
        )
    }

    fun initSync() {
        val file = File(context.filesDir, FILE_PATH)
        if (!file.exists()) {
            writeToJsonFileSync(defaultCategories)
        }
    }

    suspend fun initAsync() {
        withContext(Dispatchers.IO) {
            val file = File(context.filesDir, FILE_PATH)
            if (!file.exists()) {
                writeToJsonFileAsync(defaultCategories)
                    .onSuccess { Log.i(TAG, "init async success") }
                    .onFailure { Log.e(TAG, "init async failed", it) }
            }
        }
    }

    private fun writeToJsonFileSync(data: Category): Result<Unit> = runCatching {
        try {
            val jsonString = json.encodeToString(data)
            File(context.filesDir, FILE_PATH).writeText(jsonString)
        } catch (e: Exception) {
            Log.e(TAG, "write sync failed", e)
        }
    }


    suspend fun writeToJsonFileAsync(data: Category): Result<Unit> = runCatching {
        withContext(Dispatchers.IO) {
            val json = json.encodeToString(data)
            File(context.filesDir, FILE_PATH).writeText(json)
        }
    }

    suspend fun readFromJsonFileAsync(): Result<Category> = runCatching {
        withContext(Dispatchers.IO) {
            val file = File(context.filesDir, FILE_PATH)
            require(file.exists()) { "file not found" }
            json.decodeFromString(file.readText())
        }
    }
}