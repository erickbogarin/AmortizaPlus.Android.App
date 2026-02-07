package com.elab.amortizaplus.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavType
import androidx.compose.material3.Scaffold
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material3.Icon
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.unit.dp
import com.elab.amortizaplus.presentation.designsystem.theme.AmortizaPlusTheme
import com.elab.amortizaplus.presentation.screens.history.HistoryDetailScreen
import com.elab.amortizaplus.presentation.screens.history.HistoryScreen
import com.elab.amortizaplus.presentation.screens.simulation.SimulationFormRoute
import com.elab.amortizaplus.presentation.screens.simulation.SimulationResultRoute
import com.elab.amortizaplus.presentation.screens.simulation.SimulationTableRoute
import com.elab.amortizaplus.presentation.screens.simulation.resources.SimulationTexts

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AmortizaPlusTheme { AppNavHost() }
        }
    }
}

@Composable
private fun AppNavHost() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val currentReturnTo = navBackStackEntry?.arguments?.getString(AppRoutes.RETURN_TO_ARG)
    val showBottomBar =
        currentRoute == AppRoutes.HOME ||
            (currentRoute == AppRoutes.SIMULATION_ROUTE && currentReturnTo.isNullOrBlank()) ||
            currentRoute == AppRoutes.HISTORY

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(tonalElevation = 0.dp) {
                    NavigationBarItem(
                        selected = currentRoute == AppRoutes.HOME,
                        onClick = {
                            navController.navigate(AppRoutes.HOME) {
                                popUpTo(AppRoutes.HOME) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(Icons.Filled.Home, contentDescription = null) },
                        label = { Text(SimulationTexts.homeNavLabel) }
                    )
                    NavigationBarItem(
                        selected = currentRoute == AppRoutes.SIMULATION_ROUTE,
                        onClick = {
                            navController.navigate(AppRoutes.SIMULATION) {
                                popUpTo(AppRoutes.HOME) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(Icons.Filled.Edit, contentDescription = null) },
                        label = { Text(SimulationTexts.simulationNavLabel) }
                    )
                    NavigationBarItem(
                        selected = currentRoute == AppRoutes.HISTORY,
                        onClick = {
                            navController.navigate(AppRoutes.HISTORY) {
                                popUpTo(AppRoutes.HOME) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(Icons.Filled.List, contentDescription = null) },
                        label = { Text(SimulationTexts.historyNavLabel) }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = AppRoutes.HOME,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(AppRoutes.HOME) {
                com.elab.amortizaplus.presentation.screens.home.HomeRoute()
            }
            composable(
                route = AppRoutes.SIMULATION_ROUTE,
                arguments = listOf(
                    navArgument(AppRoutes.SAVED_SIMULATION_ID_ARG) {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = null
                    },
                    navArgument(AppRoutes.RETURN_TO_ARG) {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = null
                    }
                )
            ) { backStackEntry ->
                val selectedSimulationId =
                    backStackEntry.arguments?.getString(AppRoutes.SAVED_SIMULATION_ID_ARG)
                val returnToRoute =
                    backStackEntry.arguments?.getString(AppRoutes.RETURN_TO_ARG)
                SimulationFormRoute(
                    selectedSimulationId = selectedSimulationId,
                    parentEntry = backStackEntry,
                    onNavigateToResult = {
                        navController.navigate(AppRoutes.SIMULATION_RESULT)
                    },
                    onNavigateToResultWithId = { id, returnTo ->
                        if (returnTo.isNullOrBlank()) {
                            navController.navigate(AppRoutes.simulationResultWithSavedId(id))
                        } else {
                            navController.navigate(AppRoutes.simulationResultWithSavedId(id, returnTo))
                        }
                    },
                    returnToRoute = returnToRoute
                )
            }
            composable(
                route = AppRoutes.SIMULATION_RESULT_ROUTE,
                arguments = listOf(
                    navArgument(AppRoutes.SAVED_SIMULATION_ID_ARG) {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = null
                    },
                    navArgument(AppRoutes.RETURN_TO_ARG) {
                        type = NavType.StringType
                        nullable = true
                        defaultValue = null
                    }
                )
            ) { backStackEntry ->
                val selectedSimulationId =
                    backStackEntry.arguments?.getString(AppRoutes.SAVED_SIMULATION_ID_ARG)
                val returnToRoute =
                    backStackEntry.arguments?.getString(AppRoutes.RETURN_TO_ARG)
                val parentEntry = remember { navController.getBackStackEntry(AppRoutes.SIMULATION_ROUTE) }
                SimulationResultRoute(
                    parentEntry = parentEntry,
                    onViewDetails = { navController.navigate(AppRoutes.TABLE) },
                    onEditSimulation = {
                        if (!returnToRoute.isNullOrBlank()) {
                            navController.popBackStack(returnToRoute, inclusive = false)
                        } else {
                            navController.navigate(AppRoutes.SIMULATION) {
                                popUpTo(AppRoutes.SIMULATION_ROUTE) { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                    },
                    onNewSimulation = {
                        navController.navigate(AppRoutes.SIMULATION) {
                            popUpTo(AppRoutes.SIMULATION_ROUTE) { inclusive = true }
                            launchSingleTop = true
                        }
                    },
                    selectedSimulationId = selectedSimulationId,
                    returnToRoute = returnToRoute
                )
            }
            composable(AppRoutes.TABLE) {
                val parentEntry = remember { navController.getBackStackEntry(AppRoutes.SIMULATION_ROUTE) }
                SimulationTableRoute(
                    onBack = { navController.popBackStack() },
                    parentEntry = parentEntry
                )
            }
            composable(AppRoutes.HISTORY) {
                HistoryScreen(
                    onBack = { navController.popBackStack() },
                    onSimulationClick = { simulationId ->
                        navController.navigate("${AppRoutes.HISTORY_DETAIL}/$simulationId")
                    }
                )
            }
            composable(
                route = "${AppRoutes.HISTORY_DETAIL}/{simulationId}",
                arguments = listOf(navArgument("simulationId") { type = NavType.StringType })
            ) { backStackEntry ->
                val simulationId = backStackEntry.arguments?.getString("simulationId").orEmpty()
                HistoryDetailScreen(
                    simulationId = simulationId,
                    onUseSimulation = { selectedId ->
                        navController.navigate(
                            AppRoutes.simulationWithSavedId(
                                selectedId,
                                "${AppRoutes.HISTORY_DETAIL}/$simulationId"
                            )
                        )
                    },
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}

private object AppRoutes {
    const val HOME = "home"
    const val SIMULATION = "simulation"
    const val SAVED_SIMULATION_ID_ARG = "savedSimulationId"
    const val RETURN_TO_ARG = "returnTo"
    const val SIMULATION_ROUTE =
        "$SIMULATION?$SAVED_SIMULATION_ID_ARG={$SAVED_SIMULATION_ID_ARG}&$RETURN_TO_ARG={$RETURN_TO_ARG}"
    const val SIMULATION_RESULT = "simulation_result"
    const val SIMULATION_RESULT_ROUTE =
        "$SIMULATION_RESULT?$SAVED_SIMULATION_ID_ARG={$SAVED_SIMULATION_ID_ARG}&$RETURN_TO_ARG={$RETURN_TO_ARG}"
    const val TABLE = "table"
    const val HISTORY = "history"
    const val HISTORY_DETAIL = "history_detail"

    fun simulationWithSavedId(id: String): String {
        return "$SIMULATION?$SAVED_SIMULATION_ID_ARG=$id"
    }

    fun simulationWithSavedId(id: String, returnTo: String): String {
        return "$SIMULATION?$SAVED_SIMULATION_ID_ARG=$id&$RETURN_TO_ARG=$returnTo"
    }

    fun simulationResultWithSavedId(id: String): String {
        return "$SIMULATION_RESULT?$SAVED_SIMULATION_ID_ARG=$id"
    }

    fun simulationResultWithSavedId(id: String, returnTo: String): String {
        return "$SIMULATION_RESULT?$SAVED_SIMULATION_ID_ARG=$id&$RETURN_TO_ARG=$returnTo"
    }
}
