package com.spread.redux.sample

import com.spread.redux.Action

sealed class CounterAction: Action {
    class Increment(val value: Int = 1) : CounterAction()
    class Decrement(val value: Int = 1) : CounterAction()
}