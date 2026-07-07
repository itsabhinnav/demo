package com.test.design.core

import androidx.compose.runtime.compositionLocalOf

enum class DrivingUxState {
    Parked,
    Driving,
    Restricted,
}

val LocalDrivingUxState = compositionLocalOf { DrivingUxState.Parked }
