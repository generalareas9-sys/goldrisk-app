package com.example.ui

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Warning
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import com.example.model.AppLanguage
import com.example.ui.SettingsSheet
import com.example.ui.localization.AppLocalization
import com.example.ui.theme.AppTheme
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.calculator.CalculatorEngine
import com.example.data.TradingAccount
import com.example.model.AccountCurrency
import com.example.model.CalculationResult
import com.example.model.PositionType
import com.example.model.RiskMode
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.BuyGreen
import com.example.ui.theme.BuyGreenContainer
import com.example.ui.theme.CardBackgroundDark
import com.example.ui.theme.CardBorderDark
import com.example.ui.theme.CardBorderGold
import com.example.ui.theme.GoldContainer
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.GoldRiskSilver
import com.example.ui.theme.OnGoldContainer
import com.example.ui.theme.SellRed
import com.example.ui.theme.SellRedContainer
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary
import com.example.ui.theme.WarningAmber
import com.example.ui.theme.WarningAmberContainer
import kotlinx.coroutines.launch
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CalculatorScreen(viewModel: MainViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val allAccounts by viewModel.allAccounts.collectAsState()
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val haptic = LocalHapticFeedback.current
    val colors = AppTheme.colors
    val strings = AppLocalization.get(uiState.currentLanguage)

    var showAccountSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var showInfoSheet by remember { mutableStateOf(false) }
    var infoSheetTab by remember { mutableStateOf(InfoTab.CALCULATIONS) }
    val infoSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var showSettingsSheet by remember { mutableStateOf(false) }
    val settingsSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // Handle temporary messages
    LaunchedEffect(uiState.infoMessage) {
        uiState.infoMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.dismissMessage()
        }
    }

    Scaffold(
        containerColor = colors.background,
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .clickable {
                                if (uiState.hapticEnabled) haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                showSettingsSheet = true
                            }
                            .padding(vertical = 4.dp, horizontal = 2.dp)
                            .testTag("goldrisk_logo_settings_trigger")
                    ) {
                        // Official GoldRisk Logo Emblem with Settings Badge inside
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .border(1.5.dp, colors.goldPrimary, RoundedCornerShape(10.dp))
                                .background(colors.goldContainer)
                                .testTag("goldrisk_logo_settings")
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.goldrisk_app_icon_1788801410920),
                                contentDescription = "GoldRisk Logo - Tap for Settings",
                                modifier = Modifier.fillMaxSize()
                            )
                            // Gear badge inside the logo
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomEnd)
                                    .size(15.dp)
                                    .clip(CircleShape)
                                    .background(colors.goldPrimary),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Settings,
                                    contentDescription = "Settings",
                                    tint = Color.Black,
                                    modifier = Modifier.size(10.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "Gold",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 20.sp,
                                    color = colors.goldPrimary,
                                    letterSpacing = 0.5.sp
                                )
                                Text(
                                    text = "Risk",
                                    fontWeight = FontWeight.Black,
                                    fontSize = 20.sp,
                                    color = if (colors.isDark) GoldRiskSilver else colors.textPrimary,
                                    letterSpacing = 0.5.sp
                                )
                            }
                            Text(
                                text = strings.appSubtitle,
                                fontSize = 10.5.sp,
                                fontWeight = FontWeight.Medium,
                                color = colors.textSecondary
                            )
                        }
                    }
                },
                actions = {
                    // Information & Disclaimer Button
                    IconButton(
                        onClick = {
                            infoSheetTab = InfoTab.DISCLAIMER
                            showInfoSheet = true
                        },
                        modifier = Modifier.testTag("open_info_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Information & Disclaimer",
                            tint = colors.goldLight
                        )
                    }

                    // Quick Sample Pre-fill Button
                    IconButton(
                        onClick = {
                            focusManager.clearFocus()
                            if (uiState.hapticEnabled) haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            viewModel.populateSampleTrade(uiState.positionType == PositionType.BUY)
                            viewModel.showTemporaryMessage("Loaded 4144.888 / 4130.341 / 4220 trade")
                        },
                        modifier = Modifier.testTag("sample_trade_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Sample Trade",
                            tint = colors.textSecondary
                        )
                    }

                    // Manage Accounts Button
                    IconButton(
                        onClick = { showAccountSheet = true },
                        modifier = Modifier.testTag("open_accounts_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = strings.manageAccounts,
                            tint = colors.textPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = colors.surface,
                    titleContentColor = colors.textPrimary
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // =========================================================================
            // ACTIVE ACCOUNT & SYMBOL SPECIFICATIONS BANNER
            // Displays Account Name, Symbol, Currency, Contract Size, Min Lot, Lot Step
            // with instant profile switching between Cent and Standard
            // =========================================================================
            uiState.activeAccount?.let { account ->
                ActiveAccountProfileHeader(
                    activeAccount = account,
                    onSelectProfile = { symbol -> viewModel.selectAccountProfile(symbol) },
                    onOpenManage = { showAccountSheet = true }
                )
            }

            // =========================================================================
            // REQUIRED RESULT: RECOMMENDED LOT SIZE (The largest & most visually prominent)
            // =========================================================================
            RecommendedLotSizeCard(
                calculationResult = uiState.calculationResult,
                activeAccount = uiState.activeAccount,
                pipPrecision = uiState.pipPrecision,
                language = uiState.currentLanguage,
                onCopy = { lotText ->
                    if (uiState.hapticEnabled) haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                    clipboard.setPrimaryClip(ClipData.newPlainText("XAUUSD Lot Size", lotText))
                    viewModel.showTemporaryMessage("Lot size copied")
                }
            )

            // Trade Validation Warning Banner (if inverted SL/TP)
            AnimatedVisibility(visible = uiState.calculationResult.validationWarning != null) {
                uiState.calculationResult.validationWarning?.let { warning ->
                    TradeValidationWarning(warning = warning)
                }
            }

            // =========================================================================
            // REQUIRED CALCULATOR INPUTS (Simple vertical form from top to bottom)
            // 1. Active Trading Account
            // 2. Account Balance
            // 3. Risk Mode
            // 4. Risk Amount
            // 5. Position Type (BUY / SELL)
            // 6. Entry Price
            // 7. Stop Loss Price
            // 8. Take Profit Price (Optional)
            // =========================================================================

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CardBackgroundDark),
                border = androidx.compose.foundation.BorderStroke(1.dp, CardBorderDark)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "Trade Parameters",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = GoldPrimary
                    )

                    // -------------------------------------------------------------
                    // 1. Active Trading Account
                    // -------------------------------------------------------------
                    uiState.activeAccount?.let { account ->
                        ActiveAccountInputSection(
                            account = account,
                            onClick = { showAccountSheet = true }
                        )
                    }

                    // -------------------------------------------------------------
                    // 2. Account Balance (Clearly displaying both USC & USD)
                    // -------------------------------------------------------------
                    uiState.activeAccount?.let { account ->
                        AccountBalanceInputSection(
                            account = account,
                            onClick = { showAccountSheet = true }
                        )
                    }

                    HorizontalDivider(color = CardBorderDark.copy(alpha = 0.6f), thickness = 1.dp)

                    // -------------------------------------------------------------
                    // 3. Risk Mode (Percentage % vs Fixed Money USC / USD)
                    // -------------------------------------------------------------
                    RiskModeInputSection(
                        riskMode = uiState.riskMode,
                        activeAccount = uiState.activeAccount,
                        onRiskModeChange = viewModel::onRiskModeChange
                    )

                    // -------------------------------------------------------------
                    // 4. Risk Amount (displaying both USC and USD preview)
                    // -------------------------------------------------------------
                    RiskAmountInputSection(
                        riskMode = uiState.riskMode,
                        riskValue = uiState.riskValueText,
                        activeAccount = uiState.activeAccount,
                        onRiskValueChange = viewModel::onRiskValueChange,
                        onQuickPercent = viewModel::setQuickRiskPercent
                    )

                    HorizontalDivider(color = CardBorderDark.copy(alpha = 0.6f), thickness = 1.dp)

                    // -------------------------------------------------------------
                    // 5. Position Type (BUY / SELL)
                    // -------------------------------------------------------------
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "Position Type",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = TextSecondary
                        )
                        PositionTypeSelector(
                            selected = uiState.positionType,
                            onSelect = viewModel::setPositionType
                        )
                    }

                    // -------------------------------------------------------------
                    // 6. Entry Price
                    // -------------------------------------------------------------
                    OutlinedTextField(
                        value = uiState.entryPriceText,
                        onValueChange = viewModel::onEntryPriceChange,
                        label = { Text("Entry Price (XAUUSD)") },
                        placeholder = { Text("e.g. 4014.73") },
                        trailingIcon = {
                            if (uiState.entryPriceText.isNotEmpty()) {
                                IconButton(onClick = { viewModel.onEntryPriceChange("") }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear Entry", tint = TextTertiary)
                                }
                            }
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Decimal,
                            imeAction = ImeAction.Next
                        ),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_entry_price"),
                        colors = inputFieldColors()
                    )

                    // -------------------------------------------------------------
                    // 7. Stop Loss Price
                    // -------------------------------------------------------------
                    val slGuidance = if (uiState.positionType == PositionType.BUY) "(Below Entry)" else "(Above Entry)"
                    OutlinedTextField(
                        value = uiState.stopLossPriceText,
                        onValueChange = viewModel::onStopLossChange,
                        label = { Text("Stop Loss Price $slGuidance") },
                        placeholder = { Text(if (uiState.positionType == PositionType.BUY) "e.g. 4002.69" else "e.g. 4026.50") },
                        trailingIcon = {
                            if (uiState.stopLossPriceText.isNotEmpty()) {
                                IconButton(onClick = { viewModel.onStopLossChange("") }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear Stop Loss", tint = TextTertiary)
                                }
                            }
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Decimal,
                            imeAction = ImeAction.Next
                        ),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_stop_loss_price"),
                        colors = inputFieldColors()
                    )

                    // -------------------------------------------------------------
                    // 8. Take Profit Price (Optional)
                    // -------------------------------------------------------------
                    val tpGuidance = if (uiState.positionType == PositionType.BUY) "(Above Entry)" else "(Below Entry)"
                    OutlinedTextField(
                        value = uiState.takeProfitPriceText,
                        onValueChange = viewModel::onTakeProfitChange,
                        label = { Text("Take Profit Price (Optional) $tpGuidance") },
                        placeholder = { Text(if (uiState.positionType == PositionType.BUY) "e.g. 4052.23 (Optional)" else "e.g. 3980.00 (Optional)") },
                        trailingIcon = {
                            if (uiState.takeProfitPriceText.isNotEmpty()) {
                                IconButton(onClick = { viewModel.onTakeProfitChange("") }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear Take Profit", tint = TextTertiary)
                                }
                            }
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Decimal,
                            imeAction = ImeAction.Done
                        ),
                        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("input_take_profit_price"),
                        colors = inputFieldColors()
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // -------------------------------------------------------------
                    // Clear Button (Must display horizontally from left to right)
                    // -------------------------------------------------------------
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                focusManager.clearFocus()
                                viewModel.clearAllInputs()
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("clear_button"),
                            shape = RoundedCornerShape(10.dp),
                            border = androidx.compose.foundation.BorderStroke(1.dp, CardBorderDark),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = TextSecondary
                            )
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Clear",
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.labelLarge
                                )
                            }
                        }

                        // Quick Test Example Button
                        Button(
                            onClick = {
                                focusManager.clearFocus()
                                viewModel.populateSampleTrade(uiState.positionType == PositionType.BUY)
                            },
                            modifier = Modifier
                                .weight(1f)
                                .height(48.dp)
                                .testTag("sample_trade_example_button"),
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = SurfaceDark,
                                contentColor = GoldLight
                            ),
                            border = androidx.compose.foundation.BorderStroke(1.dp, CardBorderGold)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Refresh,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = GoldLight
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Example Trade",
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.labelMedium
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    if (showAccountSheet) {
        AccountManagerSheet(
            accounts = allAccounts,
            activeAccount = uiState.activeAccount,
            sheetState = sheetState,
            onDismiss = { showAccountSheet = false },
            onSelectActiveAccount = {
                viewModel.selectActiveAccount(it)
                coroutineScope.launch { sheetState.hide() }.invokeOnCompletion {
                    showAccountSheet = false
                }
            },
            onSaveAccount = viewModel::saveAccount,
            onDeleteAccount = viewModel::deleteAccount
        )
    }

    if (showInfoSheet) {
        InfoGuideSheet(
            sheetState = infoSheetState,
            initialTab = infoSheetTab,
            developerName = uiState.developerName,
            onDismiss = { showInfoSheet = false }
        )
    }

    if (showSettingsSheet) {
        SettingsSheet(
            sheetState = settingsSheetState,
            themeMode = uiState.themeMode,
            currentLanguage = uiState.currentLanguage,
            pipPrecision = uiState.pipPrecision,
            hapticEnabled = uiState.hapticEnabled,
            onSelectTheme = { mode -> viewModel.setThemeMode(mode) },
            onSelectLanguage = { lang -> viewModel.setCurrentLanguage(lang) },
            onSelectPipPrecision = { prec -> viewModel.setPipPrecision(prec) },
            onToggleHaptic = { enabled -> viewModel.setHapticEnabled(enabled) },
            onLoadExnessCentScenario = { viewModel.loadExnessCentScenario() },
            onDismiss = { showSettingsSheet = false }
        )
    }
}

