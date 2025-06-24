package com.spread.common

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun dateStrToDate(dateStr: String?, format: String): Date? {
    if (dateStr.isNullOrEmpty()) {
        return null
    }
    val dateFormat = SimpleDateFormat(format, Locale.getDefault())
    return dateFormat.parse(dateStr) ?: null
}