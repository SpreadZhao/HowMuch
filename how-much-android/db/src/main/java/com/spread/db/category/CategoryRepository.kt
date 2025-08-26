package com.spread.db.category

import com.spread.common.HowMuch
import com.spread.common.json
import java.io.File

class CategoryRepository {

    companion object {
        private const val FILE_PATH: String = "category.json"

        // 预设分类
        private val defaultCategories = mutableListOf(
            CategoryItem("餐饮", ICON_ID_0),
            CategoryItem("购物", ICON_ID_1),
            CategoryItem("交通", ICON_ID_2),
            CategoryItem("住房", ICON_ID_3),
            CategoryItem("娱乐", ICON_ID_4),
            CategoryItem("医疗", ICON_ID_5),
            CategoryItem("教育", ICON_ID_6),
            CategoryItem("旅行", ICON_ID_2),
            CategoryItem("人情", ICON_ID_7),
            CategoryItem("工资", ICON_ID_8),
            CategoryItem("奖金", ICON_ID_9),
            CategoryItem("投资", ICON_ID_10)
        )
    }

    private val _categories = mutableListOf<CategoryItem>()
    val categories: List<CategoryItem>
        get() = _categories

    fun loadCategory() {
        val file = File(HowMuch.application.filesDir, FILE_PATH)
        if (!file.exists()) {
            file.createNewFile()
            file.writeText(json.encodeToString(defaultCategories))
            _categories.addAll(defaultCategories)
            return
        }
        val text = file.readText()
        _categories.addAll(json.decodeFromString<List<CategoryItem>>(text))
    }

}