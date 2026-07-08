package com.test.design.core.motion

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class MotionSchemeViewModel(
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val stateKey = "app_motion_scheme"

    val motionScheme: StateFlow<AppMotionScheme> = savedStateHandle
        .getStateFlow(stateKey, AppMotionScheme.Expressive.name)
        .map(::parseScheme)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
            initialValue = parseScheme(savedStateHandle[stateKey]),
        )

    fun update(scheme: AppMotionScheme) {
        savedStateHandle[stateKey] = scheme.name
    }

    private fun parseScheme(raw: String?): AppMotionScheme =
        AppMotionScheme.entries.find { it.name == raw } ?: AppMotionScheme.Expressive
}

val LocalMotionSchemeUpdater = staticCompositionLocalOf<(AppMotionScheme) -> Unit> {
    error("MotionSchemeUpdater not provided")
}
