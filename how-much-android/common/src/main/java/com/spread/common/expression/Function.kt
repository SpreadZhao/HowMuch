package com.spread.common.expression

import java.math.BigDecimal

abstract class Function {
    abstract fun call(arguments: List<BigDecimal>): BigDecimal
}
