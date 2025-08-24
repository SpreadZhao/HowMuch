package com.spread.redux.component

import androidx.lifecycle.ViewModel
import com.spread.redux.Action
import com.spread.redux.ReduxState
import org.reduxkotlin.TypedReducer
import org.reduxkotlin.TypedStore
import org.reduxkotlin.threadsafe.createTypedThreadSafeStore
import kotlin.let
import kotlin.reflect.KClass


typealias HowMuchState = Map<KClass<out Component<*, *>>, ReduxState>
typealias HowMuchStore = TypedStore<HowMuchState, Action>

object ComponentCenter {

    // 全局 Store
    private lateinit var mStore: HowMuchStore
    val store: HowMuchStore
        get() = if (::mStore.isInitialized) mStore
        else error("Store not initialized. Call ComponentCenter.build() first.")

    // Component 与 Receiver 的注册表
    private val components = mutableMapOf<KClass<out Component<*, *>>, Component<*, *>>()
    private val receivers = mutableMapOf<KClass<out Component<*, *>>, ComponentReceiver<*, *, *>>()

    fun <C : Component<VM, S>, S : ReduxState, VM : ViewModel> add(
        component: C,
        receiver: ComponentReceiver<C, S, VM>? = null
    ) {
        val clazz = component::class
        components[clazz] = component

        receiver?.let {
            receivers[clazz] = it
            it.onBindComponent(component)
        }
    }

    fun <C : Component<VM, S>, S : ReduxState, VM : ViewModel> addAll(
        vararg entries: Pair<C, ComponentReceiver<C, S, VM>?>
    ) {
        entries.forEach { (component, receiver) ->
            val clazz = component::class
            components[clazz] = component
            receiver?.let {
                receivers[clazz] = it
                it.onBindComponent(component)
            }
        }
    }


    /**
     * 构建全局 Store，必须在使用前调用
     */
    fun build() {
        val initialState = components.mapValues { (_, comp) ->
            comp.getInitialState()
        }

        val rootReducer: TypedReducer<Map<KClass<out Component<*, *>>, ReduxState>, Action> =
            { prevState, action ->
                var hasChanged = false
                val newState = prevState.mapValues { (clazz, state) ->
                    val receiver = receivers[clazz]?.typed<Component<*, *>, ReduxState, ViewModel>()
                    if (receiver != null) {
                        val updatedState = receiver.onReceiveAction(state, action)
                        if (updatedState != state) hasChanged = true
                        updatedState
                    } else state
                }
                if (hasChanged) newState else prevState
            }

        mStore = createTypedThreadSafeStore(rootReducer, initialState)
        receivers.values.forEach { it.bindStore(mStore) }
    }

    /**
     * useSelector: 监听指定 Component 的 State 或字段
     * @return unsubscribe 方法
     */
    inline fun <reified S : ReduxState, R> useSelector(
        crossinline selector: (S) -> R,
        noinline onChange: (R, R) -> Unit
    ): () -> Unit {
        val currentStore = store

        var lastValue: R = run {
            val state = currentStore.state.values.filterIsInstance<S>().firstOrNull()
                ?: error("State not found for ${S::class.simpleName}")
            selector(state)
        }

        val unsubscribe = currentStore.subscribe {
            val state = currentStore.state.values.filterIsInstance<S>().firstOrNull()
                ?: error("State not found for ${S::class.simpleName}")
            val newValue = selector(state)
            if (newValue != lastValue) {
                onChange(lastValue, newValue)
                lastValue = newValue
            }
        }

        return unsubscribe
    }

    fun dispatch(action: Action) {
        mStore.dispatch(action)
    }
}
