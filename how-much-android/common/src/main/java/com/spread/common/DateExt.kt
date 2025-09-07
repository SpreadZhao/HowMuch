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
    return runCatching { dateFormat.parse(dateStr) }.getOrNull()
}

fun Date.dateStr(format: String): String {
    val dateFormat = SimpleDateFormat(format, Locale.getDefault())
    return dateFormat.format(this)
}

fun nowStr(format: String): String {
    return Date().dateStr(format)
}

fun timeInMillisToDateStr(timeInMillis: Long, format: String = DATE_FORMAT_YEAR_MONTH_DAY_STR): String {
    val date = Date(timeInMillis)
    return date.dateStr(format)
}

val nowCalendar: Calendar
    get() = Calendar.getInstance()

fun calendar(timeInMillis: Long): Calendar {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = timeInMillis
    return calendar
}
fun dayOfWeekStr(dayOfWeek: Int): String = when (dayOfWeek) {
    Calendar.SUNDAY -> "SUN"
    Calendar.MONDAY -> "MON"
    Calendar.TUESDAY -> "TUE"
    Calendar.WEDNESDAY -> "WED"
    Calendar.THURSDAY -> "THU"
    Calendar.FRIDAY -> "FRI"
    Calendar.SATURDAY -> "SAT"
    else -> ""
}