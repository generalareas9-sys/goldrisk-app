package com.example.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Brightness4
import androidx.compose.material.icons.filled.Brightness7
import androidx.compose.material.icons.filled.BrightnessAuto
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.AppLanguage
import com.example.model.ThemeMode
import com.example.ui.localization.AppLocalization
import com.example.ui.localization.LocalizedStrings
import com.example.ui.theme.AppTheme
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsSheet(
    sheetState: SheetState,
    themeMode: ThemeMode,
    currentLanguage: AppLanguage,
    pipPrecision: Int,
    hapticEnabled: Boolean,
    onSelectTheme: (ThemeMode) -> Unit,
    onSelectLanguage: (AppLanguage) -> Unit,
    onSelectPipPrecision: (Int) -> Unit,
    onToggleHaptic: (Boolean) -> Unit,
    onLoadExnessCentScenario: () -> Unit,
    onDismiss: () -> Unit
) {
    val colors = AppTheme.colors
    val strings: LocalizedStrings = AppLocalization.get(currentLanguage)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = colors.surface,
        dragHandle = null,
        modifier = Modifier.testTag("settings_bottom_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Header Row: Logo + Title + Close Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .border(1.2.dp, colors.goldPrimary, RoundedCornerShape(8.dp))
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.goldrisk_app_icon_1788801410920),
                            contentDescription = "GoldRisk",
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = strings.settingsTitle,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = colors.textPrimary
                        )
                        Text(
                            text = "Preferences & Presets",
                            style = MaterialTheme.typography.labelSmall,
                            color = colors.textTertiary
                        )
                    }
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.testTag("close_settings_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = colors.textSecondary
                    )
                }
            }

            HorizontalDivider(color = colors.cardBorder.copy(alpha = 0.6f))

            // =========================================================================
            // 1. THEME MODE: DARK TERMINAL & LIGHT MODE & SYSTEM
            // =========================================================================
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                SettingsSectionHeader(
                    icon = Icons.Default.Brightness4,
                    title = strings.appearanceTheme,
                    subtitle = "Switch between Dark Trading Terminal, Light Mode, or System"
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    ThemeOptionCard(
                        modifier = Modifier.weight(1f),
                        title = strings.themeDark,
                        icon = Icons.Default.Brightness4,
                        isSelected = themeMode == ThemeMode.DARK,
                        onClick = { onSelectTheme(ThemeMode.DARK) },
                        testTag = "theme_option_dark"
                    )
                    ThemeOptionCard(
                        modifier = Modifier.weight(1f),
                        title = strings.themeLight,
                        icon = Icons.Default.Brightness7,
                        isSelected = themeMode == ThemeMode.LIGHT,
                        onClick = { onSelectTheme(ThemeMode.LIGHT) },
                        testTag = "theme_option_light"
                    )
                    ThemeOptionCard(
                        modifier = Modifier.weight(1f),
                        title = strings.themeSystem,
                        icon = Icons.Default.BrightnessAuto,
                        isSelected = themeMode == ThemeMode.SYSTEM,
                        onClick = { onSelectTheme(ThemeMode.SYSTEM) },
                        testTag = "theme_option_system"
                    )
                }
            }

            HorizontalDivider(color = colors.cardBorder.copy(alpha = 0.6f))

            // =========================================================================
            // 2. LANGUAGE SELECTION
            // =========================================================================
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                SettingsSectionHeader(
                    icon = Icons.Default.Language,
                    title = strings.languageSelection,
                    subtitle = "Select trading terminal interface language"
                )

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val languages = AppLanguage.values()
                    val rows = languages.toList().chunked(2)
                    rows.forEach { rowItems ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            rowItems.forEach { lang ->
                                val isSelected = currentLanguage == lang
                                Surface(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clickable { onSelectLanguage(lang) }
                                        .testTag("lang_option_${lang.code}"),
                                    shape = RoundedCornerShape(10.dp),
                                    color = if (isSelected) colors.goldContainer else colors.cardBackground,
                                    border = BorderStroke(
                                        1.dp,
                                        if (isSelected) colors.goldPrimary else colors.cardBorder
                                    )
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(text = lang.flag, fontSize = 18.sp)
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Column {
                                                Text(
                                                    text = lang.nativeName,
                                                    style = MaterialTheme.typography.bodySmall,
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (isSelected) colors.goldLight else colors.textPrimary
                                                )
                                                Text(
                                                    text = lang.displayName,
                                                    style = MaterialTheme.typography.labelSmall,
                                                    fontSize = 10.sp,
                                                    color = colors.textTertiary
                                                )
                                            }
                                        }
                                        if (isSelected) {
                                            Icon(
                                                imageVector = Icons.Default.Check,
                                                contentDescription = "Selected",
                                                tint = colors.goldPrimary,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                }
                            }
                            if (rowItems.size == 1) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }

            HorizontalDivider(color = colors.cardBorder.copy(alpha = 0.6f))

            // =========================================================================
            // 3. FEATURE: ONE-TAP EXNESS CENT SCENARIO PRESET
            // =========================================================================
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("preset_scenario_card"),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = colors.goldContainer.copy(alpha = 0.4f)),
                border = BorderStroke(1.2.dp, colors.goldPrimary)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = null,
                                tint = colors.goldPrimary,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = strings.quickPresetTitle,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = colors.goldLight
                            )
                        }
                        Surface(
                            color = colors.goldPrimary,
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = "EXNESS CENT",
                                color = Color.Black,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Text(
                        text = "Balance: 1400 USC ($14 USD) • Risk: 100 USC ($1 USD)\nEntry: 4144.888 • Stop Loss: 4130.341 • Take Profit: 4220.000",
                        style = MaterialTheme.typography.bodySmall,
                        color = colors.textPrimary,
                        lineHeight = 18.sp
                    )

                    Button(
                        onClick = {
                            onLoadExnessCentScenario()
                            onDismiss()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("apply_preset_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colors.goldPrimary,
                            contentColor = Color.Black
                        ),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = strings.loadPresetBtn,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.labelLarge
                        )
                    }
                }
            }

            HorizontalDivider(color = colors.cardBorder.copy(alpha = 0.6f))

            // =========================================================================
            // 4. EXTRA FEATURE: LIVE USC ⇄ USD CURRENCY CONVERTER
            // =========================================================================
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                SettingsSectionHeader(
                    icon = Icons.Default.CurrencyExchange,
                    title = strings.currencyConverterTitle,
                    subtitle = "100 USC (Cent) = 1.00 USD (Standard)"
                )

                LiveUscUsdConverter()
            }

            HorizontalDivider(color = colors.cardBorder.copy(alpha = 0.6f))

            // =========================================================================
            // 5. EXTRA FEATURE: GOLD PIP PRECISION (2 vs 3 Decimals)
            // =========================================================================
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = strings.pipPrecisionTitle,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = colors.textPrimary
                    )
                    Text(
                        text = if (pipPrecision == 3) "3 Decimals (Micro-pips: 4144.888)" else "2 Decimals (Standard: 4144.88)",
                        style = MaterialTheme.typography.labelSmall,
                        color = colors.textTertiary
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Surface(
                        modifier = Modifier.clickable { onSelectPipPrecision(2) },
                        color = if (pipPrecision == 2) colors.goldPrimary else colors.cardBackground,
                        shape = RoundedCornerShape(6.dp),
                        border = BorderStroke(1.dp, if (pipPrecision == 2) colors.goldPrimary else colors.cardBorder)
                    ) {
                        Text(
                            text = ".00",
                            color = if (pipPrecision == 2) Color.Black else colors.textSecondary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }

                    Surface(
                        modifier = Modifier.clickable { onSelectPipPrecision(3) },
                        color = if (pipPrecision == 3) colors.goldPrimary else colors.cardBackground,
                        shape = RoundedCornerShape(6.dp),
                        border = BorderStroke(1.dp, if (pipPrecision == 3) colors.goldPrimary else colors.cardBorder)
                    ) {
                        Text(
                            text = ".000",
                            color = if (pipPrecision == 3) Color.Black else colors.textSecondary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        )
                    }
                }
            }

            // =========================================================================
            // 6. EXTRA FEATURE: HAPTIC FEEDBACK SWITCH
            // =========================================================================
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Vibration,
                        contentDescription = null,
                        tint = colors.textSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = strings.hapticFeedbackTitle,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = colors.textPrimary
                        )
                        Text(
                            text = "Tactile feedback when copying lots or applying presets",
                            style = MaterialTheme.typography.labelSmall,
                            color = colors.textTertiary
                        )
                    }
                }

                Switch(
                    checked = hapticEnabled,
                    onCheckedChange = onToggleHaptic,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.Black,
                        checkedTrackColor = colors.goldPrimary,
                        uncheckedThumbColor = colors.textTertiary,
                        uncheckedTrackColor = colors.surfaceVariant
                    )
                )
            }

            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}

