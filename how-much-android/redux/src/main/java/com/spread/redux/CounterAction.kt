package com.spread.redux

sealed class CounterAction: Action {
    class Increment(val value: Int = 1) : CounterAction()
    class Decrement(val value: Int = 1) : CounterAction()
}