/**
 * 1. Active Trading Account display & switcher
 */
@Composable
private fun ActiveAccountInputSection(
    account: TradingAccount,
    onClick: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "1. Active Trading Account",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.SemiBold,
                color = TextSecondary
            )
            Surface(
                color = CardBorderDark,
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier.clickable { onClick() }
            ) {
                Text(
                    text = "Switch Account",
                    color = GoldLight,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
            }
        }

        Surface(
            color = SurfaceDark,
            shape = RoundedCornerShape(10.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, CardBorderDark),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(GoldContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountBalanceWallet,
                            contentDescription = null,
                            tint = GoldPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = account.name,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "${account.symbol} • Contract: ${account.contractSize.toInt()} oz/lot • Min: ${account.minLot} • Step: ${account.lotStep}",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextTertiary
                        )
                    }
                }

                Surface(
                    color = if (account.accountCurrency == AccountCurrency.USC) GoldContainer else SurfaceDark,
                    shape = RoundedCornerShape(6.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, CardBorderGold)
                ) {
                    Text(
                        text = account.accountCurrency.name,
                        color = GoldLight,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

/**
 * 2. Account Balance Section
 * Displays balance in both USC and USD:
 * Example: Account Balance: 1,220.3 USC ≈ $12.20 USD
 */
@Composable
private fun AccountBalanceInputSection(
    account: TradingAccount,
    onClick: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = "2. Account Balance",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = TextSecondary
        )

        Surface(
            color = SurfaceDark,
            shape = RoundedCornerShape(10.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, CardBorderDark),
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                val isUSC = account.accountCurrency == AccountCurrency.USC
                val balanceText = if (isUSC) {
                    String.format(Locale.US, "%,.1f USC  ≈  $%,.2f USD", account.balance, account.balanceInUSD)
                } else {
                    String.format(Locale.US, "$%,.2f USD  ≈  %,.0f USC", account.balance, account.balanceInUSC)
                }

                Column {
                    Text(
                        text = balanceText,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        color = GoldLight
                    )
                    Text(
                        text = "Conversion: 100 USC = $1.00 USD",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextTertiary
                    )
                }

                Icon(
                    imageVector = Icons.Default.Tune,
                    contentDescription = "Edit Balance",
                    tint = TextSecondary,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

/**
 * 3. Risk Mode Input Section
 * Percentage vs Fixed Money
 */
@Composable
private fun RiskModeInputSection(
    riskMode: RiskMode,
    activeAccount: TradingAccount?,
    onRiskModeChange: (RiskMode) -> Unit
) {
    val currencyName = activeAccount?.accountCurrency?.name ?: "USC"
    val isPercentage = riskMode == RiskMode.PERCENTAGE

    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            text = "3. Risk Mode",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = TextSecondary
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(SurfaceDark)
                .border(1.dp, CardBorderDark, RoundedCornerShape(10.dp))
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Fixed Amount Mode
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(38.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (!isPercentage) GoldPrimary else Color.Transparent)
                    .clickable { onRiskModeChange(RiskMode.FIXED_AMOUNT) }
                    .testTag("risk_mode_fixed_button"),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Fixed Money ($currencyName)",
                    fontWeight = FontWeight.Bold,
                    color = if (!isPercentage) Color.Black else TextSecondary,
                    style = MaterialTheme.typography.labelMedium
                )
            }

            // Percentage Mode
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(38.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isPercentage) GoldPrimary else Color.Transparent)
                    .clickable { onRiskModeChange(RiskMode.PERCENTAGE) }
                    .testTag("risk_mode_percentage_button"),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Percentage (%)",
                    fontWeight = FontWeight.Bold,
                    color = if (isPercentage) Color.Black else TextSecondary,
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
    }
}

