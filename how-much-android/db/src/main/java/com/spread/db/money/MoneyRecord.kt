package com.spread.db.money

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.math.BigDecimal
import java.util.Date

@Entity(tableName = MoneyConst.TABLE_NAME_MONEY_RECORD)
data class MoneyRecord constructor(
    @PrimaryKey val id: Long,
    @ColumnInfo(name = "date") val date: Date,
    @ColumnInfo(name = "category") val category: String,
    @ColumnInfo(name = "type") val type: MoneyType,
    @ColumnInfo(name = "value") val value: BigDecimal,
)