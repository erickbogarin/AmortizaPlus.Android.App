package com.elab.amortizaplus.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.elab.amortizaplus.presentation.designsystem.theme.AmortizaPlusTheme
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
        startDestination = "simulation"
    ) {
        composable("simulation") {
            SimulationScreen(
                onViewDetails = { navController.navigate("table") }
            )
        }
        composable("table") {
            val parentEntry = remember { navController.getBackStackEntry("simulation") }
            SimulationTableRoute(
                onBack = { navController.popBackStack() },
                parentEntry = parentEntry
            )
        }
    }
}
