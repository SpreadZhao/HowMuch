package com.spread.db.category

import kotlinx.serialization.Serializable

@Serializable
data class Category(
    val itemList: List<CategoryItem>
)

@Serializable
class CategoryItem(
    val text: String,
    val icon: String
)