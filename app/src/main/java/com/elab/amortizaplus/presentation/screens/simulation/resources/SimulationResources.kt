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
    val startDateLabel = "Data de início (mês/ano)"
    val startDatePlaceholder = "Ex: 02/2026"
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
    val viewTableButton = "Ver Tabela Detalhada"
    val retryButton = "Tentar novamente"
    val tableTitle = "Tabela Detalhada"
    val tableSubtitle = "Detalhamento de parcelas e totais do cenário selecionado."
    val tableShowingWithExtra = "Mostrando parcelas com amortização extra"
    val tableShowingWithoutExtra = "Mostrando parcelas sem amortização extra"
    val tableToggleLabel = "Exibir extras"
    val tableSummaryTitle = "Resumo do Cenário"
    val tableColumnsTitle = "Colunas"
    val tableTotalsLabel = "Totais"
    val tableBackButton = "Voltar"
    val tableMonthHeader = "Mês"
    val tableColumnAmortization = "Amortização"
    val tableColumnInterest = "Juros"
    val tableColumnInstallment = "Parcela"
    val tableColumnExtra = "Extra"
    val tableColumnBalance = "Saldo"
    val tableSummaryTotalPaid = "Total Pago"
    val tableSummaryTotalInterest = "Total de Juros"
    val tableSummaryTotalAmortized = "Total Amortizado"
    val tableSummaryTerm = "Prazo"
    // Estados
    val initialTitle = "Pronto para simular?"
    val initialDescription = "Preencha os dados acima e toque em Calcular Simulação."
    val errorTitle = "Não foi possível calcular"
    val errorDescriptionPrefix = "Detalhes:"
    // Validação extras
    val extraMonthRequired = "Informe o mês"
    val extraMonthInvalid = "Mês inválido"
    val extraMonthTooLow = "Mês deve ser maior que zero"
    val extraMonthTooHighPrefix = "Mês deve ser ≤"
    val extraMonthDuplicate = "Mês já usado"
    val extraAmountRequired = "Informe o valor"
    val extraAmountInvalid = "Valor inválido"
    val extraAmountTooLow = "Valor deve ser maior que zero"
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
    val notAvailable = "-"
}
