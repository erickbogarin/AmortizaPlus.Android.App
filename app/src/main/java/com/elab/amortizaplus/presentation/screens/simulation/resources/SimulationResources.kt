package com.elab.amortizaplus.presentation.screens.simulation.resources

/**
 * Mensagens específicas do fluxo de simulação.
 *
 * Centraliza textos para facilitar i18n e manutenção.
 */
object SimulationTexts {
    // Títulos e labels
    val screenTitle = "Simulação de Financiamento"
    val formSectionTitle = "📋 Dados da Simulação"
    val resultSectionTitle = "📊 Resumo da Simulação"
    val summaryWithoutTitle = "📈 Resumo - Sem Amortização Extra"
    val summaryWithTitle = "📉 Resumo - Com Amortização Extra"
    val savingsTitle = "🎯 Economia"
    val savingsInterestLabel = "Juros"
    val savingsTermLabel = "Prazo"
    // Campos do formulário
    val loanAmountLabel = "Valor do Empréstimo (R$)"
    val loanAmountPlaceholder = "Ex: 150000"
    val interestRateLabel = "Taxa de Juros (%)"
    val interestRatePlaceholder = "Ex: 13"
    val termsLabel = "Prazo (meses)"
    val termsPlaceholder = "Ex: 420"
    val startDateLabel = "Data de início"
    val startDatePlaceholder = "Ex: 2026-01-31"
    // Opções
    val rateTypeAnnual = "Anual"
    val rateTypeMonthly = "Mensal"
    val systemSac = "SAC"
    val systemPrice = "PRICE"
    // Amortizações extras
    val extraAmortizationsTitle = "💰 Amortizações Extras"
    val extraAmortizationsEmpty = "Nenhuma amortização extra adicionada"
    val extraAmortizationsAddButton = "+ Adicionar amortização"
    val extraAmortizationsRemoveButton = "Remover"
    val extraAmortizationMonthLabel = "Mês"
    val extraAmortizationMonthPlaceholder = "Ex: 8"
    val extraAmortizationAmountLabel = "Valor"
    val extraAmortizationAmountPlaceholder = "Ex: 76000"
    val extraAmortizationReduceTerm = "Reduzir prazo"
    val extraAmortizationReducePayment = "Reduzir parcela"
    // Ações
    val calculateButton = "Calcular Simulação"
    val newSimulationButton = "Nova Simulação"
    // Estados
    val loadingMessage = "Calculando simulação..."
    // Resultados
    val totalPaidLabel = "Total Pago"
    val totalInterestLabel = "Total de Juros"
    val monthsLabel = "Meses"
    val systemLabel = "Sistema"
    val valueLabel = "Valor"
    val rateLabel = "Taxa"
    val termLabel = "Prazo"
}
