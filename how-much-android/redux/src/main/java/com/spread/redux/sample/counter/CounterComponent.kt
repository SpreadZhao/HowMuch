package com.spread.redux.sample.counter

import com.spread.redux.component.Component
import kotlin.reflect.KClass

class CounterComponent : Component<CounterViewModel, CounterState>() {

    private val viewModel = CounterViewModel()

    override fun getViewModel(): CounterViewModel = viewModel

    override fun getInitialState(): CounterState {
        return CounterState()
    }
}
