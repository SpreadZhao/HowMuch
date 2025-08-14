package com.spread.business.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.spread.db.money.MoneyRecord
import com.spread.db.service.Money
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.util.Calendar

sealed interface EditRecordDialogState {
    data class Show(val record: MoneyRecord? = null) : EditRecordDialogState
    data object Hide : EditRecordDialogState
}

enum class ViewType {
    CurrMonthRecords,
    MonthlyStatistics,
    YearlyStatistics
}

class MainViewModel : ViewModel() {

    companion object {
        private val calendar = Calendar.getInstance()
        private fun getMonthStartTime(time: Long): Long {
            return calendar.apply {
                timeInMillis = time
                set(Calendar.DAY_OF_MONTH, 1)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis
        }
    }

    private val selectedTimeFlow = MutableStateFlow(getMonthStartTime(calendar.timeInMillis))

    private var _showEditRecordDialogFlow =
        MutableStateFlow<EditRecordDialogState>(EditRecordDialogState.Hide)
    val showEditRecordDialogFlow: StateFlow<EditRecordDialogState> = _showEditRecordDialogFlow

    private var _viewTypeFlow = MutableStateFlow<ViewType>(ViewType.CurrMonthRecords)
    val viewTypeFlow: StateFlow<ViewType> = _viewTypeFlow

    var prevZoomInViewType: ViewType = viewTypeFlow.value
        private set

    @OptIn(ExperimentalCoroutinesApi::class)
    val currMonthMoneyRecordsFlow = selectedTimeFlow
        .flatMapLatest { time ->
            Money.listenRecordsOfMonth(time)
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(),
            emptyList()
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    val currYearMoneyRecordsFlow = selectedTimeFlow
        .flatMapLatest { time ->
            Money.listenRecordsOfYear(time)
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(),
            emptyList()
        )

    val selectedYearFlow: StateFlow<Int> = selectedTimeFlow
        .map { timeInMillis ->
            calendar.apply {
                this.timeInMillis = timeInMillis
            }.get(Calendar.YEAR)
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(),
            calendar.get(Calendar.YEAR)
        )

    val selectedMonthFlow: StateFlow<Int> = selectedTimeFlow
        .map { timeInMillis ->
            calendar.apply {
                this.timeInMillis = timeInMillis
            }.get(Calendar.MONTH)
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(),
            calendar.get(Calendar.MONTH)
        )

    val maxDayOfSelectedMonthFlow: StateFlow<Int> = selectedTimeFlow
        .map { timeInMillis ->
            calendar.apply {
                this.timeInMillis = timeInMillis
            }.getActualMaximum(Calendar.DAY_OF_MONTH)
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(),
            calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
        )

    fun select(year: Int, month: Int) {
        selectedTimeFlow.value = calendar.run {
            clear()
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month)
            set(Calendar.DAY_OF_MONTH, 1)
            timeInMillis
        }
    }

    fun selectMonthDelta(delta: Int) {
        selectedTimeFlow.value = calendar.run {
            clear()
            timeInMillis = selectedTimeFlow.value
            add(Calendar.MONTH, delta)
            timeInMillis
        }
    }

    fun selectYearDelta(delta: Int) {
        selectedTimeFlow.value = calendar.run {
            clear()
            timeInMillis = selectedTimeFlow.value
            add(Calendar.YEAR, delta)
            timeInMillis
        }
    }

    fun showEditRecordDialog(record: MoneyRecord? = null) {
        _showEditRecordDialogFlow.value = EditRecordDialogState.Show(record)
    }

    fun hideEditRecordDialog() {
        _showEditRecordDialogFlow.value = EditRecordDialogState.Hide
    }

    fun changeViewType(viewType: ViewType) {
        viewTypeFlow.value.takeIf { it != ViewType.YearlyStatistics }
            ?.let { prevZoomInViewType = it }
        _viewTypeFlow.value = viewType
    }

}