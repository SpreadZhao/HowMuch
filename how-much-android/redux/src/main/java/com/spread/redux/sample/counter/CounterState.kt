package com.spread.redux.sample.counter

import com.spread.redux.ReduxState

data class CounterState(val count: Int = 0) : ReduxState()