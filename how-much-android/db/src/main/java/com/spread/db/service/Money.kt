package com.spread.db.service

import androidx.room.Room
import com.spread.common.HowMuch
import com.spread.db.money.MoneyDatabase
import com.spread.db.money.MoneyRecord

object Money {

    private val database = Room.databaseBuilder(
        HowMuch.application,
        MoneyDatabase::class.java,
        "money-database"
    ).build()

    fun getAllRecords(): List<MoneyRecord> {
        return database.moneyDao().getAllRecords()
    }

    fun insertRecords(vararg records: MoneyRecord) {
        database.moneyDao().insertRecords(*records)
    }

}