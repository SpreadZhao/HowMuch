package com.spread.db.category

import kotlinx.serialization.Serializable

@Serializable
data class Category(
    val itemList: MutableList<CategoryItem>
)

@Serializable
data class CategoryItem(
    var text: String,
    var icon: String,
    // 记录用户使用该分类项的次数
    var usageCount: Int = 0,
    // 记录用户设置该分类项置顶的时间戳，0 表示未置顶
    var setTopTS: Long = 0L
)