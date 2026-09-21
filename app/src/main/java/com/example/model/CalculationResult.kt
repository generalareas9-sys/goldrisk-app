package com.example.model

data class CalculationResult(
    val isValidInput: Boolean,
    val validationWarning: String? = null,
    val stopLossDistance: Double = 0.0,
    val stopLossPips: Double = 0.0, // For XAUUSD, 1 pip is typically 0.10 or 10 cents
    val riskAmountInAccountCurrency: Double = 0.0,
    val riskAmountUSD: Double = 0.0,
    val riskAmountUSC: Double = 0.0,
    val exactLotSize: Double = 0.0,
    val executableLotSize: Double = 0.0,
    val actualRiskUSD: Double = 0.0,
    val actualRiskUSC: Double = 0.0,
    val actualRiskInAccountCurrency: Double = 0.0,
    val isBelowMinLot: Boolean = false,
    val minLot: Double = 0.01,
    val lotStep: Double = 0.01,
    val contractSize: Double = 1.0,
    val estimatedActualRiskAtMinLotUSD: Double = 0.0,
    val estimatedActualRiskAtMinLotUSC: Double = 0.0,
    val estimatedActualRiskAtMinLotInAccountCurrency: Double = 0.0,
    val belowMinNoticeMessage: String? = null,
    val minLotWarningMessage: String? = null,
    val minLotExceedsRisk: Boolean = false,
    val takeProfitDistance: Double? = null,
    val takeProfitPips: Double? = null,
    val riskRewardRatio: Double? = null,
    val potentialProfitInAccountCurrency: Double? = null,
    val potentialProfitUSD: Double? = null,
    val potentialProfitUSC: Double? = null,
    val positionValueUSD: Double = 0.0 // Executable Lot * Entry * Contract Size
)
