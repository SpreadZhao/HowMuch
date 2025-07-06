package com.spread.howmuch_android

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.spread.business.main.CurrMonthSurface
import kotlinx.serialization.Serializable

/* Graph */
sealed interface Route {
    @Serializable
    data object Monthly : Route

    @Serializable
    data object Statistics : Route
}


@Composable
fun MainScreen() {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Route.Monthly
    ) {
        composable<Route.Monthly> { CurrMonthSurface() }
    }

}