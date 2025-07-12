package com.spread.howmuch_android

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.spread.business.main.CurrMonthSurface
import com.spread.debug.DebugSurface
import kotlinx.serialization.Serializable

/* Graph */
sealed interface Route {
    @Serializable
    data object Monthly : Route

    @Serializable
    data object Statistics : Route

    @Serializable
    data object Debug : Route
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen() {
    val navController = rememberNavController()
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                modifier = Modifier.pointerInput(Unit) {
                    detectTapGestures(
                        onLongPress = {
                            navController.navigate(Route.Debug)
                        }
                    )
                },
                title = { Text("HowMuch") }
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            NavHost(
                navController = navController,
                startDestination = Route.Monthly
            ) {
                composable<Route.Monthly> { CurrMonthSurface() }
                composable<Route.Debug> { DebugSurface() }
            }
        }
    }


}