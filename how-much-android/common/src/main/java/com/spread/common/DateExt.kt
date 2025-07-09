package com.spread.common

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

const val DATE_FORMAT_MONTH_DAY_STR = "MMM dd"
const val DATE_FORMAT_YEAR_MONTH_DAY_STR = "yyyy-MM-dd"
const val DATE_FORMAT_YEAR_MONTH_DAY_TIME_STR = "yyyy-MM-dd HH:mm:ss"

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

fun nowStr(format: String): String {
    return Date().dateStr(format)
}

fun timeInMillisToDateStr(timeInMillis: Long, format: String): String {
    val date = Date(timeInMillis)
    return date.dateStr(format)
}

val nowCalendar: Calendar
    get() = Calendar.getInstance()