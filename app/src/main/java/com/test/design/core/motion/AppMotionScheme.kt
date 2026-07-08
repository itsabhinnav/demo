package com.test.design.core.motion

import androidx.compose.runtime.compositionLocalOf
import com.test.design.core.DrivingUxState

enum class AppMotionScheme(val label: String) {
    Standard("Standard"),
    Expressive("Expressive"),
    Custom("Custom"),
}

val LocalAppMotionScheme = compositionLocalOf { AppMotionScheme.Expressive }

val LocalEffectiveMotionScheme = compositionLocalOf { AppMotionScheme.Expressive }

fun resolveMotionScheme(
    drivingUxState: DrivingUxState,
    selected: AppMotionScheme,
): AppMotionScheme = when (drivingUxState) {
    DrivingUxState.Parked -> selected
    DrivingUxState.Driving,
    DrivingUxState.Restricted,
    -> AppMotionScheme.Standard
}
