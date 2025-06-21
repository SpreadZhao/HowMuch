package com.spread.db.money

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [MoneyRecord::class], version = 1)
@TypeConverters(MoneyConverter::class)
abstract class MoneyDatabase : RoomDatabase() {
    abstract fun moneyDao(): MoneyDao
}