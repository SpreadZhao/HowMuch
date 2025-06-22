package com.spread.db.money

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MoneyDao {

    @Query("SELECT * from ${MoneyConst.TABLE_NAME_MONEY_RECORD}")
    suspend fun getAllRecords(): List<MoneyRecord>

    @Query("SELECT * from ${MoneyConst.TABLE_NAME_MONEY_RECORD}")
    fun listenAllRecords(): Flow<List<MoneyRecord>>

    @Insert
    suspend fun insertRecords(vararg records: MoneyRecord)

    @Query("SELECT * FROM ${MoneyConst.TABLE_NAME_MONEY_RECORD} WHERE date BETWEEN :startTime AND :endTime ORDER BY date ASC")
    suspend fun getRecordsByDateRange(startTime: Long, endTime: Long): List<MoneyRecord>

    @Query("SELECT * FROM ${MoneyConst.TABLE_NAME_MONEY_RECORD} WHERE date BETWEEN :startTime AND :endTime ORDER BY date ASC")
    fun listenRecordsByDateRange(startTime: Long, endTime: Long): Flow<List<MoneyRecord>>

}