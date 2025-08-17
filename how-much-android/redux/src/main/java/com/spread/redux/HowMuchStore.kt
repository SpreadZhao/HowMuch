package com.spread.redux

import org.reduxkotlin.TypedStore
import org.reduxkotlin.threadsafe.createTypedThreadSafeStore

val HowMuchStore = createTypedThreadSafeStore<HowMuchState, Action>(HowMuchReducer, HowMuchState())

fun dispatchAction(action: Action) = HowMuchStore.dispatch(action)

fun <State, Sub, Action> TypedStore<State, Action>.select(
    selector: (State) -> Sub,
    onChange: (Sub) -> Unit
): () -> Unit {
    var lastValue = selector(state)
    val unsubscribe = subscribe {
        val newValue = selector(state)
        if (newValue != lastValue) {
            lastValue = newValue
            onChange(newValue)
        }
    }
    return unsubscribe
}
