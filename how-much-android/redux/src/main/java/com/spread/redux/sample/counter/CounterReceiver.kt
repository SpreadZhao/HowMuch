package com.spread.redux.sample.counter

import com.spread.redux.Action
import com.spread.redux.component.ComponentReceiver
import java.util.logging.Logger

class CounterReceiver : ComponentReceiver<CounterComponent, CounterState, CounterViewModel>() {

    companion object {
        private val logger = Logger.getLogger("CounterReceiver")
    }

    private var viewModel: CounterViewModel? = null

    override fun onBindComponent(component: CounterComponent) {
        viewModel = component.getViewModel()
    }

    override fun onReceiveAction(prevState: CounterState, action: Action): CounterState {
        val vm = viewModel ?: return prevState

        when (action) {
            is CounterAction.Increment -> vm.increment(action.value)
            is CounterAction.Decrement -> vm.decrement(action.value)
        }

        // 返回最新 State
        // todo 这里看看能不能支持下 flow.collect 自动同步
        return vm.counterState.value
    }

    override fun onBindStore() {
        observe(CounterState::count) { oldValue, newValue ->
            logger.info("CounterReceiver observed new count = $newValue, last count = $oldValue")
        }
    }
}
