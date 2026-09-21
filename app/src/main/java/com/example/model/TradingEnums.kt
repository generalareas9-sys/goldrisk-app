package com.example.model

enum class PositionType {
    BUY,
    SELL
}

enum class AccountCurrency(val label: String, val symbol: String) {
    USD("USD", "$"),
    USC("USC", "¢")
}

enum class RiskMode(val label: String) {
    PERCENTAGE("Percentage (%)"),
    FIXED_AMOUNT("Fixed Amount")
}

enum class ThemeMode(val label: String) {
    DARK("Dark Terminal"),
    LIGHT("Light Mode"),
    SYSTEM("System Default")
}

enum class AppLanguage(
    val code: String,
    val displayName: String,
    val nativeName: String,
    val flag: String
) {
    ENGLISH("en", "English", "English", "🇺🇸"),
    SPANISH("es", "Spanish", "Español", "🇪🇸"),
    FRENCH("fr", "French", "Français", "🇫🇷"),
    GERMAN("de", "German", "Deutsch", "🇩🇪"),
    INDONESIAN("id", "Indonesian", "Bahasa Indonesia", "🇮🇩"),
    ARABIC("ar", "Arabic", "العربية", "🇸🇦"),
    RUSSIAN("ru", "Russian", "Русский", "🇷🇺")
}
