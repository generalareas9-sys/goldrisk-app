package com.example.calculator

import com.example.data.TradingAccount
import com.example.model.AccountCurrency
import com.example.model.CalculationResult
import com.example.model.PositionType
import com.example.model.RiskMode
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.max

object CalculatorEngine {

    fun calculate(
        positionType: PositionType,
        entryPrice: Double?,
        stopLossPrice: Double?,
        takeProfitPrice: Double?,
        riskMode: RiskMode,
        riskValue: Double?,
        account: TradingAccount
    ): CalculationResult {
        // Basic input presence checks
        if (entryPrice == null || entryPrice <= 0.0 ||
            stopLossPrice == null || stopLossPrice <= 0.0 ||
            riskValue == null || riskValue <= 0.0
        ) {
            return CalculationResult(
                isValidInput = false,
                validationWarning = null,
                minLot = account.minLot,
                lotStep = account.lotStep,
                contractSize = if (account.contractSize > 0.0) account.contractSize else 1.0
            )
        }

        val slDistance = abs(entryPrice - stopLossPrice)
        if (slDistance <= 0.000001) {
            return CalculationResult(
                isValidInput = false,
                validationWarning = "Entry Price and Stop Loss cannot be identical",
                minLot = account.minLot,
                lotStep = account.lotStep,
                contractSize = if (account.contractSize > 0.0) account.contractSize else 1.0
            )
        }

        // Validate trade direction strictly before calculating
        when (positionType) {
            PositionType.BUY -> {
                if (stopLossPrice >= entryPrice) {
                    return CalculationResult(
                        isValidInput = false,
                        validationWarning = "Invalid BUY setup. Stop Loss should be below the Entry Price.",
                        minLot = account.minLot,
                        lotStep = account.lotStep,
                        contractSize = if (account.contractSize > 0.0) account.contractSize else 1.0
                    )
                } else if (takeProfitPrice != null && takeProfitPrice > 0.0 && takeProfitPrice <= entryPrice) {
                    return CalculationResult(
                        isValidInput = false,
                        validationWarning = "Invalid BUY setup. Take Profit should be above the Entry Price.",
                        minLot = account.minLot,
                        lotStep = account.lotStep,
                        contractSize = if (account.contractSize > 0.0) account.contractSize else 1.0
                    )
                }
            }
            PositionType.SELL -> {
                if (stopLossPrice <= entryPrice) {
                    return CalculationResult(
                        isValidInput = false,
                        validationWarning = "Invalid SELL setup. Stop Loss should be above the Entry Price.",
                        minLot = account.minLot,
                        lotStep = account.lotStep,
                        contractSize = if (account.contractSize > 0.0) account.contractSize else 1.0
                    )
                } else if (takeProfitPrice != null && takeProfitPrice > 0.0 && takeProfitPrice >= entryPrice) {
                    return CalculationResult(
                        isValidInput = false,
                        validationWarning = "Invalid SELL setup. Take Profit should be below the Entry Price.",
                        minLot = account.minLot,
                        lotStep = account.lotStep,
                        contractSize = if (account.contractSize > 0.0) account.contractSize else 1.0
                    )
                }
            }
        }

        // Calculate Risk Amount in account currency
        val riskAmountInAccountCurrency = when (riskMode) {
            RiskMode.PERCENTAGE -> {
                account.balance * (riskValue / 100.0)
            }
            RiskMode.FIXED_AMOUNT -> {
                riskValue
            }
        }

        // Convert Risk Amount between USD and USC
        // 100 USC = 1 USD
        val isUSC = account.accountCurrency == AccountCurrency.USC
        val riskAmountUSD = if (isUSC) riskAmountInAccountCurrency / 100.0 else riskAmountInAccountCurrency
        val riskAmountUSC = if (isUSC) riskAmountInAccountCurrency else riskAmountInAccountCurrency * 100.0

        val contractSize = if (account.contractSize > 0.0) account.contractSize else 1.0
        val lotStep = if (account.lotStep > 0.0) account.lotStep else 0.01
        val minLot = if (account.minLot > 0.0) account.minLot else 0.01

        // Formula: Lot Size = Risk Amount (USD) / (Stop Loss Distance × Contract Size)
        // For XAUUSDm: Lot Size = Risk Amount in USD / (SL Distance × 100)
        // For XAUUSDc: Lot Size = Risk Amount in USD / (SL Distance × 1)
        val exactLotSize = riskAmountUSD / (slDistance * contractSize)

        // Round DOWN to broker's valid lot step so risk is never exceeded
        val stepDecimals = getDecimalPlaces(lotStep)
        var steps = floor((exactLotSize / lotStep) + 1e-9)
        var roundedExecutable = roundToDecimals(steps * lotStep, stepDecimals)

        // Ensure rounding never exceeds risk
        if (roundedExecutable * slDistance * contractSize > riskAmountUSD + 1e-6 && steps > 0) {
            steps -= 1.0
            roundedExecutable = roundToDecimals(steps * lotStep, stepDecimals)
        }

        // Check if max lot exists
        if (account.maxLot != null && roundedExecutable > account.maxLot) {
            roundedExecutable = account.maxLot
        }

        val isBelowMin = exactLotSize < minLot - 1e-9
        // Do not silently recommend a lot size that exceeds the user's selected maximum risk.
        // Do not incorrectly claim that the recommended 0.01 lot matches the selected risk.
        val executableLotSize = if (isBelowMin) 0.0 else roundedExecutable

        // Actual Risk After Rounding: calculated using the recommended rounded lot size
        val actualRiskUSD = executableLotSize * slDistance * contractSize
        val actualRiskUSC = actualRiskUSD * 100.0
        val actualRiskInAccountCurrency = if (isUSC) actualRiskUSC else actualRiskUSD

        // Minimum lot validation messages:
        val belowMinNoticeMessage = if (isBelowMin) {
            "Calculated lot size is below the broker minimum volume of ${formatLot(minLot, lotStep)}."
        } else null

        val estimatedRiskAtMinLotUSD = minLot * slDistance * contractSize
        val estimatedRiskAtMinLotUSC = estimatedRiskAtMinLotUSD * 100.0
        val estimatedRiskAtMinLotAccount = if (isUSC) estimatedRiskAtMinLotUSC else estimatedRiskAtMinLotUSD

        val minLotExceedsRisk = isBelowMin && (estimatedRiskAtMinLotUSD > riskAmountUSD + 1e-6)
        val minLotWarningMessage = if (minLotExceedsRisk) {
            "Warning: The minimum lot size exceeds your selected risk."
        } else null

        // Stop Loss distance in pips (for Gold, 1 pip = 0.10 price change)
        val slPips = slDistance / 0.10

        // Take Profit Calculations (Optional)
        var tpDistance: Double? = null
        var tpPips: Double? = null
        var rrRatio: Double? = null
        var potentialProfitAccount: Double? = null
        var potentialProfitUSD: Double? = null
        var potentialProfitUSC: Double? = null

        if (takeProfitPrice != null && takeProfitPrice > 0.0) {
            val dist = abs(takeProfitPrice - entryPrice)
            tpDistance = dist
            tpPips = dist / 0.10
            val ratio = if (slDistance > 0.0) dist / slDistance else null
            rrRatio = ratio

            // Potential Profit based on executable lots (or exact lots if below min)
            val effectiveLots = if (executableLotSize > 0.0) executableLotSize else exactLotSize
            val profitUSD = effectiveLots * dist * contractSize
            val profitUSC = profitUSD * 100.0
            val profitAcc = if (isUSC) profitUSC else profitUSD
            potentialProfitAccount = profitAcc
            potentialProfitUSD = profitUSD
            potentialProfitUSC = profitUSC
        }

        val effectiveLots = if (executableLotSize > 0.0) executableLotSize else exactLotSize
        val positionValueUSD = effectiveLots * entryPrice * contractSize

        return CalculationResult(
            isValidInput = true,
            validationWarning = null,
            stopLossDistance = slDistance,
            stopLossPips = slPips,
            riskAmountInAccountCurrency = riskAmountInAccountCurrency,
            riskAmountUSD = riskAmountUSD,
            riskAmountUSC = riskAmountUSC,
            exactLotSize = exactLotSize,
            executableLotSize = executableLotSize,
            actualRiskUSD = actualRiskUSD,
            actualRiskUSC = actualRiskUSC,
            actualRiskInAccountCurrency = actualRiskInAccountCurrency,
            isBelowMinLot = isBelowMin,
            minLot = minLot,
            lotStep = lotStep,
            contractSize = contractSize,
            estimatedActualRiskAtMinLotUSD = estimatedRiskAtMinLotUSD,
            estimatedActualRiskAtMinLotUSC = estimatedRiskAtMinLotUSC,
            estimatedActualRiskAtMinLotInAccountCurrency = estimatedRiskAtMinLotAccount,
            belowMinNoticeMessage = belowMinNoticeMessage,
            minLotWarningMessage = minLotWarningMessage,
            minLotExceedsRisk = minLotExceedsRisk,
            takeProfitDistance = tpDistance,
            takeProfitPips = tpPips,
            riskRewardRatio = rrRatio,
            potentialProfitInAccountCurrency = potentialProfitAccount,
            potentialProfitUSD = potentialProfitUSD,
            potentialProfitUSC = potentialProfitUSC,
            positionValueUSD = positionValueUSD
        )
    }

    fun formatLot(lot: Double, lotStep: Double): String {
        val decimals = max(2, getDecimalPlaces(lotStep))
        return String.format(java.util.Locale.US, "%.${decimals}f", lot)
    }

    fun getDecimalPlaces(value: Double): Int {
        val str = BigDecimal.valueOf(value).stripTrailingZeros().toPlainString()
        val index = str.indexOf('.')
        return if (index < 0) 0 else str.length - index - 1
    }

    private fun roundToDecimals(value: Double, decimals: Int): Double {
        return try {
            BigDecimal.valueOf(value).setScale(decimals, RoundingMode.HALF_UP).toDouble()
        } catch (_: Exception) {
            value
        }
    }
}
