package com.spread.redux.component

import androidx.lifecycle.ViewModel
import com.spread.redux.Action
import com.spread.redux.ReduxState
import kotlin.reflect.KProperty1

/**
 * @param C Component类型，定义Receiver关联的Component
 * @param S Redux State类型，定义Receiver关联的暴露在Redux中的State类型
 */
abstract class ComponentReceiver<C : Component<VM, S>, S : ReduxState, VM : ViewModel> {
    protected var store: HowMuchStore? = null

    fun bindStore(store: HowMuchStore) {
        this.store = store
        onBindStore()
    }

    open fun onBindStore() {}

    open fun onBindComponent(component: C) {}

    /**
     * 在这里处理外部的请求
     */
    open fun onReceiveAction(prevState: S, action: Action): S {
        return prevState
    }

    protected fun dispatchAction(action: Action) {
        store?.dispatch(action)
    }

    /**
     * 监听某个 State 的字段（属性方式）
     */
    protected inline fun <reified T : ReduxState, R> observe(
        prop: KProperty1<T, R>,
        crossinline onChange: (R, R) -> Unit
    ): () -> Unit {
        return ComponentCenter.useSelector<T, R>(
            selector = { state -> prop.get(state) },
            onChange = { old, new -> onChange(old, new) }
        )
    }

}

@Suppress("UNCHECKED_CAST")
fun <C : Component<VM, S>, S : ReduxState, VM : ViewModel>
        ComponentReceiver<*, *, *>.typed(): ComponentReceiver<C, S, VM> {
    return this as ComponentReceiver<C, S, VM>
}