/**
 * 4. Risk Amount Input Section
 * Dual currency display: Risk Amount: 100 USC ≈ $1.00 USD
 */
@Composable
private fun RiskAmountInputSection(
    riskMode: RiskMode,
    riskValue: String,
    activeAccount: TradingAccount?,
    onRiskValueChange: (String) -> Unit,
    onQuickPercent: (Double) -> Unit
) {
    val currency = activeAccount?.accountCurrency ?: AccountCurrency.USC
    val isPercentage = riskMode == RiskMode.PERCENTAGE
    val balance = activeAccount?.balance ?: 1220.3

    // Computed preview monetary risk
    val rVal = riskValue.toDoubleOrNull() ?: 0.0
    val riskInAccount = if (isPercentage) balance * (rVal / 100.0) else rVal
    val isUSC = currency == AccountCurrency.USC
    val riskUSD = if (isUSC) riskInAccount / 100.0 else riskInAccount
    val riskUSC = if (isUSC) riskInAccount else riskInAccount * 100.0

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "4. Risk Amount",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = TextSecondary
        )

        // Quick Preset Buttons
        if (isPercentage) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                val percentPresets = listOf(0.5, 1.0, 2.0, 3.0)
                val currentPct = riskValue.toDoubleOrNull()
                val isCustom = currentPct == null || percentPresets.none { Math.abs(it - currentPct) < 0.001 }

                percentPresets.forEach { pct ->
                    val isSelected = !isCustom && currentPct != null && Math.abs(pct - currentPct) < 0.001
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSelected) GoldPrimary else SurfaceDark,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isSelected) GoldPrimary else CardBorderDark
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onQuickPercent(pct) }
                    ) {
                        Text(
                            text = "${pct}%",
                            textAlign = TextAlign.Center,
                            color = if (isSelected) Color.Black else TextPrimary,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            modifier = Modifier.padding(vertical = 6.dp)
                        )
                    }
                }

                // Custom preset pill
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isCustom) GoldPrimary else SurfaceDark,
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (isCustom) GoldPrimary else CardBorderDark
                    ),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = "Custom",
                        textAlign = TextAlign.Center,
                        color = if (isCustom) Color.Black else TextPrimary,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = if (isCustom) FontWeight.Bold else FontWeight.Medium,
                        modifier = Modifier.padding(vertical = 6.dp)
                    )
                }
            }
        } else {
            // Fixed Amount Presets (Requirement 8)
            val currentVal = riskValue.toDoubleOrNull()
            if (isUSC) {
                // 50 USC | 100 USC | 200 USC | Custom
                val uscPresets = listOf(
                    Triple(50.0, "50 USC", "≈ $0.50"),
                    Triple(100.0, "100 USC", "≈ $1.00"),
                    Triple(200.0, "200 USC", "≈ $2.00")
                )
                val isCustom = currentVal == null || uscPresets.none { Math.abs(it.first - currentVal) < 0.001 }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    uscPresets.forEach { (amt, label, conv) ->
                        val isSelected = !isCustom && currentVal != null && Math.abs(amt - currentVal) < 0.001
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) GoldPrimary else SurfaceDark,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) GoldPrimary else CardBorderDark
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onRiskValueChange(if (amt % 1.0 == 0.0) amt.toLong().toString() else amt.toString()) }
                        ) {
                            Column(
                                modifier = Modifier.padding(vertical = 4.dp, horizontal = 2.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = label,
                                    color = if (isSelected) Color.Black else TextPrimary,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                                Text(
                                    text = conv,
                                    color = if (isSelected) Color.Black.copy(alpha = 0.8f) else GoldLight,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    // Custom pill
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isCustom) GoldPrimary else SurfaceDark,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isCustom) GoldPrimary else CardBorderDark
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 4.dp, horizontal = 2.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Custom",
                                color = if (isCustom) Color.Black else TextPrimary,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (isCustom) FontWeight.Bold else FontWeight.Medium
                            )
                            Text(
                                text = "Enter amt",
                                color = if (isCustom) Color.Black.copy(alpha = 0.8f) else TextTertiary,
                                fontSize = 9.sp
                            )
                        }
                    }
                }
            } else {
                // USD Presets: $10 | $25 | $50 | Custom with conversions (≈ 1,000 USC, ≈ 2,500 USC, ≈ 5,000 USC)
                val usdPresets = listOf(
                    Triple(10.0, "$10", "≈ 1,000 USC"),
                    Triple(25.0, "$25", "≈ 2,500 USC"),
                    Triple(50.0, "$50", "≈ 5,000 USC")
                )
                val isCustom = currentVal == null || usdPresets.none { Math.abs(it.first - currentVal) < 0.001 }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    usdPresets.forEach { (amt, label, conv) ->
                        val isSelected = !isCustom && currentVal != null && Math.abs(amt - currentVal) < 0.001
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) GoldPrimary else SurfaceDark,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) GoldPrimary else CardBorderDark
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { onRiskValueChange(if (amt % 1.0 == 0.0) amt.toLong().toString() else amt.toString()) }
                        ) {
                            Column(
                                modifier = Modifier.padding(vertical = 4.dp, horizontal = 2.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = label,
                                    color = if (isSelected) Color.Black else TextPrimary,
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                                Text(
                                    text = conv,
                                    color = if (isSelected) Color.Black.copy(alpha = 0.8f) else GoldLight,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }

                    // Custom pill
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isCustom) GoldPrimary else SurfaceDark,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isCustom) GoldPrimary else CardBorderDark
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 4.dp, horizontal = 2.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Custom",
                                color = if (isCustom) Color.Black else TextPrimary,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (isCustom) FontWeight.Bold else FontWeight.Medium
                            )
                            Text(
                                text = "Enter amt",
                                color = if (isCustom) Color.Black.copy(alpha = 0.8f) else TextTertiary,
                                fontSize = 9.sp
                            )
                        }
                    }
                }
            }
        }

        OutlinedTextField(
            value = riskValue,
            onValueChange = onRiskValueChange,
            label = {
                Text(if (isPercentage) "Risk Percentage (%)" else "Risk Amount (${currency.name})")
            },
            supportingText = {
                // Clearly display both currencies
                val dualCurrencyRisk = if (isUSC) {
                    String.format(Locale.US, "Risk Amount: %,.0f USC  ≈  $%,.2f USD", riskUSC, riskUSD)
                } else {
                    String.format(Locale.US, "Risk Amount: $%,.2f USD  ≈  %,.0f USC", riskUSD, riskUSC)
                }
                Text(
                    text = dualCurrencyRisk,
                    color = GoldLight,
                    fontWeight = FontWeight.SemiBold
                )
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("input_risk_amount"),
            colors = inputFieldColors()
        )
    }
}

