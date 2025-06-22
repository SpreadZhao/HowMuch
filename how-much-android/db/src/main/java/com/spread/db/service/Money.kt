package com.spread.db.service

import androidx.room.Room
import com.spread.common.HowMuch
import com.spread.db.money.MoneyDatabase
import com.spread.db.money.MoneyRecord
import kotlinx.coroutines.flow.Flow
import java.util.Calendar
import java.util.Date

object Money {

    private val database = Room.databaseBuilder(
        HowMuch.application,
        MoneyDatabase::class.java,
        "money-database"
    ).build()

    suspend fun getAllRecords(): List<MoneyRecord> {
        return database.moneyDao().getAllRecords()
    }

    fun listenAllRecords(): Flow<List<MoneyRecord>> {
        return database.moneyDao().listenAllRecords()
    }

    suspend fun insertRecords(vararg records: MoneyRecord) {
        database.moneyDao().insertRecords(*records)
    }

    suspend fun getTodayRecords() = getRecordsOfDay(System.currentTimeMillis())

    suspend fun getRecordsOfDay(time: Long): List<MoneyRecord> {
        val (start, end) = getDayRangeFromTime(time)
        return database.moneyDao().getRecordsByDateRange(start, end)
    }

    fun listenRecordsOfMonth(time: Long): Flow<List<MoneyRecord>> {
        val (start, end) = getMonthRangeFromTime(time)
        return database.moneyDao().listenRecordsByDateRange(start, end)
    }

    private fun getMonthRangeFromTime(time: Long): Pair<Long, Long> {
        val calendar = Calendar.getInstance()
        calendar.time = Date(time)

        // 设置为当月1号 00:00:00.000
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startTime = calendar.timeInMillis

        // 设置为下月1号 00:00:00.000 再减1毫秒
        calendar.add(Calendar.MONTH, 1)
        val endTime = calendar.timeInMillis - 1

        return startTime to endTime
    }

    private fun getDayRangeFromTime(time: Long): Pair<Long, Long> {
        val calendar = Calendar.getInstance()
        calendar.time = Date(time)

        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startTime = calendar.timeInMillis
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        val endTime = calendar.timeInMillis

        return startTime to endTime
    }


}