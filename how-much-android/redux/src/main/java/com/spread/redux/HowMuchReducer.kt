package com.spread.redux

import org.reduxkotlin.TypedReducer
import org.reduxkotlin.combineReducers

val HowMuchReducer: TypedReducer<HowMuchState, Action> = combineReducers(
    counterReducer
)