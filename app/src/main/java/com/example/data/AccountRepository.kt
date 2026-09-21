package com.example.data

import com.example.model.AccountCurrency
import com.example.model.RiskMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull

class AccountRepository(private val accountDao: AccountDao) {

    val allAccounts: Flow<List<TradingAccount>> = accountDao.getAllAccounts()
    val activeAccount: Flow<TradingAccount?> = accountDao.getActiveAccount()

    suspend fun ensureDefaultAccount() {
        val count = accountDao.getAccountCount()
        if (count == 0) {
            val centAcc = TradingAccount(
                name = "Exness Standard Cent",
                brokerName = "Exness",
                symbol = "XAUUSDc",
                accountType = "Exness Standard Cent",
                balance = 1400.0,
                currency = AccountCurrency.USC.name,
                defaultRiskAmount = 100.0,
                defaultRiskPercentage = 1.0,
                riskMode = RiskMode.FIXED_AMOUNT.name,
                contractSize = 1.0,
                minLot = 0.01,
                lotStep = 0.01,
                maxLot = 100.0,
                isActive = true
            )
            val standardAcc = TradingAccount(
                name = "Exness Standard",
                brokerName = "Exness",
                symbol = "XAUUSDm",
                accountType = "Exness Standard",
                balance = 1000.0,
                currency = AccountCurrency.USD.name,
                defaultRiskAmount = 20.0,
                defaultRiskPercentage = 1.0,
                riskMode = RiskMode.FIXED_AMOUNT.name,
                contractSize = 100.0,
                minLot = 0.01,
                lotStep = 0.01,
                maxLot = 200.0,
                isActive = false
            )
            val centId = accountDao.insertAccount(centAcc)
            accountDao.insertAccount(standardAcc)
            accountDao.setActiveAccount(centId)
        } else {
            val all = accountDao.getAllAccounts().firstOrNull() ?: emptyList()
            // Verify and sync Exness Standard Cent profile
            all.find { it.symbol == "XAUUSDc" || it.name.contains("Cent", ignoreCase = true) }?.let { cent ->
                if (cent.contractSize != 1.0 || cent.symbol != "XAUUSDc" || cent.currency != AccountCurrency.USC.name) {
                    accountDao.updateAccount(
                        cent.copy(
                            name = "Exness Standard Cent",
                            symbol = "XAUUSDc",
                            currency = AccountCurrency.USC.name,
                            contractSize = 1.0,
                            minLot = 0.01,
                            lotStep = 0.01
                        )
                    )
                }
            }

            // Verify and sync Exness Standard profile
            val standard = all.find {
                it.symbol == "XAUUSDm" || it.symbol == "XAUUSD" ||
                (it.name.contains("Standard", ignoreCase = true) && !it.name.contains("Cent", ignoreCase = true))
            }
            if (standard != null) {
                if (standard.contractSize != 100.0 || standard.symbol != "XAUUSDm" || standard.maxLot != 200.0 || standard.name != "Exness Standard") {
                    accountDao.updateAccount(
                        standard.copy(
                            name = "Exness Standard",
                            symbol = "XAUUSDm",
                            accountType = "Exness Standard",
                            currency = AccountCurrency.USD.name,
                            contractSize = 100.0,
                            minLot = 0.01,
                            lotStep = 0.01,
                            maxLot = 200.0
                        )
                    )
                }
            } else {
                accountDao.insertAccount(
                    TradingAccount(
                        name = "Exness Standard",
                        brokerName = "Exness",
                        symbol = "XAUUSDm",
                        accountType = "Exness Standard",
                        balance = 1000.0,
                        currency = AccountCurrency.USD.name,
                        defaultRiskAmount = 20.0,
                        defaultRiskPercentage = 1.0,
                        riskMode = RiskMode.FIXED_AMOUNT.name,
                        contractSize = 100.0,
                        minLot = 0.01,
                        lotStep = 0.01,
                        maxLot = 200.0,
                        isActive = false
                    )
                )
            }

            val active = accountDao.getActiveAccount().firstOrNull()
            if (active == null) {
                val firstAcc = accountDao.getAllAccounts().firstOrNull()?.firstOrNull()
                if (firstAcc != null) {
                    accountDao.setActiveAccount(firstAcc.id)
                }
            }
        }
    }

    suspend fun insertAccount(account: TradingAccount): Long {
        val id = accountDao.insertAccount(account)
        if (account.isActive) {
            accountDao.setActiveAccount(id)
        }
        return id
    }

    suspend fun updateAccount(account: TradingAccount) {
        accountDao.updateAccount(account)
        if (account.isActive) {
            accountDao.setActiveAccount(account.id)
        }
    }

    suspend fun deleteAccount(account: TradingAccount) {
        accountDao.deleteAccount(account)
        // If deleted account was active, set another account active
        val remaining = accountDao.getAllAccounts().firstOrNull()
        if (!remaining.isNullOrEmpty()) {
            val currentActive = accountDao.getActiveAccount().firstOrNull()
            if (currentActive == null) {
                accountDao.setActiveAccount(remaining.first().id)
            }
        }
    }

    suspend fun setActiveAccount(accountId: Long) {
        accountDao.setActiveAccount(accountId)
    }
}
