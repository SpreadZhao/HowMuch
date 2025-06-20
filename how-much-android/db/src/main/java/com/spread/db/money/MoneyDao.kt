package com.spread.db.money

import androidx.room.Dao
import androidx.room.Query

@Dao
interface MoneyDao {

    @Query("SELECT * from ${MoneyConst.TABLE_NAME_MONEY_RECORD}")
    fun getAllRecords(): List<MoneyRecord>



}