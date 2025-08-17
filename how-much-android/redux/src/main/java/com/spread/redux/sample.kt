package com.spread.redux

import org.reduxkotlin.Reducer


val reducer: Reducer<Int> = { state, action ->
    when (action) {
        is Increment -> state + 1
        is Decrement -> state - 1
        else -> state
    }
}

class Increment
class Decrement