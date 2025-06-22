package com.spread.debug

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.spread.db.service.Money
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class DebugViewModel : ViewModel() {

    val allMoneyRecordsFlow = Money.listenAllRecords()
        .map { it.joinToString() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), "")

    val currMonthMoneyRecordsFlow = Money.listenRecordsOfMonth(System.currentTimeMillis())
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(), listOf())

}