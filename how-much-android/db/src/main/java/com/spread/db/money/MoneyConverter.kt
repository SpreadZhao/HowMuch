package com.spread.db.money

import androidx.room.TypeConverter
import java.math.BigDecimal

class MoneyConverter {

    @TypeConverter
    fun fromString(value: String): BigDecimal {
        return BigDecimal(value)
    }

    @TypeConverter
    fun toString(bigDecimal: BigDecimal): String {
        return bigDecimal.toPlainString()
    }

}