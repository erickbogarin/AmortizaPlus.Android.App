package com.elab.amortizaplus.di

import com.elab.amortizaplus.domain.calculator.FinancingCalculator
import com.elab.amortizaplus.domain.calculator.PriceCalculator
import com.elab.amortizaplus.domain.calculator.SacCalculator
import com.elab.amortizaplus.domain.usecase.CalculateFinancingUseCase
import org.koin.dsl.module

val domainModule = module {
    single { SacCalculator() }
    single { PriceCalculator() }

    single { FinancingCalculator(get(), get()) }

    factory { CalculateFinancingUseCase(get()) }
}

val presentationModule = module {
    //view models
}

val appModule = module {
    includes(domainModule, presentationModule)
}