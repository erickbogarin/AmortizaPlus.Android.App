package com.elab.amortizaplus.presentation.screens.home

import androidx.compose.runtime.Composable

@Composable
fun HomeRoute(
    onStartSimulation: () -> Unit
) {
    HomeScreen(
        onStartSimulation = onStartSimulation
    )
}
