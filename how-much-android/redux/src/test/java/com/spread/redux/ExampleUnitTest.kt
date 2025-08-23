package com.spread.redux

import com.spread.redux.component.ComponentCenter
import com.spread.redux.sample.counter.CounterAction
import com.spread.redux.sample.counter.CounterComponent
import com.spread.redux.sample.counter.CounterReceiver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import java.util.logging.Logger

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {

    companion object {
        private val logger = Logger.getLogger("ExampleUnitTest")
    }

    private val testDispatcher = StandardTestDispatcher()

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setup() {
        // 设置测试环境的 Main Dispatcher
        Dispatchers.setMain(testDispatcher)
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun testRedux() {
        ComponentCenter.addAll(CounterComponent() to CounterReceiver())
        ComponentCenter.build()

        // 这里如果不等待，那么多次action的修改会被合并
        ComponentCenter.dispatch(CounterAction.Increment(1))
        testDispatcher.scheduler.advanceUntilIdle()
        ComponentCenter.dispatch(CounterAction.Increment(2))
        testDispatcher.scheduler.advanceUntilIdle()
        ComponentCenter.dispatch(CounterAction.Increment(3))
        testDispatcher.scheduler.advanceUntilIdle()
        ComponentCenter.dispatch(CounterAction.Increment(0))
        testDispatcher.scheduler.advanceUntilIdle()
    }
}