/**
 * THE MOST PROMINENT RESULT CARD ON THE SCREEN:
 * "RECOMMENDED LOT SIZE"
 * Under it, displays:
 * - Stop-Loss Distance
 * - Risk Amount (in both currencies)
 * - Risk-to-Reward Ratio (or "TP not provided")
 * - Potential Profit (in both currencies or "TP not provided")
 */
@Composable
fun RecommendedLotSizeCard(
    calculationResult: CalculationResult,
    activeAccount: TradingAccount?,
    pipPrecision: Int = 3,
    language: AppLanguage = AppLanguage.ENGLISH,
    onCopy: (String) -> Unit
) {
    val colors = AppTheme.colors
    val strings = AppLocalization.get(language)
    val isValid = calculationResult.isValidInput
    val isBelowMin = calculationResult.isBelowMinLot
    val lotStep = activeAccount?.lotStep ?: 0.01

    val executableLotFormatted = if (isValid && !isBelowMin) {
        CalculatorEngine.formatLot(calculationResult.executableLotSize, lotStep)
    } else if (isValid && isBelowMin) {
        "0.00"
    } else {
        "--.--"
    }

    val exactLotFormatted = if (isValid) {
        String.format(Locale.US, "%.4f", calculationResult.exactLotSize)
    } else {
        "--"
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("recommended_lot_size_card"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = colors.surface),
        border = androidx.compose.foundation.BorderStroke(
            width = 2.dp,
            color = if (isValid && !isBelowMin) colors.goldPrimary else colors.cardBorderGold
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header row with GoldRisk badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(if (isValid) colors.goldPrimary else colors.textTertiary)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = strings.recommendedLotSize.uppercase(Locale.US),
                        style = MaterialTheme.typography.labelMedium,
                        letterSpacing = 1.2.sp,
                        fontWeight = FontWeight.Bold,
                        color = colors.goldPrimary
                    )
                }

                Surface(
                    color = if (isValid && !isBelowMin) colors.buyGreenContainer else colors.surface,
                    shape = RoundedCornerShape(6.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, if (isValid && !isBelowMin) colors.buyGreen else colors.cardBorder)
                ) {
                    val cs = activeAccount?.contractSize?.toInt() ?: calculationResult.contractSize.toInt()
                    Text(
                        text = "${strings.specContract.uppercase(Locale.US)} = $cs",
                        color = if (isValid && !isBelowMin) colors.buyGreen else colors.textTertiary,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // =========================================================================
            // HUGE PROMINENT LOT SIZE DISPLAY
            // =========================================================================
            Row(
                verticalAlignment = Alignment.Bottom,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = executableLotFormatted,
                    style = MaterialTheme.typography.displayLarge.copy(
                        fontSize = 56.sp,
                        fontWeight = FontWeight.Black
                    ),
                    color = if (isValid && !isBelowMin) GoldPrimary else TextTertiary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.testTag("result_lot_size_text")
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "LOT",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Black,
                    color = if (isValid && !isBelowMin) GoldLight else TextTertiary,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }

            // Warning if exact calculated lot is below minimum (Requirement: MINIMUM LOT VALIDATION)
            if (isValid && isBelowMin) {
                Spacer(modifier = Modifier.height(10.dp))
                Surface(
                    color = SellRedContainer,
                    shape = RoundedCornerShape(10.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, SellRed),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("min_lot_warning_box")
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // Notice: "Calculated lot size is below the broker minimum volume of 0.01."
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Info,
                                contentDescription = null,
                                tint = WarningAmber,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Calculated lot size is below the broker minimum volume of ${calculationResult.minLot}.",
                                color = WarningAmber,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Display a warning if trading the minimum lot would exceed the intended risk:
                        // "Warning: The minimum lot size exceeds your selected risk."
                        if (calculationResult.minLotExceedsRisk) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Warning,
                                    contentDescription = null,
                                    tint = SellRed,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Warning: The minimum lot size exceeds your selected risk.",
                                    color = SellRed,
                                    style = MaterialTheme.typography.bodySmall,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        HorizontalDivider(color = CardBorderDark.copy(alpha = 0.6f), thickness = 1.dp)

                        // Calculate and display:
                        // - Exact calculated lot size
                        // - Broker minimum lot
                        // - Actual risk at 0.01 lot
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Exact Calculated Lot: $exactLotFormatted lots",
                                style = MaterialTheme.typography.labelSmall,
                                color = TextSecondary
                            )
                            Text(
                                text = "Broker Minimum Lot: ${calculationResult.minLot} LOT",
                                style = MaterialTheme.typography.labelSmall,
                                color = if (calculationResult.minLotExceedsRisk) SellRed else TextPrimary,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        val isUSC = activeAccount?.accountCurrency == AccountCurrency.USC
                        val estRiskText = if (isUSC) {
                            String.format(Locale.US, "%,.0f USC  ≈  $%,.2f USD", calculationResult.estimatedActualRiskAtMinLotUSC, calculationResult.estimatedActualRiskAtMinLotUSD)
                        } else {
                            String.format(Locale.US, "$%,.2f USD  ≈  %,.0f USC", calculationResult.estimatedActualRiskAtMinLotUSD, calculationResult.estimatedActualRiskAtMinLotUSC)
                        }
                        Text(
                            text = "Actual Risk at ${calculationResult.minLot} Lot: $estRiskText",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                            color = if (calculationResult.minLotExceedsRisk) SellRed else WarningAmber
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Sub-row: Exact Lot & Copy Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = if (isValid) "Exact Lot Size: $exactLotFormatted lots" else "Exact Lot Size: --",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Rounded down to valid lot step (${lotStep})",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextTertiary
                    )
                }

                Button(
                    onClick = {
                        if (isValid && !isBelowMin) {
                            onCopy(executableLotFormatted)
                        }
                    },
                    enabled = isValid && !isBelowMin,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GoldPrimary,
                        contentColor = Color.Black,
                        disabledContainerColor = CardBorderDark,
                        disabledContentColor = TextTertiary
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("copy_lot_size_button")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.ContentCopy,
                            contentDescription = "Copy Lot Size",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = strings.copyLot,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            HorizontalDivider(color = colors.cardBorder, thickness = 1.dp)
            Spacer(modifier = Modifier.height(12.dp))

            // =========================================================================
            // UNDER IT: 5 KEY METRICS
            // 1. Risk Amount: 100 USC = 1 USD
            // 2. Stop-Loss Distance:
            // 3. Recommended Lot Size: (shown above)
            // 4. Risk-to-Reward Ratio: (if TP is provided)
            // 5. Potential Profit: (if TP is provided)
            // =========================================================================

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val isUSC = activeAccount?.accountCurrency == AccountCurrency.USC

                // 1. Risk Amount (100 USC = 1 USD)
                val riskAmountDualText = if (isValid) {
                    if (isUSC) {
                        String.format(Locale.US, "%,.0f USC = $%,.2f USD", calculationResult.riskAmountUSC, calculationResult.riskAmountUSD)
                    } else {
                        String.format(Locale.US, "$%,.2f USD = %,.0f USC", calculationResult.riskAmountUSD, calculationResult.riskAmountUSC)
                    }
                } else {
                    "--"
                }
                ResultMetricRow(
                    label = strings.riskAmount,
                    value = riskAmountDualText,
                    highlightColor = colors.sellRed
                )

                // 2. Stop-Loss Distance
                val slDistanceText = if (isValid) {
                    String.format(Locale.US, "%.${pipPrecision}f", calculationResult.stopLossDistance)
                } else {
                    "--"
                }
                ResultMetricRow(
                    label = strings.stopLossDistance,
                    value = slDistanceText,
                    subValue = if (isValid) "(Formula: |Entry - SL|)" else null,
                    highlightColor = colors.textPrimary
                )

                // 3. Actual Risk After Lot Rounding
                val actualRiskDualText = if (isValid && !isBelowMin) {
                    if (isUSC) {
                        String.format(Locale.US, "%,.2f USC ≈ $%,.2f USD", calculationResult.actualRiskUSC, calculationResult.actualRiskUSD)
                    } else {
                        String.format(Locale.US, "$%,.2f USD ≈ %,.2f USC", calculationResult.actualRiskUSD, calculationResult.actualRiskUSC)
                    }
                } else if (isValid && isBelowMin) {
                    "Below minimum lot"
                } else {
                    "--"
                }
                ResultMetricRow(
                    label = strings.actualRisk,
                    value = actualRiskDualText,
                    subValue = if (isValid && !isBelowMin) "Never exceeds intended risk (safe round down)" else null,
                    highlightColor = if (isValid && !isBelowMin) colors.goldLight else colors.textSecondary
                )

                // 4. Risk-to-Reward Ratio: (if TP is provided)
                val rrRatioText = if (isValid) {
                    if (calculationResult.riskRewardRatio != null) {
                        String.format(Locale.US, "1 : %.2f", calculationResult.riskRewardRatio)
                    } else {
                        "TP not provided"
                    }
                } else {
                    "TP not provided"
                }
                ResultMetricRow(
                    label = strings.riskRewardRatio,
                    value = rrRatioText,
                    highlightColor = if (calculationResult.riskRewardRatio != null) colors.buyGreen else colors.textSecondary
                )

                // 5. Potential Profit: (if TP is provided)
                val potentialProfitText = if (isValid && calculationResult.potentialProfitUSD != null) {
                    val profitUSD = calculationResult.potentialProfitUSD ?: 0.0
                    val profitUSC = calculationResult.potentialProfitUSC ?: 0.0
                    if (isUSC) {
                        String.format(Locale.US, "%,.2f USC ≈ $%,.2f USD", profitUSC, profitUSD)
                    } else {
                        String.format(Locale.US, "$%,.2f USD ≈ %,.2f USC", profitUSD, profitUSC)
                    }
                } else {
                    "TP not provided"
                }
                ResultMetricRow(
                    label = strings.potentialProfit,
                    value = potentialProfitText,
                    highlightColor = if (calculationResult.potentialProfitUSD != null) colors.buyGreen else colors.textSecondary
                )
            }
        }
    }
}

@Composable
private fun ResultMetricRow(
    label: String,
    value: String,
    subValue: String? = null,
    highlightColor: Color = AppTheme.colors.textPrimary
) {
    val colors = AppTheme.colors
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(colors.cardBackground.copy(alpha = 0.65f))
            .padding(horizontal = 10.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = colors.textSecondary
            )
            if (subValue != null) {
                Text(
                    text = subValue,
                    style = MaterialTheme.typography.labelSmall,
                    fontSize = 10.sp,
                    color = colors.textTertiary
                )
            }
        }

        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = highlightColor
        )
    }
}

