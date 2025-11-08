package com.elab.amortizaplus.presentation.ui.screens.form

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import com.elab.amortizaplus.presentation.ui.theme.AmortizaPlusTheme
import com.elab.amortizaplus.presentation.ui.theme.Dimens
import com.elab.amortizaplus.presentation.ui.theme.Spacing

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimulationFormScreen() {
    var loanAmount by remember { mutableStateOf("") }
    var interestRate by remember { mutableStateOf("") }
    var terms by remember { mutableStateOf("") }

    AmortizaPlusTheme {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Nova Simulação") },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(Spacing.large),
                verticalArrangement = Arrangement.spacedBy(Spacing.large),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                OutlinedTextField(
                    value = loanAmount,
                    onValueChange = { loanAmount = it },
                    label = { Text("Valor do empréstimo (R$)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = interestRate,
                    onValueChange = { interestRate = it },
                    label = { Text("Taxa de juros (%)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = terms,
                    onValueChange = { terms = it },
                    label = { Text("Prazo (meses)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Button(
                    onClick = { /* simular futuramente */ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(Dimens.buttonHeightMedium)
                ) {
                    Text("Simular", style = MaterialTheme.typography.titleSmall)
                }

                if (loanAmount.isNotBlank() && interestRate.isNotBlank() && terms.isNotBlank()) {
                    Text(
                        text = "Prévia: R$ $loanAmount em $terms meses a $interestRate%",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewSimulationForm() {
    AmortizaPlusTheme {
        SimulationFormScreen()
    }
}