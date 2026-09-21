package com.example.ui

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.calculator.CalculatorEngine
import com.example.data.AppDatabase
import com.example.data.AccountRepository
import com.example.data.TradingAccount
import com.example.model.AccountCurrency
import com.example.model.AppLanguage
import com.example.model.CalculationResult
import com.example.model.PositionType
import com.example.model.RiskMode
import com.example.model.ThemeMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

private data class Quadruple<A, B, C, D>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D
)

data class CalculatorUiState(
    val positionType: PositionType = PositionType.BUY,
    val entryPriceText: String = "",
    val stopLossPriceText: String = "",
    val takeProfitPriceText: String = "",
    val riskMode: RiskMode = RiskMode.PERCENTAGE,
    val riskValueText: String = "1.0",
    val activeAccount: TradingAccount? = null,
    val calculationResult: CalculationResult = CalculationResult(isValidInput = false),
    val infoMessage: String? = null,
    val developerName: String = "Ousman Seid Ebrahim",
    val themeMode: ThemeMode = ThemeMode.DARK,
    val currentLanguage: AppLanguage = AppLanguage.ENGLISH,
    val pipPrecision: Int = 3,
    val hapticEnabled: Boolean = true
)

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = application.getSharedPreferences("goldrisk_prefs", Context.MODE_PRIVATE)
    private val repository: AccountRepository
    private var hasUserCustomizedRisk = false

    val allAccounts: StateFlow<List<TradingAccount>>
    val activeAccount: StateFlow<TradingAccount?>

    // Settings States
    private val _themeMode = MutableStateFlow(
        ThemeMode.values().find { it.name == prefs.getString("theme_mode", ThemeMode.DARK.name) } ?: ThemeMode.DARK
    )
    val themeMode = _themeMode.asStateFlow()

    private val _currentLanguage = MutableStateFlow(
        AppLanguage.values().find { it.name == prefs.getString("app_language", AppLanguage.ENGLISH.name) } ?: AppLanguage.ENGLISH
    )
    val currentLanguage = _currentLanguage.asStateFlow()

    private val _pipPrecision = MutableStateFlow(prefs.getInt("pip_precision", 3))
    val pipPrecision = _pipPrecision.asStateFlow()

    private val _hapticEnabled = MutableStateFlow(prefs.getBoolean("haptic_enabled", true))
    val hapticEnabled = _hapticEnabled.asStateFlow()

    private val _positionType = MutableStateFlow(PositionType.BUY)
    val positionType = _positionType.asStateFlow()

    private val _entryPriceText = MutableStateFlow("")
    val entryPriceText = _entryPriceText.asStateFlow()

    private val _stopLossPriceText = MutableStateFlow("")
    val stopLossPriceText = _stopLossPriceText.asStateFlow()

    private val _takeProfitPriceText = MutableStateFlow("")
    val takeProfitPriceText = _takeProfitPriceText.asStateFlow()

    private val _riskMode = MutableStateFlow(RiskMode.PERCENTAGE)
    val riskMode = _riskMode.asStateFlow()

    private val _riskValueText = MutableStateFlow("1.0")
    val riskValueText = _riskValueText.asStateFlow()

    private val _infoMessage = MutableStateFlow<String?>(null)
    val infoMessage = _infoMessage.asStateFlow()

    // Developer credit (fixed, non-editable)
    private val _developerName = MutableStateFlow("Ousman Seid Ebrahim")
    val developerName = _developerName.asStateFlow()

    init {
        val database = AppDatabase.getInstance(application)
        repository = AccountRepository(database.accountDao())

        allAccounts = repository.allAccounts.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            emptyList()
        )

        activeAccount = repository.activeAccount.stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            null
        )

        viewModelScope.launch {
            repository.ensureDefaultAccount()
        }

        // When switching accounts, automatically load the correct specifications and defaults:
        viewModelScope.launch {
            repository.activeAccount.collect { account ->
                if (account != null) {
                    if (account.symbol.equals("XAUUSDc", ignoreCase = true) || account.name.contains("Cent", ignoreCase = true)) {
                        _riskMode.value = RiskMode.FIXED_AMOUNT
                        _riskValueText.value = "100" // Default Risk: 100 USC
                    } else if (account.symbol.equals("XAUUSDm", ignoreCase = true) || account.accountCurrency == AccountCurrency.USD) {
                        _riskMode.value = account.accountRiskMode
                        _riskValueText.value = if (account.accountRiskMode == RiskMode.PERCENTAGE) {
                            formatNumber(account.defaultRiskPercentage)
                        } else {
                            formatNumber(if (account.defaultRiskAmount > 0.0) account.defaultRiskAmount else 20.0)
                        }
                    } else {
                        _riskMode.value = account.accountRiskMode
                        _riskValueText.value = if (account.accountRiskMode == RiskMode.PERCENTAGE) {
                            formatNumber(account.defaultRiskPercentage)
                        } else {
                            formatNumber(account.defaultRiskAmount)
                        }
                    }
                }
            }
        }
    }

    val uiState: StateFlow<CalculatorUiState> = combine(
        combine(_positionType, _entryPriceText, _stopLossPriceText) { pos, entry, sl ->
            Triple(pos, entry, sl)
        },
        combine(_takeProfitPriceText, _riskMode, _riskValueText) { tp, rMode, rVal ->
            Triple(tp, rMode, rVal)
        },
        combine(activeAccount, _infoMessage, _developerName) { account, message, devName ->
            Triple(account, message, devName)
        },
        combine(_themeMode, _currentLanguage, _pipPrecision, _hapticEnabled) { theme, lang, prec, haptic ->
            Quadruple(theme, lang, prec, haptic)
        }
    ) { (pos, entry, sl), (tp, rMode, rVal), (account, message, devName), (theme, lang, prec, haptic) ->
        val entryNum = entry.toDoubleOrNull()
        val slNum = sl.toDoubleOrNull()
        val tpNum = tp.toDoubleOrNull()
        val rNum = rVal.toDoubleOrNull()

        val calcResult = if (account != null) {
            CalculatorEngine.calculate(
                positionType = pos,
                entryPrice = entryNum,
                stopLossPrice = slNum,
                takeProfitPrice = tpNum,
                riskMode = rMode,
                riskValue = rNum,
                account = account
            )
        } else {
            CalculationResult(isValidInput = false)
        }

        CalculatorUiState(
            positionType = pos,
            entryPriceText = entry,
            stopLossPriceText = sl,
            takeProfitPriceText = tp,
            riskMode = rMode,
            riskValueText = rVal,
            activeAccount = account,
            calculationResult = calcResult,
            infoMessage = message,
            developerName = devName,
            themeMode = theme,
            currentLanguage = lang,
            pipPrecision = prec,
            hapticEnabled = haptic
        )
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        CalculatorUiState()
    )

    fun setPositionType(type: PositionType) {
        _positionType.value = type
    }

    fun onEntryPriceChange(value: String) {
        _entryPriceText.value = cleanDecimalInput(value)
    }

    fun onStopLossChange(value: String) {
        _stopLossPriceText.value = cleanDecimalInput(value)
    }

    fun onTakeProfitChange(value: String) {
        _takeProfitPriceText.value = cleanDecimalInput(value)
    }

    fun onRiskModeChange(mode: RiskMode) {
        hasUserCustomizedRisk = true
        _riskMode.value = mode
        val acc = activeAccount.value
        if (mode == RiskMode.PERCENTAGE) {
            if (_riskValueText.value.toDoubleOrNull()?.let { it > 100.0 } == true || _riskValueText.value.isBlank()) {
                _riskValueText.value = "1.0"
            }
        } else {
            if (acc != null && (_riskValueText.value.toDoubleOrNull()?.let { it <= 5.0 } == true || _riskValueText.value.isBlank())) {
                _riskValueText.value = if (acc.accountCurrency == AccountCurrency.USC) "100" else "1.0"
            }
        }
    }

    fun onRiskValueChange(value: String) {
        hasUserCustomizedRisk = true
        _riskValueText.value = cleanDecimalInput(value)
    }

    fun setQuickRiskPercent(percent: Double) {
        _riskMode.value = RiskMode.PERCENTAGE
        _riskValueText.value = formatNumber(percent)
    }

    fun setQuickRiskAmount(amount: Double) {
        _riskMode.value = RiskMode.FIXED_AMOUNT
        _riskValueText.value = formatNumber(amount)
    }

    fun clearAllInputs() {
        clearTrade()
    }

    fun clearTrade() {
        _positionType.value = PositionType.BUY
        _entryPriceText.value = ""
        _stopLossPriceText.value = ""
        _takeProfitPriceText.value = ""
    }

    fun populateSampleTrade(buy: Boolean = true) {
        val acc = activeAccount.value
        _riskMode.value = RiskMode.FIXED_AMOUNT
        _riskValueText.value = if (acc?.accountCurrency == AccountCurrency.USC) "100" else "1.0"
        if (buy) {
            _positionType.value = PositionType.BUY
            _entryPriceText.value = "4144.888"
            _stopLossPriceText.value = "4130.341"
            _takeProfitPriceText.value = "4220.000"
        } else {
            _positionType.value = PositionType.SELL
            _entryPriceText.value = "4144.888"
            _stopLossPriceText.value = "4159.435"
            _takeProfitPriceText.value = "4069.776"
        }
    }

    fun loadExnessCentScenario() {
        viewModelScope.launch {
            // Find or create cent account
            val accounts = allAccounts.value
            val centAccount = accounts.find { it.symbol.equals("XAUUSDc", ignoreCase = true) || it.name.contains("Cent", ignoreCase = true) }
            if (centAccount != null) {
                val updated = centAccount.copy(
                    balance = 1400.0,
                    currency = AccountCurrency.USC.name,
                    defaultRiskAmount = 100.0,
                    contractSize = 1.0,
                    symbol = "XAUUSDc",
                    isActive = true
                )
                repository.updateAccount(updated)
                repository.setActiveAccount(updated.id)
            }
            _positionType.value = PositionType.BUY
            _entryPriceText.value = "4144.888"
            _stopLossPriceText.value = "4130.341"
            _takeProfitPriceText.value = "4220.000"
            _riskMode.value = RiskMode.FIXED_AMOUNT
            _riskValueText.value = "100"
            showTemporaryMessage("Loaded Exness Cent Setup: 100 USC Risk • 4144.888 / 4130.341 / 4220.000")
        }
    }

    // Settings actions
    fun setThemeMode(mode: ThemeMode) {
        _themeMode.value = mode
        prefs.edit().putString("theme_mode", mode.name).apply()
    }

    fun setCurrentLanguage(language: AppLanguage) {
        _currentLanguage.value = language
        prefs.edit().putString("app_language", language.name).apply()
    }

    fun setPipPrecision(precision: Int) {
        _pipPrecision.value = precision
        prefs.edit().putInt("pip_precision", precision).apply()
    }

    fun setHapticEnabled(enabled: Boolean) {
        _hapticEnabled.value = enabled
        prefs.edit().putBoolean("haptic_enabled", enabled).apply()
    }

    fun showTemporaryMessage(msg: String) {
        _infoMessage.value = msg
    }

    fun dismissMessage() {
        _infoMessage.value = null
    }

    // Account Management
    fun selectActiveAccount(accountId: Long) {
        viewModelScope.launch {
            repository.setActiveAccount(accountId)
        }
    }

    fun selectAccountProfile(symbol: String) {
        viewModelScope.launch {
            val accounts = allAccounts.value
            val target = accounts.find { it.symbol.equals(symbol, ignoreCase = true) }
                ?: if (symbol.contains("m", ignoreCase = true)) {
                    accounts.find { it.symbol.contains("m", ignoreCase = true) || (it.accountCurrency == AccountCurrency.USD && !it.name.contains("Cent", ignoreCase = true)) }
                } else {
                    accounts.find { it.symbol.contains("c", ignoreCase = true) || it.name.contains("Cent", ignoreCase = true) }
                }
            if (target != null) {
                repository.setActiveAccount(target.id)
            }
        }
    }

    fun saveAccount(
        id: Long,
        name: String,
        brokerName: String = "Exness",
        symbol: String = "XAUUSDc",
        accountType: String = "Exness Standard Cent Account",
        balance: Double,
        currency: AccountCurrency,
        defaultRiskAmount: Double,
        defaultRiskPercentage: Double = 1.0,
        riskMode: RiskMode,
        contractSize: Double,
        minLot: Double,
        lotStep: Double,
        maxLot: Double? = null
    ) {
        viewModelScope.launch {
            val account = TradingAccount(
                id = id,
                name = name.trim().ifEmpty { "Trading Account" },
                brokerName = brokerName.trim().ifEmpty { "Exness" },
                symbol = symbol.trim().ifEmpty { "XAUUSDc" },
                accountType = accountType.trim().ifEmpty { "Exness Standard Cent Account" },
                balance = balance,
                currency = currency.name,
                defaultRiskAmount = defaultRiskAmount,
                defaultRiskPercentage = defaultRiskPercentage,
                riskMode = riskMode.name,
                contractSize = if (contractSize > 0.0) contractSize else 1.0,
                minLot = if (minLot > 0.0) minLot else 0.01,
                lotStep = if (lotStep > 0.0) lotStep else 0.01,
                maxLot = maxLot,
                isActive = (id == 0L && allAccounts.value.isEmpty()) || (activeAccount.value?.id == id)
            )

            if (id == 0L) {
                val newId = repository.insertAccount(account)
                // If it's the only account, activate it
                if (allAccounts.value.size <= 1) {
                    repository.setActiveAccount(newId)
                }
            } else {
                repository.updateAccount(account)
            }
        }
    }

    fun deleteAccount(account: TradingAccount) {
        viewModelScope.launch {
            if (allAccounts.value.size > 1) {
                repository.deleteAccount(account)
            } else {
                showTemporaryMessage("Cannot delete the only remaining account")
            }
        }
    }

    private fun cleanDecimalInput(input: String): String {
        val filtered = input.filter { it.isDigit() || it == '.' }
        val parts = filtered.split('.')
        return if (parts.size > 2) {
            parts[0] + "." + parts.subList(1, parts.size).joinToString("")
        } else {
            filtered
        }
    }

    private fun formatNumber(value: Double): String {
        return if (value % 1.0 == 0.0) {
            value.toLong().toString()
        } else {
            value.toString()
        }
    }
}
