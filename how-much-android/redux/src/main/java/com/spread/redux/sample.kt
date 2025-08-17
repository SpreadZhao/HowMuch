package com.spread.redux

import org.reduxkotlin.Reducer
import org.reduxkotlin.typedReducer

data class CounterState(val count: Int = 0)

val reducer: Reducer<CounterState> = typedReducer<CounterState, CounterAction> { state, action ->
    when (action) {
        is CounterAction.Increment -> state.copy(count = state.count + action.value)
        is CounterAction.Decrement -> state.copy(count = state.count - action.value)
    }
}