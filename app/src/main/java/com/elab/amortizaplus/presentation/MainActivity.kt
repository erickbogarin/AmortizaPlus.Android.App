package com.elab.amortizaplus.presentation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.elab.amortizaplus.presentation.ui.screens.preview.SimulationPreviewScreen
import com.elab.amortizaplus.presentation.ui.theme.AmortizaPlusTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AmortizaPlusTheme {
                SimulationPreviewScreen()
            }
        }
    }
}
