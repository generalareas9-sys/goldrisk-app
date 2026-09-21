package com.example

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.example.calculator.CalculatorEngine
import com.example.data.TradingAccount
import com.example.model.AccountCurrency
import com.example.model.PositionType
import com.example.model.RiskMode
import com.example.ui.RecommendedLotSizeCard
import com.example.ui.theme.XauUsdCalculatorTheme
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [36])
class GreetingScreenshotTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun greeting_screenshot() {
    val account = TradingAccount(
        name = "Standard USD",
        balance = 1000.0,
        currency = AccountCurrency.USD.name,
        defaultRiskAmount = 1.0,
        riskMode = RiskMode.PERCENTAGE.name,
        contractSize = 1.0,
        minLot = 0.01,
        lotStep = 0.01
    )
    val result = CalculatorEngine.calculate(
        positionType = PositionType.BUY,
        entryPrice = 2900.0,
        stopLossPrice = 2895.0,
        takeProfitPrice = 2915.0,
        riskMode = RiskMode.PERCENTAGE,
        riskValue = 1.0,
        account = account
    )
    composeTestRule.setContent {
      XauUsdCalculatorTheme {
        RecommendedLotSizeCard(
            calculationResult = result,
            activeAccount = account,
            onCopy = {}
        )
      }
    }

    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/greeting.png")
  }
}

