package com.spread.business.main

import android.util.Log
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarResult
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.spread.common.calendar
import com.spread.common.nowCalendar
import com.spread.db.category.CategoryRepository
import com.spread.db.money.MoneyRecord
import com.spread.db.money.MoneyType
import com.spread.db.service.Money
import com.spread.db.service.groupByDay
import com.spread.db.suggestion.SuggestionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull
import java.math.BigDecimal
import java.util.Calendar

sealed interface EditRecordDialogState {
    data class Show(val record: MoneyRecord? = null) : EditRecordDialogState
    data object Hide : EditRecordDialogState
}

sealed interface UIEvent {
    data class ShowSnackbar(
        val message: String,
        val actionLabel: String? = null,
        val withDismissAction: Boolean = false,
        val duration: SnackbarDuration = SnackbarDuration.Short,
        val onResult: suspend (SnackbarResult) -> Unit
    ) : UIEvent
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

    private var _viewTypeFlow = MutableStateFlow(ViewType.CurrMonthRecords)
    val viewTypeFlow: StateFlow<ViewType> = _viewTypeFlow

    var prevZoomInViewType: ViewType = viewTypeFlow.value
        private set

    private var _saveSuccessActionFlow = MutableStateFlow<Pair<Int?, MoneyRecord?>?>(null)
    val saveSuccessActionFlow: StateFlow<Pair<Int?, MoneyRecord?>?> = _saveSuccessActionFlow

    private var _uiEventFlow = MutableSharedFlow<UIEvent>()
    val uiEventFlow: SharedFlow<UIEvent> get() = _uiEventFlow.asSharedFlow()


    @OptIn(ExperimentalCoroutinesApi::class)
    val currMonthMoneyRecordsFlow = selectedTimeFlow
        .flatMapLatest { time ->
            Money.listenRecordsOfMonth(time).distinctUntilChanged()
        }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(),
            emptyList()
        )

    @OptIn(ExperimentalCoroutinesApi::class)
    val currYearMoneyRecordsFlow = selectedTimeFlow
        .flatMapLatest { time ->
            Money.listenRecordsOfYear(time).distinctUntilChanged()
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

    suspend fun showSnackbar(
        message: String,
        actionLabel: String? = null,
        withDismissAction: Boolean = false,
        duration: SnackbarDuration = SnackbarDuration.Short,
        onResult: suspend (SnackbarResult) -> Unit
    ) {
        _uiEventFlow.emit(
            UIEvent.ShowSnackbar(
                message,
                actionLabel,
                withDismissAction,
                duration,
                onResult
            )
        )
    }

    fun handleRecordEdit(
        insert: Boolean,
        record: MoneyRecord
    ) {
        viewModelScope.launch(Dispatchers.Main) {
            val id = withContext(Dispatchers.IO) {
                if (insert) {
                    Money.insertRecords(record).firstOrNull()
                } else {
                    Money.updateRecords(record)
                    record.id
                }
            }

            val targetGroupIndex =
                if (!insert || id == null || id <= 0) null
                else withTimeoutOrNull(300L) {
                    currMonthMoneyRecordsFlow
                        .map { records ->
                            records.groupByDay().indexOfFirst { dailyRecords ->
                                dailyRecords.any { it.id == id }
                            }
                        }
                        .first { it >= 0 }
                }

            val blinkRecord = currMonthMoneyRecordsFlow.value.find { it.id == id }
            _saveSuccessActionFlow.value = targetGroupIndex to blinkRecord
        }
    }

    inner class RecordEditState {

        val categoryRepository by lazy {
            // TODO: here's a performance issue
            CategoryRepository().apply { loadRepository() }
        }

        val suggestionRepository by lazy {
            SuggestionRepository().apply { loadRepository() }
        }

        val recordFlow: StateFlow<MoneyRecord?> = showEditRecordDialogFlow
            .map { state ->
                when (state) {
                    is EditRecordDialogState.Show -> state.record
                    else -> null
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Lazily,
                initialValue = null
            )

        val calendarFlow: StateFlow<Calendar> = showEditRecordDialogFlow
            .map { state ->
                when (state) {
                    is EditRecordDialogState.Show -> state.record?.date?.let {
                        calendar(it)
                    } ?: nowCalendar

                    else -> nowCalendar
                }
            }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.Lazily,
                initialValue = nowCalendar
            )

        private var _remarkInputFlow = MutableStateFlow("")
        val remarkInputFlow: StateFlow<String> = _remarkInputFlow

        private var _moneyInputFlow = MutableStateFlow("")
        val moneyInputFlow: StateFlow<String> = _moneyInputFlow

        private var _categoryInputFlow = MutableStateFlow("")
        val categoryInputFlow: StateFlow<String> = _categoryInputFlow

        private var _moneyTypeFlow = MutableStateFlow(MoneyType.Expense)
        val moneyTypeFlow: StateFlow<MoneyType> = _moneyTypeFlow

        fun updateRemark(remark: String) {
            _remarkInputFlow.value = remark
            Log.d("Spread", "update remark: $remark")
        }

        fun updateMoney(value: BigDecimal?) {
            _moneyInputFlow.value = value?.toString() ?: ""
            Log.d("Spread", "update value: $value")
        }

        fun updateCategory(category: String) {
            _categoryInputFlow.value = category
            Log.d("Spread", "update category: $category")
        }

        fun updateMoneyType(moneyType: MoneyType) {
            _moneyTypeFlow.value = moneyType
            Log.d("Spread", "update moneyType: $moneyType")
        }

        private fun refreshOnDialogShow(refreshActionBuilder: MutableList<(MoneyRecord?) -> Unit>.() -> Unit) {
            buildList(refreshActionBuilder).forEach { action ->
                viewModelScope.launch {
                    showEditRecordDialogFlow.collect {
                        if (it is EditRecordDialogState.Show) {
                            action(it.record)
                        }
                    }
                }
            }
        }
    }

    val recordEditState by lazy { RecordEditState() }


}