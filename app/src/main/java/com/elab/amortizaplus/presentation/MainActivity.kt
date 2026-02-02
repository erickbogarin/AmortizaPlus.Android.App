package com.elab.amortizaplus.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavType
import com.elab.amortizaplus.presentation.designsystem.theme.AmortizaPlusTheme
import com.elab.amortizaplus.presentation.screens.history.HistoryDetailScreen
import com.elab.amortizaplus.presentation.screens.history.HistoryScreen
import com.elab.amortizaplus.presentation.screens.simulation.SimulationScreen
import com.elab.amortizaplus.presentation.screens.simulation.SimulationTableRoute

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
    NavHost(
        navController = navController,
        startDestination = AppRoutes.SIMULATION
    ) {
        composable(AppRoutes.SIMULATION) {
            SimulationScreen(
                onViewDetails = { navController.navigate(AppRoutes.TABLE) },
                onViewHistory = { navController.navigate(AppRoutes.HISTORY) }
            )
        }
        composable(AppRoutes.TABLE) {
            val parentEntry = remember { navController.getBackStackEntry(AppRoutes.SIMULATION) }
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
                onBack = { navController.popBackStack() }
            )
        }
    }
}

private object AppRoutes {
    const val SIMULATION = "simulation"
    const val TABLE = "table"
    const val HISTORY = "history"
    const val HISTORY_DETAIL = "history_detail"
}
