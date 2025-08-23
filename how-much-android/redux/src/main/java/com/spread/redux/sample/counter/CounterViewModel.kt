package com.spread.redux.sample.counter

import androidx.lifecycle.ViewModel
import com.spread.redux.sample.counter.CounterState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class CounterViewModel : ViewModel() {

    private val _counterState = MutableStateFlow(CounterState())
    val counterState: StateFlow<CounterState> = _counterState

    fun increment(value: Int) {
        _counterState.value = _counterState.value.copy(count = _counterState.value.count + value)
    }

    fun decrement(value: Int) {
        _counterState.value = _counterState.value.copy(count = _counterState.value.count - value)
    }
}