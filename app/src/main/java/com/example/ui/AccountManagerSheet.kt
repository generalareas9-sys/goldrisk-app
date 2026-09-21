package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.TradingAccount
import com.example.model.AccountCurrency
import com.example.model.RiskMode
import com.example.ui.theme.BuyGreen
import com.example.ui.theme.CardBackgroundDark
import com.example.ui.theme.CardBorderDark
import com.example.ui.theme.GoldContainer
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.OnGoldContainer
import com.example.ui.theme.SellRed
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountManagerSheet(
    accounts: List<TradingAccount>,
    activeAccount: TradingAccount?,
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onSelectActiveAccount: (Long) -> Unit,
    onSaveAccount: (
        id: Long,
        name: String,
        brokerName: String,
        symbol: String,
        accountType: String,
        balance: Double,
        currency: AccountCurrency,
        defaultRiskAmount: Double,
        defaultRiskPercentage: Double,
        riskMode: RiskMode,
        contractSize: Double,
        minLot: Double,
        lotStep: Double,
        maxLot: Double?
    ) -> Unit,
    onDeleteAccount: (TradingAccount) -> Unit
) {
    var accountToEdit by remember { mutableStateOf<TradingAccount?>(null) }
    var isAddingNewAccount by remember { mutableStateOf(false) }
    var accountToDelete by remember { mutableStateOf<TradingAccount?>(null) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 12.dp)
                    .width(40.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(TextTertiary.copy(alpha = 0.5f))
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Trading Accounts",
                        style = MaterialTheme.typography.titleLarge,
                        color = TextPrimary
                    )
                    Text(
                        text = "Select active account or customize settings",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary
                    )
                }

                Button(
                    onClick = { isAddingNewAccount = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GoldPrimary,
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("add_account_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Account",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "New", fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Account List
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, fill = false),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(accounts, key = { it.id }) { account ->
                    val isActive = account.id == activeAccount?.id
                    AccountListItem(
                        account = account,
                        isActive = isActive,
                        onSelect = { onSelectActiveAccount(account.id) },
                        onEdit = { accountToEdit = account },
                        onDelete = { accountToDelete = account }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }

    // Add or Edit Dialog
    if (isAddingNewAccount || accountToEdit != null) {
        AccountEditDialog(
            account = accountToEdit,
            onDismiss = {
                isAddingNewAccount = false
                accountToEdit = null
            },
            onSave = { id, name, broker, symbol, type, bal, curr, riskAmt, riskPct, mode, contract, minLot, lotStep, maxLot ->
                onSaveAccount(id, name, broker, symbol, type, bal, curr, riskAmt, riskPct, mode, contract, minLot, lotStep, maxLot)
                isAddingNewAccount = false
                accountToEdit = null
            }
        )
    }

    // Delete Confirmation Dialog
    if (accountToDelete != null) {
        AlertDialog(
            onDismissRequest = { accountToDelete = null },
            title = { Text("Delete Account?", color = TextPrimary) },
            text = {
                Text(
                    "Are you sure you want to delete '${accountToDelete?.name}'? This action cannot be undone.",
                    color = TextSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        accountToDelete?.let { onDeleteAccount(it) }
                        accountToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SellRed)
                ) {
                    Text("Delete", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { accountToDelete = null }) {
                    Text("Cancel", color = TextSecondary)
                }
            },
            containerColor = CardBackgroundDark
        )
    }
}

@Composable
private fun AccountListItem(
    account: TradingAccount,
    isActive: Boolean,
    onSelect: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = if (isActive) 2.dp else 1.dp,
                color = if (isActive) GoldPrimary else CardBorderDark,
                shape = RoundedCornerShape(14.dp)
            )
            .clickable { onSelect() }
            .testTag("account_item_${account.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isActive) GoldContainer.copy(alpha = 0.35f) else CardBackgroundDark
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Radio Indicator
            RadioButton(
                selected = isActive,
                onClick = onSelect,
                colors = RadioButtonDefaults.colors(
                    selectedColor = GoldPrimary,
                    unselectedColor = TextTertiary
                )
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = account.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    CurrencyBadge(currency = account.accountCurrency)

                    if (isActive) {
                        Spacer(modifier = Modifier.width(6.dp))
                        ActiveBadge()
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Balance with auto conversion
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (account.accountCurrency == AccountCurrency.USC) {
                        Text(
                            text = String.format(Locale.US, "%,.0f USC", account.balance),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = GoldLight
                        )
                        Text(
                            text = String.format(Locale.US, " (≈ $%,.2f USD)", account.balanceInUSD),
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    } else {
                        Text(
                            text = String.format(Locale.US, "$%,.2f USD", account.balance),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = GoldLight
                        )
                        Text(
                            text = String.format(Locale.US, " (≈ %,.0f USC)", account.balanceInUSC),
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "${account.brokerName} • ${account.symbol} • ${account.accountType}",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary,
                    fontWeight = FontWeight.Medium
                )

                Text(
                    text = "Contract: ${account.contractSize} oz | Step: ${account.lotStep} | Min: ${account.minLot}${if (account.maxLot != null) " | Max: ${account.maxLot}" else ""}",
                    style = MaterialTheme.typography.labelSmall,
                    color = TextTertiary
                )
            }

            // Edit & Delete
            IconButton(
                onClick = onEdit,
                modifier = Modifier.size(36.dp).testTag("edit_account_${account.id}")
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit Account",
                    tint = TextSecondary,
                    modifier = Modifier.size(18.dp)
                )
            }

            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(36.dp).testTag("delete_account_${account.id}")
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete Account",
                    tint = SellRed.copy(alpha = 0.8f),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
fun CurrencyBadge(currency: AccountCurrency) {
    val isUSC = currency == AccountCurrency.USC
    Surface(
        color = if (isUSC) Color(0xFF2E1A47) else Color(0xFF0F3622),
        shape = RoundedCornerShape(6.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isUSC) Color(0xFF9D65E8) else BuyGreen
        )
    ) {
        Text(
            text = currency.name,
            color = if (isUSC) Color(0xFFD6BBFC) else BuyGreen,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
        )
    }
}

@Composable
private fun ActiveBadge() {
    Surface(
        color = GoldPrimary,
        shape = RoundedCornerShape(6.dp)
    ) {
        Text(
            text = "ACTIVE",
            color = Color.Black,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            fontSize = 9.sp,
            modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
        )
    }
}

@Composable
fun AccountEditDialog(
    account: TradingAccount?,
    onDismiss: () -> Unit,
    onSave: (
        id: Long,
        name: String,
        brokerName: String,
        symbol: String,
        accountType: String,
        balance: Double,
        currency: AccountCurrency,
        defaultRiskAmount: Double,
        defaultRiskPercentage: Double,
        riskMode: RiskMode,
        contractSize: Double,
        minLot: Double,
        lotStep: Double,
        maxLot: Double?
    ) -> Unit
) {
    val isEdit = account != null
    var name by remember { mutableStateOf(account?.name ?: if (isEdit) "" else "Exness Standard Cent") }
    var brokerName by remember { mutableStateOf(account?.brokerName ?: "Exness") }
    var symbol by remember { mutableStateOf(account?.symbol ?: "XAUUSDc") }
    var accountType by remember { mutableStateOf(account?.accountType ?: "Exness Standard Cent Account") }
    var balanceText by remember { mutableStateOf(account?.balance?.let { if (it % 1.0 == 0.0) it.toLong().toString() else it.toString() } ?: "1220.3") }
    var currency by remember { mutableStateOf(account?.accountCurrency ?: AccountCurrency.USC) }
    var defaultRiskAmountText by remember { mutableStateOf(account?.defaultRiskAmount?.let { if (it % 1.0 == 0.0) it.toLong().toString() else it.toString() } ?: "100") }
    var defaultRiskPercentageText by remember { mutableStateOf(account?.defaultRiskPercentage?.let { if (it % 1.0 == 0.0) it.toLong().toString() else it.toString() } ?: "1.0") }
    var riskMode by remember { mutableStateOf(account?.accountRiskMode ?: RiskMode.FIXED_AMOUNT) }
    var contractSizeText by remember { mutableStateOf(account?.contractSize?.let { if (it % 1.0 == 0.0) it.toLong().toString() else it.toString() } ?: "1.0") }
    var minLotText by remember { mutableStateOf(account?.minLot?.toString() ?: "0.01") }
    var lotStepText by remember { mutableStateOf(account?.lotStep?.toString() ?: "0.01") }
    var maxLotText by remember { mutableStateOf(account?.maxLot?.toString() ?: "100.0") }

    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Live converted balance preview
    val currentBalance = balanceText.toDoubleOrNull() ?: 0.0
    val convertedBalancePreview = if (currency == AccountCurrency.USC) {
        String.format(Locale.US, "≈ $%,.2f USD", currentBalance / 100.0)
    } else {
        String.format(Locale.US, "≈ %,.0f USC", currentBalance * 100.0)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = CardBackgroundDark,
        title = {
            Text(
                text = if (isEdit) "Edit Account" else "Add New Account",
                style = MaterialTheme.typography.titleLarge,
                color = TextPrimary
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Section 1: Basic Information
                Text(
                    text = "Basic Information",
                    style = MaterialTheme.typography.labelLarge,
                    color = GoldLight,
                    fontWeight = FontWeight.Bold
                )

                // Account Name
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Account Name") },
                    placeholder = { Text("e.g. Exness Standard Cent") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_account_name"),
                    colors = inputFieldColors()
                )

                // Broker Name (optional)
                OutlinedTextField(
                    value = brokerName,
                    onValueChange = { brokerName = it },
                    label = { Text("Broker Name (optional)") },
                    placeholder = { Text("e.g. Exness, IC Markets") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_broker_name"),
                    colors = inputFieldColors()
                )

                // Account Type & Symbol
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = accountType,
                        onValueChange = { accountType = it },
                        label = { Text("Account Type") },
                        placeholder = { Text("e.g. Standard Cent") },
                        singleLine = true,
                        modifier = Modifier.weight(1.3f).testTag("input_account_type"),
                        colors = inputFieldColors()
                    )

                    OutlinedTextField(
                        value = symbol,
                        onValueChange = { symbol = it },
                        label = { Text("Symbol") },
                        placeholder = { Text("e.g. XAUUSDc") },
                        singleLine = true,
                        modifier = Modifier.weight(1f).testTag("input_symbol"),
                        colors = inputFieldColors()
                    )
                }

                // Currency selector
                Column {
                    Text(
                        text = "Account Currency",
                        style = MaterialTheme.typography.labelMedium,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        AccountCurrency.values().forEach { curr ->
                            val selected = currency == curr
                            FilterChip(
                                selected = selected,
                                onClick = { currency = curr },
                                label = {
                                    Text(
                                        text = if (curr == AccountCurrency.USC) "USC (Cent: 100 USC = $1)" else "USD (Standard)",
                                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                modifier = Modifier.weight(1f).testTag("currency_chip_${curr.name}"),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = GoldPrimary,
                                    selectedLabelColor = Color.Black
                                )
                            )
                        }
                    }
                }

                // Balance
                OutlinedTextField(
                    value = balanceText,
                    onValueChange = { balanceText = it },
                    label = { Text("Account Balance (${currency.name})") },
                    supportingText = {
                        Text(convertedBalancePreview, color = GoldLight)
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_account_balance"),
                    colors = inputFieldColors()
                )

                HorizontalDivider(color = CardBorderDark, thickness = 1.dp)

                // Section 2: Risk Settings
                Text(
                    text = "Risk Settings",
                    style = MaterialTheme.typography.labelLarge,
                    color = GoldLight,
                    fontWeight = FontWeight.Bold
                )

                // Risk Mode
                Column {
                    Text(
                        text = "Default Risk Mode",
                        style = MaterialTheme.typography.labelMedium,
                        color = TextSecondary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        RiskMode.values().forEach { mode ->
                            val selected = riskMode == mode
                            FilterChip(
                                selected = selected,
                                onClick = { riskMode = mode },
                                label = {
                                    Text(
                                        text = if (mode == RiskMode.PERCENTAGE) "Percentage (%)" else "Fixed Amount",
                                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                modifier = Modifier.weight(1f),
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = GoldPrimary,
                                    selectedLabelColor = Color.Black
                                )
                            )
                        }
                    }
                }

                // Default Risk Amounts
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = defaultRiskAmountText,
                        onValueChange = { defaultRiskAmountText = it },
                        label = { Text("Default Fixed (${currency.name})") },
                        placeholder = { Text(if (currency == AccountCurrency.USC) "100" else "20") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier.weight(1f).testTag("input_default_risk"),
                        colors = inputFieldColors()
                    )

                    OutlinedTextField(
                        value = defaultRiskPercentageText,
                        onValueChange = { defaultRiskPercentageText = it },
                        label = { Text("Default Risk (%)") },
                        placeholder = { Text("1.0") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier.weight(1f).testTag("input_default_risk_pct"),
                        colors = inputFieldColors()
                    )
                }

                HorizontalDivider(color = CardBorderDark, thickness = 1.dp)

                // Section 3: XAUUSD Specifications
                Text(
                    text = "XAUUSD Specifications",
                    style = MaterialTheme.typography.labelLarge,
                    color = GoldLight,
                    fontWeight = FontWeight.Bold
                )

                // Contract Size (Default 1.0)
                OutlinedTextField(
                    value = contractSizeText,
                    onValueChange = { contractSizeText = it },
                    label = { Text("Contract Size (oz per lot)") },
                    supportingText = { Text("Exness Cent (XAUUSDc) = 1 | Exness Standard (XAUUSDm) = 100", color = GoldLight) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("input_contract_size"),
                    colors = inputFieldColors()
                )

                // Broker Minimum Lot, Lot Step, & Maximum Lot
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = minLotText,
                        onValueChange = { minLotText = it },
                        label = { Text("Min Lot") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier.weight(1f).testTag("input_min_lot"),
                        colors = inputFieldColors()
                    )

                    OutlinedTextField(
                        value = lotStepText,
                        onValueChange = { lotStepText = it },
                        label = { Text("Lot Step") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier.weight(1f).testTag("input_lot_step"),
                        colors = inputFieldColors()
                    )

                    OutlinedTextField(
                        value = maxLotText,
                        onValueChange = { maxLotText = it },
                        label = { Text("Max Lot") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        singleLine = true,
                        modifier = Modifier.weight(1f).testTag("input_max_lot"),
                        colors = inputFieldColors()
                    )
                }

                errorMessage?.let {
                    Text(
                        text = it,
                        color = SellRed,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val bal = balanceText.toDoubleOrNull()
                    val riskAmt = defaultRiskAmountText.toDoubleOrNull()
                    val riskPct = defaultRiskPercentageText.toDoubleOrNull()
                    val contract = contractSizeText.toDoubleOrNull()
                    val minLot = minLotText.toDoubleOrNull()
                    val lotStep = lotStepText.toDoubleOrNull()
                    val maxLot = maxLotText.toDoubleOrNull()

                    if (name.isBlank()) {
                        errorMessage = "Please enter an account name"
                        return@Button
                    }
                    if (bal == null || bal <= 0.0) {
                        errorMessage = "Please enter a valid positive balance"
                        return@Button
                    }
                    if (riskAmt == null || riskAmt <= 0.0) {
                        errorMessage = "Please enter a valid default risk amount"
                        return@Button
                    }
                    if (riskPct == null || riskPct <= 0.0) {
                        errorMessage = "Please enter a valid default risk percentage"
                        return@Button
                    }
                    if (contract == null || contract <= 0.0) {
                        errorMessage = "Please enter a valid contract size"
                        return@Button
                    }
                    if (minLot == null || minLot <= 0.0) {
                        errorMessage = "Please enter a valid minimum lot"
                        return@Button
                    }
                    if (lotStep == null || lotStep <= 0.0) {
                        errorMessage = "Please enter a valid lot step"
                        return@Button
                    }

                    onSave(
                        account?.id ?: 0L,
                        name,
                        brokerName,
                        symbol,
                        accountType,
                        bal,
                        currency,
                        riskAmt,
                        riskPct,
                        riskMode,
                        contract,
                        minLot,
                        lotStep,
                        maxLot
                    )
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = GoldPrimary,
                    contentColor = Color.Black
                ),
                modifier = Modifier.testTag("save_account_confirm_button")
            ) {
                Text("Save", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        }
    )
}

@Composable
private fun inputFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedTextColor = TextPrimary,
    unfocusedTextColor = TextPrimary,
    focusedBorderColor = GoldPrimary,
    unfocusedBorderColor = CardBorderDark,
    focusedLabelColor = GoldPrimary,
    unfocusedLabelColor = TextSecondary,
    cursorColor = GoldPrimary
)
