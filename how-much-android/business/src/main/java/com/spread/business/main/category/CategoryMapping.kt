package com.spread.business.main.category

import com.spread.db.category.IconId
import com.spread.ui.R

// TODO: make this configurable
private val icons = arrayOf(
    R.drawable.ic_food,
    R.drawable.ic_shopping,
    R.drawable.ic_transport,
    R.drawable.ic_housing,
    R.drawable.ic_entertainment,
    R.drawable.ic_medical,
    R.drawable.ic_education,
    R.drawable.ic_transport,
    R.drawable.ic_gift,
    R.drawable.ic_salary,
    R.drawable.ic_bonus,
    R.drawable.ic_investment
)

fun iconOf(index: IconId) = icons.getOrNull(index.id)