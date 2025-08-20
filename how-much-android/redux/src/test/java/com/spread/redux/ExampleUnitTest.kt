package com.spread.redux

import com.spread.redux.sample.CounterAction
import org.junit.Assert.assertEquals
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
//        val appStateUnsubscribe = HowMuchStore.subscribe { logger.info("HowMuchState changed: ${HowMuchStore.state.hashCode()}") }
        val unsubscribe =
            HowMuchStore.select({ it.counterState }) { logger.info("CounterState changed: $it") }
        dispatchAction(CounterAction.Increment(0))
        dispatchAction(CounterAction.Increment(0))
        dispatchAction(CounterAction.Increment(1))
        dispatchAction(CounterAction.Increment(2))
        dispatchAction(CounterAction.Decrement(3))
        unsubscribe()
//        appStateUnsubscribe()
        dispatchAction(CounterAction.Increment())
        dispatchAction(CounterAction.Increment())
        dispatchAction(CounterAction.Decrement())
    }
}