@Composable
private fun SettingsSectionHeader(
    icon: ImageVector,
    title: String,
    subtitle: String
) {
    val colors = AppTheme.colors
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(colors.goldContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = colors.goldPrimary,
                modifier = Modifier.size(16.dp)
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = colors.textPrimary
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall,
                color = colors.textTertiary
            )
        }
    }
}

@Composable
private fun ThemeOptionCard(
    modifier: Modifier = Modifier,
    title: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    val colors = AppTheme.colors
    Surface(
        modifier = modifier
            .clickable { onClick() }
            .testTag(testTag),
        color = if (isSelected) colors.goldContainer else colors.cardBackground,
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(
            1.2.dp,
            if (isSelected) colors.goldPrimary else colors.cardBorder
        )
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = if (isSelected) colors.goldPrimary else colors.textSecondary,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) colors.goldLight else colors.textPrimary,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun LiveUscUsdConverter() {
    val colors = AppTheme.colors
    var uscInput by remember { mutableStateOf("1400") }
    var usdInput by remember { mutableStateOf("14.00") }

    Surface(
        color = colors.cardBackground,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, colors.cardBorder)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // USC Field
                OutlinedTextField(
                    value = uscInput,
                    onValueChange = { newVal ->
                        uscInput = newVal.filter { it.isDigit() || it == '.' }
                        val num = uscInput.toDoubleOrNull()
                        if (num != null) {
                            usdInput = String.format(Locale.US, "%.2f", num / 100.0)
                        }
                    },
                    label = { Text("USC (Cents)", fontSize = 11.sp) },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = colors.goldPrimary,
                        unfocusedBorderColor = colors.cardBorder,
                        focusedTextColor = colors.textPrimary,
                        unfocusedTextColor = colors.textPrimary
                    )
                )

                Text(
                    text = "⇄",
                    fontSize = 20.sp,
                    color = colors.goldPrimary,
                    fontWeight = FontWeight.Bold
                )

                // USD Field
                OutlinedTextField(
                    value = usdInput,
                    onValueChange = { newVal ->
                        usdInput = newVal.filter { it.isDigit() || it == '.' }
                        val num = usdInput.toDoubleOrNull()
                        if (num != null) {
                            uscInput = String.format(Locale.US, "%.0f", num * 100.0)
                        }
                    },
                    label = { Text("USD (Dollars)", fontSize = 11.sp) },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = colors.goldPrimary,
                        unfocusedBorderColor = colors.cardBorder,
                        focusedTextColor = colors.textPrimary,
                        unfocusedTextColor = colors.textPrimary
                    )
                )
            }

            Surface(
                color = colors.surfaceVariant,
                shape = RoundedCornerShape(6.dp)
            ) {
                Text(
                    text = "Quick tip: In Exness Cent accounts, 100 USC = $1.00 USD. 1400 USC balance = $14.00 USD equity.",
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.textSecondary,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                )
            }
        }
    }
}
