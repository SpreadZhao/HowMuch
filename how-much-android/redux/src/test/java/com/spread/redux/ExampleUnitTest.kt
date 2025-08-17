package com.spread.redux

import org.junit.Test

import org.junit.Assert.*
import org.reduxkotlin.threadsafe.createThreadSafeStore
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
        val store = createThreadSafeStore(reducer, CounterState())

        val unsubscribe = store.subscribe { logger.info("state: ${store.state}")}
        store.dispatch(CounterAction.Increment())
        store.dispatch(CounterAction.Increment(2))
        store.dispatch(CounterAction.Decrement(3))
        unsubscribe()
        store.dispatch(CounterAction.Increment())
        store.dispatch(CounterAction.Increment())
        store.dispatch(CounterAction.Decrement())
    }
}