/**
 * Validation warning banner.
 */
@Composable
fun TradeValidationWarning(warning: String) {
    Surface(
        color = WarningAmberContainer,
        shape = RoundedCornerShape(10.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, WarningAmber)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = WarningAmber,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = warning,
                color = WarningAmber,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

/**
 * Position Type Selector: BUY (Green) vs SELL (Red)
 */
@Composable
fun PositionTypeSelector(
    selected: PositionType,
    onSelect: (PositionType) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceDark)
            .border(1.dp, CardBorderDark, RoundedCornerShape(12.dp))
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // BUY Button
        val isBuy = selected == PositionType.BUY
        val buyBg by animateColorAsState(if (isBuy) BuyGreen else Color.Transparent)
        val buyContent by animateColorAsState(if (isBuy) Color.Black else BuyGreen)

        Box(
            modifier = Modifier
                .weight(1f)
                .height(44.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(buyBg)
                .clickable { onSelect(PositionType.BUY) }
                .testTag("select_buy_button"),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowUpward,
                    contentDescription = null,
                    tint = buyContent,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "BUY / LONG",
                    fontWeight = FontWeight.Black,
                    style = MaterialTheme.typography.titleSmall,
                    color = buyContent
                )
            }
        }

        // SELL Button
        val isSell = selected == PositionType.SELL
        val sellBg by animateColorAsState(if (isSell) SellRed else Color.Transparent)
        val sellContent by animateColorAsState(if (isSell) Color.White else SellRed)

        Box(
            modifier = Modifier
                .weight(1f)
                .height(44.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(sellBg)
                .clickable { onSelect(PositionType.SELL) }
                .testTag("select_sell_button"),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowDownward,
                    contentDescription = null,
                    tint = sellContent,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "SELL / SHORT",
                    fontWeight = FontWeight.Black,
                    style = MaterialTheme.typography.titleSmall,
                    color = sellContent
                )
            }
        }
    }
}

