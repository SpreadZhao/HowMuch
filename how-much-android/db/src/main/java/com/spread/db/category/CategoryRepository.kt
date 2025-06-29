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
            itemList = listOf(
                CategoryItem("餐饮", "🍔"),
                CategoryItem("购物", "🛒"),
                CategoryItem("交通", "🚕"),
                CategoryItem("住房", "🏠"),
                CategoryItem("娱乐", "🎮"),
                CategoryItem("医疗", "🏥"),
                CategoryItem("教育", "📚"),
                CategoryItem("旅行", "✈️"),
                CategoryItem("人情", "🎁"),
                CategoryItem("工资", "💰"),
                CategoryItem("奖金", "🎯"),
                CategoryItem("投资", "📈")
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