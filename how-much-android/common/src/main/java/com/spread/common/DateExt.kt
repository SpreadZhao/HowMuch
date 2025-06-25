package com.spread.common

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

const val DATE_FORMAT_MONTH_DAY_STR = "MMM dd"

fun dateStrToDate(dateStr: String?, format: String): Date? {
    if (dateStr.isNullOrEmpty()) {
        return null
    }
    val dateFormat = SimpleDateFormat(format, Locale.getDefault())
    return dateFormat.parse(dateStr) ?: null
}

fun Date.dateStr(format: String): String {
    val dateFormat = SimpleDateFormat(format, Locale.getDefault())
    return dateFormat.format(this)
}