@Composable
private fun inputFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = GoldPrimary,
    unfocusedBorderColor = CardBorderDark,
    focusedLabelColor = GoldLight,
    unfocusedLabelColor = TextSecondary,
    cursorColor = GoldPrimary,
    focusedTextColor = TextPrimary,
    unfocusedTextColor = TextPrimary,
    focusedPlaceholderColor = TextTertiary,
    unfocusedPlaceholderColor = TextTertiary,
    focusedContainerColor = SurfaceDark,
    unfocusedContainerColor = SurfaceDark
)

/**
 * Active Account & Symbol Specifications Banner
 * Always clearly displays:
 * - Account Name
 * - Symbol
 * - Account Currency
 * - Contract Size
 * - Minimum Lot
 * - Lot Step
 * plus one-tap switching between Exness Standard Cent & Exness Standard
 */
@Composable
private fun ActiveAccountProfileHeader(
    activeAccount: TradingAccount,
    onSelectProfile: (String) -> Unit,
    onOpenManage: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("account_profile_header"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackgroundDark),
        border = BorderStroke(1.dp, CardBorderGold)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Top Row: Title + Manage Accounts
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(GoldContainer),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = null,
                            tint = GoldPrimary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "ACCOUNT PROFILE",
                        style = MaterialTheme.typography.labelMedium,
                        letterSpacing = 1.2.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldLight
                    )
                }

                Surface(
                    color = SurfaceDark,
                    shape = RoundedCornerShape(6.dp),
                    border = BorderStroke(1.dp, CardBorderDark),
                    modifier = Modifier.clickable { onOpenManage() }
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "Manage Accounts",
                            color = GoldLight,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            // Quick Account Switcher Tabs
            val isCentActive = activeAccount.symbol.equals("XAUUSDc", ignoreCase = true) ||
                    activeAccount.name.contains("Cent", ignoreCase = true)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Profile 1: Exness Standard Cent
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onSelectProfile("XAUUSDc") }
                        .testTag("switch_to_cent_profile"),
                    color = if (isCentActive) GoldPrimary else SurfaceDark,
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(
                        1.dp,
                        if (isCentActive) GoldPrimary else CardBorderDark
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Exness Standard Cent",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (isCentActive) Color.Black else TextPrimary,
                            maxLines = 1
                        )
                        Text(
                            text = "XAUUSDc • 1 oz/lot • USC",
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 10.sp,
                            color = if (isCentActive) Color.Black.copy(alpha = 0.85f) else GoldLight
                        )
                    }
                }

                // Profile 2: Exness Standard
                Surface(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onSelectProfile("XAUUSDm") }
                        .testTag("switch_to_standard_profile"),
                    color = if (!isCentActive) GoldPrimary else SurfaceDark,
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(
                        1.dp,
                        if (!isCentActive) GoldPrimary else CardBorderDark
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Exness Standard",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = if (!isCentActive) Color.Black else TextPrimary,
                            maxLines = 1
                        )
                        Text(
                            text = "XAUUSDm • 100 oz/lot • USD",
                            style = MaterialTheme.typography.labelSmall,
                            fontSize = 10.sp,
                            color = if (!isCentActive) Color.Black.copy(alpha = 0.85f) else GoldLight
                        )
                    }
                }
            }

            HorizontalDivider(color = CardBorderDark.copy(alpha = 0.6f), thickness = 1.dp)

            // Specifications display:
            // - Account Name
            // - Symbol
            // - Account Currency
            // - Contract Size
            // - Minimum Lot
            // - Lot Step
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    SpecItem(label = "Account Name", value = activeAccount.name)
                    SpecItem(label = "Symbol", value = activeAccount.symbol, isHighlighted = true)
                    SpecItem(label = "Currency", value = activeAccount.accountCurrency.name)
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    SpecItem(
                        label = "Contract Size",
                        value = "${activeAccount.contractSize.toInt()} oz / lot",
                        isHighlighted = true
                    )
                    SpecItem(label = "Minimum Lot", value = "${activeAccount.minLot}")
                    SpecItem(label = "Lot Step", value = "${activeAccount.lotStep}")
                }
            }
        }
    }
}

@Composable
private fun SpecItem(
    label: String,
    value: String,
    isHighlighted: Boolean = false
) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontSize = 10.sp,
            color = TextTertiary
        )
        Text(
            text = value,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = if (isHighlighted) GoldLight else TextPrimary
        )
    }
}

