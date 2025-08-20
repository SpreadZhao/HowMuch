package com.spread.redux.sample

import com.spread.redux.HowMuchState
import org.reduxkotlin.typedReducer

data class CounterState(val count: Int = 0)

val counterReducer = typedReducer<HowMuchState, CounterAction> { state, action ->
    when (action) {
        is CounterAction.Increment -> {
            val newCount = state.counterState.count + action.value
            if (newCount != state.counterState.count) {
                state.copy(counterState = CounterState(newCount))
            } else state
        }

        is CounterAction.Decrement -> {
            val newCount = state.counterState.count - action.value
            if (newCount != state.counterState.count) {
                state.copy(counterState = CounterState(newCount))
            } else state
        }
    }
}