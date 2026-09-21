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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Functions
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Percent
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.SheetState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.AccentBlue
import com.example.ui.theme.BackgroundDark
import com.example.ui.theme.BuyGreen
import com.example.ui.theme.CardBackgroundDark
import com.example.ui.theme.CardBorderDark
import com.example.ui.theme.CardBorderGold
import com.example.ui.theme.GoldContainer
import com.example.ui.theme.GoldLight
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.GoldRiskSilver
import com.example.ui.theme.OnGoldContainer
import com.example.ui.theme.SellRed
import com.example.ui.theme.SurfaceDark
import com.example.ui.theme.SurfaceVariantDark
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary
import com.example.ui.theme.WarningAmber
import com.example.ui.theme.WarningAmberContainer

enum class InfoTab(val title: String, val icon: ImageVector) {
    DISCLAIMER("Disclaimer", Icons.Default.Warning),
    HOW_TO_USE("How to Use", Icons.Default.MenuBook),
    CALCULATIONS("Formulas", Icons.Default.Functions),
    ABOUT("About", Icons.Default.Info)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InfoGuideSheet(
    sheetState: SheetState,
    initialTab: InfoTab = InfoTab.DISCLAIMER,
    developerName: String = "Ousman Seid Ebrahim",
    onDismiss: () -> Unit
) {
    var selectedTab by remember(initialTab) { mutableIntStateOf(initialTab.ordinal) }
    val tabs = InfoTab.values()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = BackgroundDark,
        contentColor = TextPrimary,
        dragHandle = null,
        modifier = Modifier.testTag("info_guide_bottom_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.92f)
        ) {
            // Header Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(SurfaceDark)
                    .padding(horizontal = 20.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .border(1.dp, GoldPrimary, RoundedCornerShape(8.dp))
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.goldrisk_app_icon_1788801410920),
                            contentDescription = "GoldRisk",
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Gold",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Black,
                                color = GoldPrimary
                            )
                            Text(
                                text = "Risk",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Black,
                                color = GoldRiskSilver
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(GoldContainer)
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "v1.0.0",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = OnGoldContainer
                                )
                            }
                        }
                        Text(
                            text = "Disclaimer, Guide & Formulas",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.testTag("close_info_sheet_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = TextSecondary
                    )
                }
            }

            // Tabs Row
            ScrollableTabRow(
                selectedTabIndex = selectedTab,
                containerColor = SurfaceDark,
                contentColor = GoldPrimary,
                edgePadding = 16.dp,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = GoldPrimary,
                        height = 3.dp
                    )
                },
                divider = {
                    Divider(color = CardBorderDark, thickness = 1.dp)
                }
            ) {
                tabs.forEachIndexed { index, tab ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = tab.icon,
                                    contentDescription = null,
                                    modifier = Modifier.size(16.dp),
                                    tint = if (selectedTab == index) GoldLight else TextTertiary
                                )
                                Text(
                                    text = tab.title,
                                    fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Medium,
                                    color = if (selectedTab == index) GoldLight else TextSecondary,
                                    fontSize = 13.sp
                                )
                            }
                        },
                        modifier = Modifier.testTag("tab_${tab.name.lowercase()}")
                    )
                }
            }

            // Content Area based on selected Tab
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                when (selectedTab) {
                    InfoTab.DISCLAIMER.ordinal -> DisclaimerPage()
                    InfoTab.HOW_TO_USE.ordinal -> HowToUseGoldRiskPage()
                    InfoTab.CALCULATIONS.ordinal -> HowCalculationsWorkPage()
                    InfoTab.ABOUT.ordinal -> AboutGoldRiskPage(
                        developerName = developerName,
                        onOpenDisclaimer = { selectedTab = InfoTab.DISCLAIMER.ordinal }
                    )
                }
            }
        }
    }
}

