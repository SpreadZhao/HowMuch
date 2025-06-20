package com.spread.db.money

import androidx.room.Database

@Database(entities = [MoneyRecord::class], version = 1)
abstract class MoneyDatabase {
    abstract fun moneyDao(): MoneyDao
}