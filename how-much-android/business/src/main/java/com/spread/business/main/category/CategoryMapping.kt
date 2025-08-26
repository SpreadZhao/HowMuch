package com.spread.business.main.category

import com.spread.db.category.IconId
import com.spread.ui.R

// TODO: make this configurable
private val icons = arrayOf(
    R.drawable.ic_food,             // 0
    R.drawable.ic_shopping,         // 1
    R.drawable.ic_transport,        // 2
    R.drawable.ic_housing,          // 3
    R.drawable.ic_entertainment,    // 4
    R.drawable.ic_medical,          // 5
    R.drawable.ic_education,        // 6
    R.drawable.ic_gift,             // 7
    R.drawable.ic_salary,           // 8
    R.drawable.ic_bonus,            // 9
    R.drawable.ic_investment        // 10
)

fun iconOf(index: IconId) = icons.getOrNull(index.id)