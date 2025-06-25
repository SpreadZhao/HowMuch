package com.spread.business.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.spread.db.service.Money
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import java.util.Calendar

class CurrMonthViewModel : ViewModel() {

    private val calendar = Calendar.getInstance()

    val currMonthMoneyRecordsFlow = Money.listenRecordsOfMonth(calendar.timeInMillis)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), listOf())

    val currMonth: Int
        get() = calendar.get(Calendar.MONTH)

    val maxDayOfCurrMonth: Int
        get() = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)

}