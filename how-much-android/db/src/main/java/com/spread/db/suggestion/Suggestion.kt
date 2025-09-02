package com.spread.db.suggestion

import kotlinx.serialization.Serializable

@Serializable
data class SuggestionItem(
    val text: String,
    var useCount: Int = 0,
    var lastUseTime: Long = System.currentTimeMillis()
)
