package com.spread.redux.sample.counter

import androidx.lifecycle.viewModelScope
import com.spread.redux.Action
import com.spread.redux.component.ComponentReceiver
import kotlinx.coroutines.launch
import java.util.logging.Logger

class CounterReceiver : ComponentReceiver<CounterComponent, CounterState, CounterViewModel>() {

    companion object {
        private val logger = Logger.getLogger("CounterReceiver")
    }

    private var viewModel: CounterViewModel? = null

    override fun onBindComponent(component: CounterComponent) {
        viewModel = component.getViewModel()
        val viewModelScope = viewModel?.viewModelScope

        // Flow -> Redux State 同步
        viewModelScope?.launch {
            viewModel?.counterState?.collect { counterState ->
                dispatchAction(CounterAction.InternalStateUpdated(counterState))
            }
        }
    }

    override fun onReceiveAction(prevState: CounterState, action: Action): CounterState {
        val vm = viewModel ?: return prevState

        when (action) {
            is CounterAction.Increment -> vm.increment(action.value)
            is CounterAction.Decrement -> vm.decrement(action.value)
            is CounterAction.InternalStateUpdated -> return action.state
        }

        return prevState
    }

    override fun onBindStore() {
        observe(CounterState::count) { oldValue, newValue ->
            logger.info("CounterReceiver observed new count = $newValue, last count = $oldValue")
        }
    }
}
