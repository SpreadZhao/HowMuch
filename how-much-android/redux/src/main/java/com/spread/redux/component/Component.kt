package com.spread.redux.component

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import com.spread.redux.ReduxState
import kotlin.reflect.KClass


typealias View = @Composable () -> Unit

abstract class Component<out VM: ViewModel, out S: ReduxState> {

    private var mViewModel: VM? = null

    fun getView(): View? = null

    open fun getViewModel(): VM? = mViewModel

    abstract fun getInitialState(): S
}