// -----------------------------------------------------------------------------
// 1. HOW CALCULATIONS WORK PAGE
// -----------------------------------------------------------------------------
@Composable
private fun HowCalculationsWorkPage() {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(10.dp))
            FormulaHeaderCard(
                title = "Mathematical Engine Specifications",
                description = "All formulas exactly mirror the core calculation engine used in GoldRisk for XAUUSD spot gold precision trading."
            )
        }

        // 1. Stop-Loss Distance
        item {
            FormulaCard(
                title = "1. Stop-Loss Distance",
                formula = "Distance = |Entry Price - Stop Loss Price|",
                explanation = "Represents the absolute dollar distance per troy ounce between your entry and your stop loss.",
                buyExample = "BUY: Distance = Entry - Stop Loss (e.g., 4424.287 - 4421.214 = 3.073)",
                sellExample = "SELL: Distance = Stop Loss - Entry (e.g., 4427.360 - 4424.287 = 3.073)",
                accentColor = GoldPrimary
            )
        }

        // 2. Risk Amount
        item {
            FormulaCard(
                title = "2. Risk Amount",
                formula = "Fixed Amount (USC / USD)  OR  Balance × (Risk % ÷ 100)",
                explanation = "The maximum monetary loss you are willing to accept on the trade if the stop loss is triggered.\n\n" +
                        "Dual Currency Conversion (Cent Accounts):\n" +
                        "• 100 USC = 1.00 USD\n" +
                        "• Risk (USD) = Risk Amount (USC) ÷ 100",
                example = "Example: 100 USC risk = $1.00 USD (on a 1,220.3 USC / $12.20 USD balance)",
                accentColor = AccentBlue
            )
        }

        // 3. Exact Lot Size
        item {
            FormulaCard(
                title = "3. Exact Lot Size",
                formula = "Lot Size = Risk Amount (USD) ÷ (Stop-Loss Distance × Contract Size)",
                explanation = "XAUUSD is quoted in USD per ounce. In Exness Standard Cent accounts, the Contract Size is 1 oz per lot (compared to 100 oz in Standard accounts).\n\n" +
                        "By taking Risk in USD and dividing by (Distance × Contract Size), the position size is calculated directly in LOTS without artificial multipliers.",
                example = "Example:\n" +
                        "• Risk: 100 USC = $1.00 USD\n" +
                        "• Stop-Loss Distance: 3.073\n" +
                        "• Contract Size: 1\n" +
                        "Lot Size = 1.00 ÷ (3.073 × 1) = 0.3254 lots",
                accentColor = BuyGreen
            )
        }

        // 4. Recommended Lot Size
        item {
            FormulaCard(
                title = "4. Recommended Lot Size",
                formula = "Recommended Lot = floor(Exact Lot Size ÷ Lot Step) × Lot Step",
                explanation = "Brokers require orders to adhere to specific lot steps (typically 0.01 lots). To strictly preserve your risk ceiling, GoldRisk ALWAYS rounds DOWN to the nearest valid step. It NEVER rounds up, ensuring your actual maximum risk is never exceeded.",
                example = "Example: Exact 0.3254 lots with 0.01 step → 0.32 LOT\n(Actual risk = 0.32 × 3.073 × 1 = $0.983 USD / 98.3 USC ≤ 100 USC)",
                accentColor = GoldLight
            )
        }

        // 5. Risk-to-Reward Ratio
        item {
            FormulaCard(
                title = "5. Risk-to-Reward Ratio (R:R)",
                formula = "R:R = Take Profit Distance ÷ Stop Loss Distance\nWhere TP Distance = |Take Profit - Entry|",
                explanation = "Quantifies potential upside relative to downside risk. Displayed as 1 : R:R. If Take Profit is not entered, R:R shows 'TP not provided'.",
                example = "Example:\n" +
                        "• Entry: 4424.287, SL: 4421.214 (SL Distance = 3.073)\n" +
                        "• TP: 4433.506 (TP Distance = 9.219)\n" +
                        "• R:R = 9.219 ÷ 3.073 = 3.00 → 1 : 3.00",
                accentColor = GoldPrimary
            )
        }

        // 6. Potential Profit
        item {
            FormulaCard(
                title = "6. Potential Profit",
                formula = "Potential Profit = Risk Amount × Risk-to-Reward Ratio",
                explanation = "Calculates the total monetary gain in both account currency (USC/USD) and equivalent USD if the target price is achieved.",
                example = "Example:\n" +
                        "• Risk: 100 USC ($1.00 USD)\n" +
                        "• R:R: 1 : 3.00\n" +
                        "• Potential Profit = 100 USC × 3.00 = 300 USC (≈ $3.00 USD)",
                accentColor = BuyGreen
            )
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// -----------------------------------------------------------------------------
// 2. HOW TO USE GOLDRISK PAGE
// -----------------------------------------------------------------------------
@Composable
private fun HowToUseGoldRiskPage() {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(10.dp))
            FormulaHeaderCard(
                title = "Fast 6-Step Execution Guide",
                description = "Follow these steps to calculate and execute disciplined, mathematically precise XAUUSD position sizes."
            )
        }

        item {
            StepGuideCard(
                stepNumber = "1",
                icon = Icons.Default.AccountBalance,
                title = "Select Account",
                description = "Choose your trading account profile from the top selector. GoldRisk comes pre-configured for Exness Standard Cent (XAUUSDc, Contract Size 1, USC currency) and Exness Standard (XAUUSDm, Contract Size 100, USD currency). Tap the manage button to edit or add custom accounts.",
                accentColor = GoldPrimary
            )
        }

        item {
            StepGuideCard(
                stepNumber = "2",
                icon = Icons.Default.Percent,
                title = "Select Risk",
                description = "Choose whether you want to risk a Fixed Amount (e.g., 100 USC or $1.00 USD) or a Percentage (e.g., 1% or 2%) of your current account balance. Use the quick preset chips for one-tap selection.",
                accentColor = AccentBlue
            )
        }

        item {
            StepGuideCard(
                stepNumber = "3",
                icon = Icons.Default.SwapVert,
                title = "Choose BUY or SELL",
                description = "Select BUY / LONG (green) or SELL / SHORT (red). The interface immediately adjusts color cues and enforces directional price validation (e.g., in a BUY trade, Stop Loss must be below Entry).",
                accentColor = BuyGreen
            )
        }

        item {
            StepGuideCard(
                stepNumber = "4",
                icon = Icons.Default.Edit,
                title = "Enter Entry, Stop Loss, and optional Take Profit",
                description = "Type your planned entry price and stop loss price into the high-precision decimal fields. You may optionally enter a Take Profit target to calculate your Risk-to-Reward ratio and potential profit.",
                accentColor = GoldLight
            )
        }

        item {
            StepGuideCard(
                stepNumber = "5",
                icon = Icons.Default.Calculate,
                title = "Calculate Lot Size",
                description = "GoldRisk calculates instantly in real time as you type! The Recommended Lot Size hero card displays the exact executable lot size rounded down to your broker's step, alongside Stop Loss Distance, Risk Amount, R:R, and Potential Profit.",
                accentColor = GoldPrimary
            )
        }

        item {
            StepGuideCard(
                stepNumber = "6",
                icon = Icons.Default.ContentCopy,
                title = "Copy the Recommended Lot and execute the trade",
                description = "Tap the 'Copy' button in the hero card to copy the calculated lot size to your clipboard. Paste directly into MetaTrader 4, MetaTrader 5, or the Exness Trade app to execute your order with zero math errors.",
                accentColor = BuyGreen
            )
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// -----------------------------------------------------------------------------
// 3. ABOUT GOLDRISK PAGE
// -----------------------------------------------------------------------------
@Composable
private fun AboutGoldRiskPage(
    developerName: String = "Ousman Seid Ebrahim",
    onOpenDisclaimer: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(10.dp))
            // App Branding Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = CardBackgroundDark),
                border = BorderStroke(1.dp, CardBorderGold)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.goldrisk_logo_banner_1788801424769),
                        contentDescription = "GoldRisk Official Logo",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Fit
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Fast, accurate, and safe XAUUSD lot-size calculation with conservative risk management.",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = TextSecondary,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(GoldContainer)
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "Release v1.0.0",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = OnGoldContainer
                        )
                    }
                }
            }
        }

        // Specification Details Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = CardBackgroundDark),
                border = BorderStroke(1.dp, CardBorderDark)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "APPLICATION SPECIFICATIONS",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = GoldLight,
                        letterSpacing = 1.sp
                    )

                    AboutSpecRow(label = "App Name", value = "GoldRisk")
                    AboutSpecRow(
                        label = "Purpose",
                        value = "Fast, accurate, and safe XAUUSD lot-size calculation with conservative risk management."
                    )
                    AboutSpecRow(label = "Current Version", value = "v1.0.0")
                    AboutSpecRow(label = "Platform", value = "Android & iOS")
                    AboutSpecRow(
                        label = "Developer",
                        value = developerName
                    )
                }
            }
        }

        // Professional Version Numbering Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = CardBackgroundDark),
                border = BorderStroke(1.dp, CardBorderDark)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "PROFESSIONAL VERSION NUMBERING",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = GoldLight,
                        letterSpacing = 1.sp
                    )

                    KeyFeatureItem("v1.0.0", "First complete official release.")
                    KeyFeatureItem("v1.0.1", "Bug fixes and minor corrections.")
                    KeyFeatureItem("v1.1.0", "New features and improvements.")
                    KeyFeatureItem("v2.0.0", "Major update or redesign.")
                }
            }
        }

        // Core Capabilities Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = CardBackgroundDark),
                border = BorderStroke(1.dp, CardBorderDark)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "KEY CAPABILITIES",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = GoldLight,
                        letterSpacing = 1.sp
                    )

                    KeyFeatureItem("Direct Lot Size Calculation", "Computes lots directly for Exness Standard Cent (XAUUSDc) without multiplying by 100 or using confusing labels.")
                    KeyFeatureItem("Conservative Round-Down", "Always truncates to broker lot step so you never accidentally risk more capital than defined.")
                    KeyFeatureItem("Dual Currency Support", "Real-time bidirectional USC and USD conversion (100 USC = 1 USD).")
                    KeyFeatureItem("Risk-to-Reward Intelligence", "Automatic R:R evaluation and potential profit projection.")
                }
            }
        }

        // Full Disclaimer Text Card on About Page (Requirement 7)
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = WarningAmberContainer),
                border = BorderStroke(1.5.dp, WarningAmber)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Warning",
                            tint = WarningAmber,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Important Financial Disclaimer",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Black,
                            color = WarningAmber
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "GoldRisk is a calculation tool designed to assist with position sizing and risk management. Trading involves financial risk. Users are responsible for verifying all calculations and broker specifications before placing a trade. GoldRisk does not provide financial advice or trading signals.",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary,
                        lineHeight = 22.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    TextButton(
                        onClick = onOpenDisclaimer,
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text(
                            text = "View Full Risk Acknowledgments →",
                            color = GoldLight,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// -----------------------------------------------------------------------------
// 4. DISCLAIMER PAGE
// -----------------------------------------------------------------------------
@Composable
private fun DisclaimerPage() {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(10.dp))
            // Primary Warning Box
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = WarningAmberContainer),
                border = BorderStroke(1.5.dp, WarningAmber)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Warning",
                            tint = WarningAmber,
                            modifier = Modifier.size(26.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = "Important Disclaimer",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Black,
                            color = WarningAmber
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "GoldRisk is a calculation tool designed to assist with position sizing and risk management. Trading involves financial risk. Users are responsible for verifying all calculations and broker specifications before placing a trade. GoldRisk does not provide financial advice or trading signals.",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary,
                        lineHeight = 22.sp
                    )
                }
            }
        }

        // Detailed Legal & Risk Clauses
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = CardBackgroundDark),
                border = BorderStroke(1.dp, CardBorderDark)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Text(
                        text = "RISK ACKNOWLEDGMENTS & POLICIES",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = GoldLight,
                        letterSpacing = 1.sp
                    )

                    DisclaimerBullet(
                        icon = Icons.Default.Shield,
                        title = "Calculation & Assistance Tool Only",
                        body = "GoldRisk is strictly a mathematical utility designed to calculate lot sizes based on parameters entered by the user. It is not an automated trading robot, expert advisor, or signal service."
                    )

                    DisclaimerBullet(
                        icon = Icons.Default.Info,
                        title = "No Financial Advice",
                        body = "Nothing contained within this application constitutes financial, investment, legal, or tax advice. Past market behavior does not guarantee future results."
                    )

                    DisclaimerBullet(
                        icon = Icons.Default.SwapVert,
                        title = "User Responsibility & Broker Specs",
                        body = "Brokers may change contract sizes, leverage rules, lot step requirements, spreads, or swap rates without prior notice. Users must independently verify all broker specifications before submitting trades."
                    )

                    DisclaimerBullet(
                        icon = Icons.Default.Warning,
                        title = "Leverage & Capital Risk",
                        body = "Foreign exchange and precious metals (XAUUSD) trading on margin carries a high level of risk and may not be suitable for all investors. High leverage can work against you as well as for you."
                    )
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceVariantDark),
                border = BorderStroke(1.dp, CardBorderDark)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "GoldRisk v1.0.0",
                        fontWeight = FontWeight.Bold,
                        color = GoldPrimary,
                        fontSize = 13.sp
                    )
                    Text(
                        text = "Crafted for disciplined traders by Ousman Seid Ebrahim",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// -----------------------------------------------------------------------------
// REUSABLE HELPER COMPONENTS
// -----------------------------------------------------------------------------

@Composable
private fun FormulaHeaderCard(title: String, description: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceVariantDark),
        border = BorderStroke(1.dp, CardBorderGold)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = GoldLight
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                lineHeight = 18.sp
            )
        }
    }
}

