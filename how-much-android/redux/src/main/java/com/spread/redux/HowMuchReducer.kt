package com.spread.redux

import com.spread.redux.sample.counterReducer
import org.reduxkotlin.TypedReducer
import org.reduxkotlin.combineReducers

val HowMuchReducer: TypedReducer<HowMuchState, Action> = combineReducers(
    counterReducer
)