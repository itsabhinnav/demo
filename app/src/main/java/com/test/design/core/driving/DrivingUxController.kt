package com.test.design.core.driving

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import com.test.design.component.core.DrivingUxState

class DrivingUxController(initialState: DrivingUxState = DrivingUxState.Parked) {
    var state by mutableStateOf(initialState)
        private set

    fun update(newState: DrivingUxState) {
        state = newState
    }
}

val LocalDrivingUxController = staticCompositionLocalOf<DrivingUxController> {
    error("DrivingUxController not provided")
}

@Composable
fun rememberDrivingUxController(initialState: DrivingUxState = DrivingUxState.Parked): DrivingUxController =
    remember { DrivingUxController(initialState) }
