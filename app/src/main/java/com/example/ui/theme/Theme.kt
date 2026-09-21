package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = GoldPrimary,
    onPrimary = Color.Black,
    primaryContainer = GoldContainer,
    onPrimaryContainer = OnGoldContainer,
    secondary = AccentBlue,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF0C2B45),
    onSecondaryContainer = Color(0xFFBAE6FD),
    tertiary = BuyGreen,
    onTertiary = Color.Black,
    background = BackgroundDark,
    onBackground = TextPrimary,
    surface = SurfaceDark,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = TextSecondary,
    outline = CardBorderDark,
    error = SellRed,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFFB48200),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFECC0),
    onPrimaryContainer = Color(0xFF332500),
    secondary = Color(0xFF0284C7),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE0F2FE),
    onSecondaryContainer = Color(0xFF0369A1),
    tertiary = Color(0xFF059669),
    onTertiary = Color.White,
    background = Color(0xFFF8FAFC),
    onBackground = Color(0xFF0F172A),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF0F172A),
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = Color(0xFF475569),
    outline = Color(0xFFCBD5E1),
    error = Color(0xFFDC2626),
    onError = Color.White
)

data class AppThemeColors(
    val background: Color,
    val surface: Color,
    val surfaceVariant: Color,
    val cardBackground: Color,
    val cardBorder: Color,
    val cardBorderGold: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textTertiary: Color,
    val goldPrimary: Color,
    val goldLight: Color,
    val goldContainer: Color,
    val buyGreen: Color,
    val buyGreenContainer: Color,
    val sellRed: Color,
    val sellRedContainer: Color,
    val warningAmber: Color,
    val accentBlue: Color,
    val isDark: Boolean
)

val DarkAppThemeColors = AppThemeColors(
    background = Color(0xFF080A0E),
    surface = Color(0xFF0E1218),
    surfaceVariant = Color(0xFF151B24),
    cardBackground = Color(0xFF121720),
    cardBorder = Color(0xFF222B3A),
    cardBorderGold = Color(0x44D4AF37),
    textPrimary = Color(0xFFF8FAFC),
    textSecondary = Color(0xFF94A3B8),
    textTertiary = Color(0xFF64748B),
    goldPrimary = Color(0xFFD4AF37),
    goldLight = Color(0xFFF3CE65),
    goldContainer = Color(0xFF2A200B),
    buyGreen = Color(0xFF00E676),
    buyGreenContainer = Color(0xFF072614),
    sellRed = Color(0xFFFF3D57),
    sellRedContainer = Color(0xFF2F0B10),
    warningAmber = Color(0xFFFFAB00),
    accentBlue = Color(0xFF38BDF8),
    isDark = true
)

val LightAppThemeColors = AppThemeColors(
    background = Color(0xFFF1F5F9),
    surface = Color(0xFFFFFFFF),
    surfaceVariant = Color(0xFFE2E8F0),
    cardBackground = Color(0xFFFFFFFF),
    cardBorder = Color(0xFFCBD5E1),
    cardBorderGold = Color(0x66B48200),
    textPrimary = Color(0xFF0F172A),
    textSecondary = Color(0xFF475569),
    textTertiary = Color(0xFF64748B),
    goldPrimary = Color(0xFFB48200),
    goldLight = Color(0xFFD4AF37),
    goldContainer = Color(0xFFFFF7DF),
    buyGreen = Color(0xFF059669),
    buyGreenContainer = Color(0xFFE6F7F0),
    sellRed = Color(0xFFDC2626),
    sellRedContainer = Color(0xFFFDE8E8),
    warningAmber = Color(0xFFD97706),
    accentBlue = Color(0xFF0284C7),
    isDark = false
)

val LocalAppColors = androidx.compose.runtime.staticCompositionLocalOf {
    DarkAppThemeColors
}

object AppTheme {
    val colors: AppThemeColors
        @Composable
        get() = LocalAppColors.current
}

@Composable
fun XauUsdCalculatorTheme(
    darkTheme: Boolean = true, // Default to sleek dark trading terminal
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val appThemeColors = if (darkTheme) DarkAppThemeColors else LightAppThemeColors

    androidx.compose.runtime.CompositionLocalProvider(
        LocalAppColors provides appThemeColors
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
