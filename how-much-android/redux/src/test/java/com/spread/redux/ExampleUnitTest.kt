package com.spread.redux

import com.spread.redux.component.ComponentCenter
import com.spread.redux.sample.counter.CounterAction
import com.spread.redux.sample.counter.CounterComponent
import com.spread.redux.sample.counter.CounterReceiver
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

    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun testRedux() {
        ComponentCenter.add(CounterComponent(), CounterReceiver())
        ComponentCenter.build()

        ComponentCenter.dispatch(CounterAction.Increment(1))
        ComponentCenter.dispatch(CounterAction.Increment(2))
        ComponentCenter.dispatch(CounterAction.Increment(3))
        ComponentCenter.dispatch(CounterAction.Increment(0))
    }
}