package com.test.design.core.theme

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class ThemeModeViewModel(
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val stateKey = "app_theme_mode"

    val themeMode: StateFlow<AppThemeMode> = savedStateHandle
        .getStateFlow(stateKey, AppThemeMode.System.name)
        .map(::parseMode)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
            initialValue = parseMode(savedStateHandle[stateKey]),
        )

    fun update(mode: AppThemeMode) {
        savedStateHandle[stateKey] = mode.name
    }

    private fun parseMode(raw: String?): AppThemeMode =
        AppThemeMode.entries.find { it.name == raw } ?: AppThemeMode.System
}

val LocalThemeModeUpdater = staticCompositionLocalOf<(AppThemeMode) -> Unit> {
    error("ThemeModeUpdater not provided")
}
