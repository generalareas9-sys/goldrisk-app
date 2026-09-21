package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.ThemeMode
import com.example.ui.CalculatorScreen
import com.example.ui.MainViewModel
import com.example.ui.theme.XauUsdCalculatorTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      val viewModel: MainViewModel = viewModel()
      val uiState by viewModel.uiState.collectAsState()
      val isSystemDark = isSystemInDarkTheme()
      val isDark = when (uiState.themeMode) {
        ThemeMode.DARK -> true
        ThemeMode.LIGHT -> false
        ThemeMode.SYSTEM -> isSystemDark
      }

      XauUsdCalculatorTheme(darkTheme = isDark) {
        CalculatorScreen(viewModel = viewModel)
      }
    }
  }
}

