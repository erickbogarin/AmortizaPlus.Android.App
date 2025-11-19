package com.elab.amortizaplus.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.elab.amortizaplus.presentation.designsystem.preview.ThemeShowcase
import com.elab.amortizaplus.presentation.designsystem.theme.AmortizaPlusTheme
import com.elab.amortizaplus.presentation.screens.preview.SimulationPreviewScreen
import com.elab.amortizaplus.presentation.screens.simulation.SimulationScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AmortizaPlusTheme() {
                SimulationScreen()
            }
        }
    }
}
