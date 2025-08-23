package com.spread.redux.sample.counter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CounterViewModel : ViewModel() {

    private val _counterState = MutableStateFlow(CounterState())
    val counterState: StateFlow<CounterState> = _counterState

    fun increment(value: Int) {
        viewModelScope.launch {
            _counterState.emit(_counterState.value.copy(count = _counterState.value.count + value))
        }
    }

    fun decrement(value: Int) {
        viewModelScope.launch {
            _counterState.emit(_counterState.value.copy(count = _counterState.value.count - value))
        }
    }
}