@Composable
private fun FormulaCard(
    title: String,
    formula: String,
    explanation: String,
    example: String? = null,
    buyExample: String? = null,
    sellExample: String? = null,
    accentColor: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackgroundDark),
        border = BorderStroke(1.dp, CardBorderDark)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            // Formula Code Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(SurfaceDark)
                    .border(1.dp, accentColor.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 12.dp, vertical = 10.dp)
            ) {
                Text(
                    text = formula,
                    style = MaterialTheme.typography.bodyMedium,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    color = accentColor,
                    fontSize = 13.sp
                )
            }

            Text(
                text = explanation,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                lineHeight = 18.sp
            )

            if (buyExample != null && sellExample != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(SurfaceVariantDark)
                        .padding(10.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = buyExample,
                            style = MaterialTheme.typography.labelSmall,
                            color = BuyGreen,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = sellExample,
                            style = MaterialTheme.typography.labelSmall,
                            color = SellRed,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            } else if (example != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(6.dp))
                        .background(SurfaceVariantDark)
                        .padding(10.dp)
                ) {
                    Text(
                        text = example,
                        style = MaterialTheme.typography.labelSmall,
                        color = GoldLight,
                        fontFamily = FontFamily.Monospace,
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun StepGuideCard(
    stepNumber: String,
    icon: ImageVector,
    title: String,
    description: String,
    accentColor: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackgroundDark),
        border = BorderStroke(1.dp, CardBorderDark)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Step badge with Icon
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(accentColor.copy(alpha = 0.15f))
                    .border(1.5.dp, accentColor, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stepNumber,
                    fontWeight = FontWeight.Black,
                    fontSize = 16.sp,
                    color = accentColor
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = accentColor
                    )
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    lineHeight = 18.sp
                )
            }
        }
    }
}

@Composable
private fun AboutSpecRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary
        )
    }
}

@Composable
private fun KeyFeatureItem(title: String, description: String) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(GoldPrimary)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        }
        Text(
            text = description,
            style = MaterialTheme.typography.labelSmall,
            color = TextSecondary,
            modifier = Modifier.padding(start = 14.dp),
            lineHeight = 15.sp
        )
    }
}

@Composable
private fun DisclaimerBullet(icon: ImageVector, title: String, body: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = WarningAmber,
            modifier = Modifier
                .size(18.dp)
                .padding(top = 2.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = body,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                lineHeight = 17.sp
            )
        }
    }
}
