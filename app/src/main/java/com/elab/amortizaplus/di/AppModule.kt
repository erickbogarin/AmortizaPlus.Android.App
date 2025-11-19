package com.elab.amortizaplus.di

import com.elab.amortizaplus.domain.calculator.FinancingCalculator
import com.elab.amortizaplus.domain.calculator.PriceCalculator
import com.elab.amortizaplus.domain.calculator.SacCalculator
import com.elab.amortizaplus.domain.usecase.CalculateFinancingUseCase
import com.elab.amortizaplus.presentation.screens.simulation.SimulationViewModel
import com.elab.amortizaplus.presentation.screens.simulation.validation.SimulationInputValidator

// ⚠️ IMPORT CERTO do viewModel (não importa o do Compose)
import org.koin.androidx.viewmodel.dsl.viewModel

import org.koin.dsl.module

// -------------------------------------------------------------------------
// Domain
// -------------------------------------------------------------------------
val domainModule = module {
    single { SacCalculator() }
    single { PriceCalculator() }

    // Recebe Sac + Price
    single { FinancingCalculator(get(), get()) }

    // Novo a cada uso
    factory { CalculateFinancingUseCase(get()) }
}

// -------------------------------------------------------------------------
// Validation
// -------------------------------------------------------------------------
val validationModule = module {
    factory { SimulationInputValidator() }
}

// -------------------------------------------------------------------------
// ViewModels
// -------------------------------------------------------------------------
val viewModelModule = module {
    viewModel {
        SimulationViewModel(
            calculateFinancingUseCase = get(),
            validator = get(),
        )
    }
}

// -------------------------------------------------------------------------
// App
// -------------------------------------------------------------------------
val appModule = module {
    includes(
        domainModule,
        validationModule,
        viewModelModule
    )
}