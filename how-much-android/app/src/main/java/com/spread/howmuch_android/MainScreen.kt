package com.spread.howmuch_android

import android.util.Log
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.activity
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.spread.business.main.MainSurface
import com.spread.business.main.MainViewModel
import com.spread.business.main.ViewType
import com.spread.business.outside.QuickAddRecordActivity
import com.spread.debug.DebugSurface
import kotlinx.serialization.Serializable

/* Graph */
sealed interface Route {
    @Serializable
    data object Main : Route

    @Serializable
    data object Statistics : Route

    @Serializable
    data object Debug : Route

    @Serializable
    data object QuickAddRecord : Route
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App() {
    val navController = rememberNavController()
    val viewModel: MainViewModel = viewModel()
    val viewType by viewModel.viewTypeFlow.collectAsState()
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
                actions = {
                    IconButton(
                        onClick = {
                            Log.d("SpreaE", "viewModel: ${viewModel.hashCode()}")
                            viewModel.changeViewType(
                                if (viewType == ViewType.YearlyStatistics) {
                                    viewModel.prevZoomInViewType
                                } else {
                                    ViewType.YearlyStatistics
                                }
                            )
                        }
                    ) {
                        if (viewType == ViewType.YearlyStatistics) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_expand),
                                contentDescription = "Expand"
                            )
                        } else {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_collapse),
                                contentDescription = "Collapse"
                            )
                        }
                    }
                },
                title = { Text("HowMuch") }
            )
        }
    ) { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {
            NavHost(
                navController = navController,
                startDestination = Route.Main
            ) {
                composable<Route.Main> { MainSurface(viewModel) }
                composable<Route.Debug> { DebugSurface() }
                activity<Route.QuickAddRecord> {
                    activityClass = QuickAddRecordActivity::class
                }
            }
        }
    }


}