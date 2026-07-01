package com.test.design.core.driving

import androidx.compose.runtime.staticCompositionLocalOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.test.design.component.core.DrivingUxState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class DrivingUxViewModel(
    private val savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val stateKey = "driving_ux_state"

    val drivingUxState: StateFlow<DrivingUxState> = savedStateHandle
        .getStateFlow(stateKey, DrivingUxState.Parked.name)
        .map(::parseState)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(stopTimeoutMillis = 5_000),
            initialValue = parseState(savedStateHandle[stateKey]),
        )

    fun update(newState: DrivingUxState) {
        savedStateHandle[stateKey] = newState.name
    }

    private fun parseState(raw: String?): DrivingUxState =
        DrivingUxState.entries.find { it.name == raw } ?: DrivingUxState.Parked
}

val LocalDrivingUxUpdater = staticCompositionLocalOf<(DrivingUxState) -> Unit> {
    error("DrivingUxUpdater not provided")
}
