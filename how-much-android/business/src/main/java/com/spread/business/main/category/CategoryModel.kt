package com.spread.business.main.category

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.runtime.toMutableStateList
import com.spread.db.category.Category
import com.spread.db.category.CategoryItem

data class CategoryModel (
    val itemList: SnapshotStateList<CategoryItemModel>
)

data class CategoryItemModel(
    val text: MutableState<String>,
    val icon: MutableState<String>
)

fun Category.toCategoryModel(): CategoryModel {
    val stateItems = this.itemList.map { item ->
        CategoryItemModel(
            text = mutableStateOf(item.text),
            icon = mutableStateOf(item.icon)
        )
    }

    return CategoryModel(
        itemList = stateItems.toMutableStateList()
    )
}

fun CategoryModel.toCategory(): Category {
    // 提取状态值
    val dataItems = this.itemList.map { itemModel ->
        CategoryItem(
            text = itemModel.text.value,
            icon = itemModel.icon.value
        )
    }.toMutableList()

    return Category(itemList = dataItems)
}