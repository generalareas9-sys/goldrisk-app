package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.model.AccountCurrency
import com.example.model.RiskMode

@Entity(tableName = "trading_accounts")
data class TradingAccount(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val brokerName: String = "Exness",
    val symbol: String = "XAUUSDc",
    val accountType: String = "Exness Standard Cent Account",
    val balance: Double,
    val currency: String = AccountCurrency.USC.name, // USC or USD
    val defaultRiskAmount: Double = 100.0, // e.g. 100 USC ($1.00)
    val defaultRiskPercentage: Double = 1.0, // e.g. 1.0%
    val riskMode: String = RiskMode.FIXED_AMOUNT.name,
    val contractSize: Double = 1.0, // Default 1.0 per user specification (NOT 100)
    val minLot: Double = 0.01,
    val lotStep: Double = 0.01,
    val maxLot: Double? = null,
    val isActive: Boolean = false
) {
    val accountCurrency: AccountCurrency
        get() = try {
            AccountCurrency.valueOf(currency)
        } catch (_: Exception) {
            AccountCurrency.USD
        }

    val accountRiskMode: RiskMode
        get() = try {
            RiskMode.valueOf(riskMode)
        } catch (_: Exception) {
            RiskMode.FIXED_AMOUNT
        }

    /**
     * Balance converted to USD.
     * 100 USC = 1 USD.
     */
    val balanceInUSD: Double
        get() = if (accountCurrency == AccountCurrency.USC) balance / 100.0 else balance

    /**
     * Balance converted to USC.
     * 1 USD = 100 USC.
     */
    val balanceInUSC: Double
        get() = if (accountCurrency == AccountCurrency.USD) balance * 100.0 else balance
}
