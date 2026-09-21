package com.example

import com.example.calculator.CalculatorEngine
import com.example.data.TradingAccount
import com.example.model.AccountCurrency
import com.example.model.PositionType
import com.example.model.RiskMode
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class CalculatorEngineTest {

    private val standardAccount = TradingAccount(
        name = "Test Standard USD",
        balance = 1000.0,
        currency = AccountCurrency.USD.name,
        defaultRiskAmount = 2.0,
        riskMode = RiskMode.PERCENTAGE.name,
        contractSize = 1.0,
        minLot = 0.01,
        lotStep = 0.01
    )

    private val centAccount = TradingAccount(
        name = "Test Cent USC",
        balance = 10000.0, // 10,000 USC = 100 USD
        currency = AccountCurrency.USC.name,
        defaultRiskAmount = 100.0, // 100 USC = 1 USD
        riskMode = RiskMode.FIXED_AMOUNT.name,
        contractSize = 1.0,
        minLot = 0.01,
        lotStep = 0.01
    )

    @Test
    fun `test exact and executable lot size calculation with rounding down`() {
        // Entry: 2900, SL: 2896 -> SL distance = 4.0
        // Risk: 2% of $1000 = $20.0
        // Contract size: 1.0
        // Lot size = 20 / (4 * 1) = 5.0 lots
        val result = CalculatorEngine.calculate(
            positionType = PositionType.BUY,
            entryPrice = 2900.0,
            stopLossPrice = 2896.0,
            takeProfitPrice = null,
            riskMode = RiskMode.PERCENTAGE,
            riskValue = 2.0,
            account = standardAccount
        )

        assertTrue(result.isValidInput)
        assertEquals(4.0, result.stopLossDistance, 0.001)
        assertEquals(20.0, result.riskAmountUSD, 0.001)
        assertEquals(2000.0, result.riskAmountUSC, 0.001)
        assertEquals(5.0, result.exactLotSize, 0.001)
        assertEquals(5.0, result.executableLotSize, 0.001)
        assertFalse(result.isBelowMinLot)
    }

    @Test
    fun `test rounding down to lot step ensures max risk is never exceeded`() {
        // Entry: 2900, SL: 2897 -> SL distance = 3.0
        // Risk: $10.0 fixed
        // Exact lot size = 10.0 / (3.0 * 1.0) = 3.333333... lots
        // With lotStep = 0.01, executable must round DOWN to 3.33 lots
        val result = CalculatorEngine.calculate(
            positionType = PositionType.BUY,
            entryPrice = 2900.0,
            stopLossPrice = 2897.0,
            takeProfitPrice = null,
            riskMode = RiskMode.FIXED_AMOUNT,
            riskValue = 10.0,
            account = standardAccount
        )

        assertTrue(result.isValidInput)
        assertEquals(3.0, result.stopLossDistance, 0.001)
        assertEquals(3.3333, result.exactLotSize, 0.001)
        assertEquals(3.33, result.executableLotSize, 0.0001)
        // 3.33 lots * 3.0 distance = $9.99 loss <= $10.00 risk!
        assertTrue(result.executableLotSize * result.stopLossDistance <= 10.0)
    }

    @Test
    fun `test cent account USC currency and automatic conversion`() {
        // 100 USC = 1 USD
        // Balance: 10,000 USC ($100 USD)
        // Risk: 200 USC ($2.00 USD)
        // Entry: 2900, SL: 2890 -> SL distance = 10.0
        // Lot size = $2.00 / (10 * 1) = 0.20 lots
        val result = CalculatorEngine.calculate(
            positionType = PositionType.BUY,
            entryPrice = 2900.0,
            stopLossPrice = 2890.0,
            takeProfitPrice = null,
            riskMode = RiskMode.FIXED_AMOUNT,
            riskValue = 200.0,
            account = centAccount
        )

        assertTrue(result.isValidInput)
        assertEquals(200.0, result.riskAmountUSC, 0.001)
        assertEquals(2.0, result.riskAmountUSD, 0.001)
        assertEquals(0.20, result.executableLotSize, 0.001)
    }

    @Test
    fun `test take profit and risk reward ratio`() {
        // Entry: 2900, SL: 2890 (dist: 10), TP: 2925 (dist: 25)
        // R:R should be 25 / 10 = 2.5
        val result = CalculatorEngine.calculate(
            positionType = PositionType.BUY,
            entryPrice = 2900.0,
            stopLossPrice = 2890.0,
            takeProfitPrice = 2925.0,
            riskMode = RiskMode.PERCENTAGE,
            riskValue = 1.0, // $10 risk
            account = standardAccount
        )

        assertNotNull(result.takeProfitDistance)
        assertEquals(25.0, result.takeProfitDistance!!, 0.001)
        assertNotNull(result.riskRewardRatio)
        assertEquals(2.5, result.riskRewardRatio!!, 0.01)

        // Potential profit: 1.0 lot * 25.0 * 1.0 = $25.00 USD
        assertNotNull(result.potentialProfitUSD)
        assertEquals(25.0, result.potentialProfitUSD!!, 0.01)
        assertEquals(2500.0, result.potentialProfitUSC!!, 0.01)
    }

    @Test
    fun `test trade validation warnings for BUY and SELL`() {
        // Inverted BUY: SL above Entry
        val buyInverted = CalculatorEngine.calculate(
            positionType = PositionType.BUY,
            entryPrice = 2900.0,
            stopLossPrice = 2910.0,
            takeProfitPrice = 2890.0,
            riskMode = RiskMode.PERCENTAGE,
            riskValue = 1.0,
            account = standardAccount
        )
        assertNotNull(buyInverted.validationWarning)
        assertTrue(buyInverted.validationWarning!!.contains("Stop Loss should be below the Entry Price"))

        // Inverted SELL: SL below Entry
        val sellInverted = CalculatorEngine.calculate(
            positionType = PositionType.SELL,
            entryPrice = 2900.0,
            stopLossPrice = 2890.0,
            takeProfitPrice = 2910.0,
            riskMode = RiskMode.PERCENTAGE,
            riskValue = 1.0,
            account = standardAccount
        )
        assertNotNull(sellInverted.validationWarning)
        assertTrue(sellInverted.validationWarning!!.contains("Stop Loss should be above the Entry Price"))

        // Correct SELL: SL above Entry, TP below Entry
        val sellValid = CalculatorEngine.calculate(
            positionType = PositionType.SELL,
            entryPrice = 2900.0,
            stopLossPrice = 2905.0,
            takeProfitPrice = 2890.0,
            riskMode = RiskMode.PERCENTAGE,
            riskValue = 1.0,
            account = standardAccount
        )
        assertNull(sellValid.validationWarning)
        assertEquals(5.0, sellValid.stopLossDistance, 0.001)
        assertEquals(10.0, sellValid.takeProfitDistance!!, 0.001)
        assertEquals(2.0, sellValid.riskRewardRatio!!, 0.01)
    }

    @Test
    fun `test below minimum lot check`() {
        // Account with min lot 0.01
        // Very small risk: $0.05 on a $10 SL distance -> 0.005 lots (< 0.01)
        val result = CalculatorEngine.calculate(
            positionType = PositionType.BUY,
            entryPrice = 2900.0,
            stopLossPrice = 2890.0,
            takeProfitPrice = null,
            riskMode = RiskMode.FIXED_AMOUNT,
            riskValue = 0.05,
            account = standardAccount
        )

        assertTrue(result.isValidInput)
        assertTrue(result.isBelowMinLot)
        assertEquals(0.0, result.executableLotSize, 0.001)
    }

    @Test
    fun `test user example trade with Exness Standard Cent Account`() {
        val exnessCent = TradingAccount(
            name = "Exness Standard Cent",
            balance = 1220.3,
            currency = AccountCurrency.USC.name,
            defaultRiskAmount = 100.0,
            riskMode = RiskMode.FIXED_AMOUNT.name,
            contractSize = 1.0,
            minLot = 0.01,
            lotStep = 0.01,
            symbol = "XAUUSDc",
            accountType = "Exness Standard Cent Account"
        )

        // Example trade from user:
        // Entry: 4424.287
        // Stop Loss: 4421.214
        // Stop Loss Distance: 3.073
        // Risk: 100 USC = $1 USD
        // Contract Size: 1
        // Exact calculation:
        // Lot Size = 1 / (3.073 × 1) = 0.3254 lots
        // Recommended Lot Size: 0.32 LOT
        // Exact Lot Size: 0.3254 lots
        val result = CalculatorEngine.calculate(
            positionType = PositionType.BUY,
            entryPrice = 4424.287,
            stopLossPrice = 4421.214,
            takeProfitPrice = null,
            riskMode = RiskMode.FIXED_AMOUNT,
            riskValue = 100.0,
            account = exnessCent
        )

        assertTrue(result.isValidInput)
        assertEquals(3.073, result.stopLossDistance, 0.001)
        assertEquals(100.0, result.riskAmountUSC, 0.001)
        assertEquals(1.00, result.riskAmountUSD, 0.001)

        // Exact lot size: 1 / (3.073 * 1) = 0.3254 lots
        assertEquals(0.3254, result.exactLotSize, 0.0001)
        // Recommended lot size rounded down to 0.01 step: 0.32 LOT
        assertEquals(0.32, result.executableLotSize, 0.001)
    }

    @Test
    fun `test 4014 trade with Exness Standard Cent Account`() {
        val exnessCent = TradingAccount(
            name = "Exness Standard Cent",
            balance = 1220.3,
            currency = AccountCurrency.USC.name,
            defaultRiskAmount = 100.0,
            riskMode = RiskMode.FIXED_AMOUNT.name,
            contractSize = 1.0,
            minLot = 0.01,
            lotStep = 0.01,
            symbol = "XAUUSDc",
            accountType = "Exness Standard Cent Account"
        )

        val result = CalculatorEngine.calculate(
            positionType = PositionType.BUY,
            entryPrice = 4014.73,
            stopLossPrice = 4002.69,
            takeProfitPrice = 4052.23,
            riskMode = RiskMode.FIXED_AMOUNT,
            riskValue = 100.0,
            account = exnessCent
        )

        assertTrue(result.isValidInput)
        assertEquals(12.04, result.stopLossDistance, 0.001)
        assertEquals(100.0, result.riskAmountUSC, 0.001)
        assertEquals(1.00, result.riskAmountUSD, 0.001)
        assertNotNull(result.riskRewardRatio)
        assertEquals(3.11, result.riskRewardRatio!!, 0.01)

        // Exact lot = 1.0 / (12.04 * 1) = 0.083056... -> Executable = 0.08
        assertEquals(0.0831, result.exactLotSize, 0.001)
        assertEquals(0.08, result.executableLotSize, 0.001)
    }

    @Test
    fun `test Exness Standard profile XAUUSDm with Contract Size 100`() {
        val exnessStandard = TradingAccount(
            name = "Exness Standard",
            balance = 1000.0,
            currency = AccountCurrency.USD.name,
            defaultRiskAmount = 20.0,
            riskMode = RiskMode.FIXED_AMOUNT.name,
            contractSize = 100.0,
            minLot = 0.01,
            maxLot = 200.0,
            lotStep = 0.01,
            symbol = "XAUUSDm",
            accountType = "Exness Standard Account"
        )

        // Entry: 2650.0, SL: 2645.0 -> SL Distance = 5.0
        // Risk: $20 USD
        // Contract Size: 100
        // Exact lot size = 20 / (5.0 * 100) = 20 / 500 = 0.04 lots
        val result = CalculatorEngine.calculate(
            positionType = PositionType.BUY,
            entryPrice = 2650.0,
            stopLossPrice = 2645.0,
            takeProfitPrice = 2660.0,
            riskMode = RiskMode.FIXED_AMOUNT,
            riskValue = 20.0,
            account = exnessStandard
        )

        assertTrue(result.isValidInput)
        assertEquals(100.0, result.contractSize, 0.001)
        assertEquals(5.0, result.stopLossDistance, 0.001)
        assertEquals(20.0, result.riskAmountUSD, 0.001)
        assertEquals(0.04, result.exactLotSize, 0.0001)
        assertEquals(0.04, result.executableLotSize, 0.0001)
        assertFalse(result.isBelowMinLot)
        assertFalse(result.minLotExceedsRisk)
        // Actual risk = 0.04 * 5.0 * 100 = $20.0
        assertEquals(20.0, result.actualRiskUSD, 0.001)
    }

    @Test
    fun `test XAUUSDm safe down-rounding according to volume step 0_01`() {
        val exnessStandard = TradingAccount(
            name = "Exness Standard",
            balance = 1000.0,
            currency = AccountCurrency.USD.name,
            defaultRiskAmount = 25.0,
            riskMode = RiskMode.FIXED_AMOUNT.name,
            contractSize = 100.0,
            minLot = 0.01,
            maxLot = 200.0,
            lotStep = 0.01,
            symbol = "XAUUSDm",
            accountType = "Exness Standard Account"
        )

        // Entry: 2650.0, SL: 2647.0 -> SL Distance = 3.0
        // Risk: $25 USD
        // Exact lot = 25 / (3.0 * 100) = 25 / 300 = 0.083333... lots
        // Valid volume step = 0.01 -> Round down to 0.08 lots
        val result = CalculatorEngine.calculate(
            positionType = PositionType.BUY,
            entryPrice = 2650.0,
            stopLossPrice = 2647.0,
            takeProfitPrice = null,
            riskMode = RiskMode.FIXED_AMOUNT,
            riskValue = 25.0,
            account = exnessStandard
        )

        assertEquals(0.0833, result.exactLotSize, 0.001)
        assertEquals(0.08, result.executableLotSize, 0.001)
        // Actual risk: 0.08 * 3.0 * 100 = $24.00, which is strictly <= intended $25.00
        assertEquals(24.0, result.actualRiskUSD, 0.001)
        assertTrue(result.actualRiskUSD <= result.riskAmountUSD)
    }

    @Test
    fun `test XAUUSDm minimum lot validation and warning when min lot exceeds risk`() {
        val exnessStandard = TradingAccount(
            name = "Exness Standard",
            balance = 500.0,
            currency = AccountCurrency.USD.name,
            defaultRiskAmount = 2.0,
            riskMode = RiskMode.FIXED_AMOUNT.name,
            contractSize = 100.0,
            minLot = 0.01,
            maxLot = 200.0,
            lotStep = 0.01,
            symbol = "XAUUSDm",
            accountType = "Exness Standard Account"
        )

        // Risk: $2 USD
        // Entry: 2650.0, SL: 2645.0 -> SL distance = 5.0
        // Exact lot = 2 / (5 * 100) = 2 / 500 = 0.004 lots
        // 0.004 < broker minimum 0.01!
        val result = CalculatorEngine.calculate(
            positionType = PositionType.BUY,
            entryPrice = 2650.0,
            stopLossPrice = 2645.0,
            takeProfitPrice = null,
            riskMode = RiskMode.FIXED_AMOUNT,
            riskValue = 2.0,
            account = exnessStandard
        )

        assertTrue(result.isBelowMinLot)
        assertEquals(0.004, result.exactLotSize, 0.0001)
        assertEquals(0.01, result.minLot, 0.0001)
        assertEquals(0.0, result.executableLotSize, 0.0001)

        // Actual risk at 0.01 lot: 0.01 * 5.0 * 100 = $5.00 USD
        assertEquals(5.00, result.estimatedActualRiskAtMinLotUSD, 0.001)

        // Intended risk was $2.00, so $5.00 exceeds intended risk
        assertTrue(result.minLotExceedsRisk)
        assertEquals("Calculated lot size is below the broker minimum volume of 0.01.", result.belowMinNoticeMessage)
        assertEquals("Warning: The minimum lot size exceeds your selected risk.", result.minLotWarningMessage)
    }

    @Test
    fun `test contrast between Cent XAUUSDc and Standard XAUUSDm formulas`() {
        val centAccount = TradingAccount(
            name = "Exness Standard Cent",
            balance = 10000.0,
            currency = AccountCurrency.USC.name,
            contractSize = 1.0,
            symbol = "XAUUSDc"
        )
        val standardAccount = TradingAccount(
            name = "Exness Standard",
            balance = 100.0,
            currency = AccountCurrency.USD.name,
            contractSize = 100.0,
            symbol = "XAUUSDm"
        )

        // Both risking equivalent of $1.00 USD (100 USC vs $1.00 USD)
        // Price distance = 2.0
        val centResult = CalculatorEngine.calculate(
            positionType = PositionType.BUY,
            entryPrice = 2700.0,
            stopLossPrice = 2698.0,
            takeProfitPrice = null,
            riskMode = RiskMode.FIXED_AMOUNT,
            riskValue = 100.0, // 100 USC = $1.00 USD
            account = centAccount
        )
        val standardResult = CalculatorEngine.calculate(
            positionType = PositionType.BUY,
            entryPrice = 2700.0,
            stopLossPrice = 2698.0,
            takeProfitPrice = null,
            riskMode = RiskMode.FIXED_AMOUNT,
            riskValue = 1.0, // $1.00 USD
            account = standardAccount
        )

        // For Cent: Lot Size = 1.0 / (2.0 * 1) = 0.50 lots
        assertEquals(0.50, centResult.executableLotSize, 0.001)

        // For Standard: Lot Size = 1.0 / (2.0 * 100) = 1.0 / 200 = 0.005 lots (< 0.01 min lot)
        assertEquals(0.005, standardResult.exactLotSize, 0.0001)
        assertTrue(standardResult.isBelowMinLot)
    }
}
