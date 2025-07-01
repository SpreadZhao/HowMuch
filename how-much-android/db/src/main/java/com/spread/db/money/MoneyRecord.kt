package com.spread.db.money

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable
import java.math.BigDecimal
import java.util.Date

@ConsistentCopyVisibility
@Entity(tableName = MoneyConst.TABLE_NAME_MONEY_RECORD)
@Serializable
data class MoneyRecord internal constructor(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    @ColumnInfo(name = "date") val date: Long,
    @ColumnInfo(name = "category") val category: String,
    @ColumnInfo(name = "type") val type: MoneyType,
    @ColumnInfo(name = "remark") val remark: String,
    @ColumnInfo(name = "value") val value: @Serializable(with = BigDecimalSerializer::class) BigDecimal,
) {
    override fun toString(): String {
        return "MoneyRecord(id=$id, date=${Date(date)}, category='$category', type=${type.name}, value=$value)"
    }
}