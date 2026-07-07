package com.test.design.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MotionScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.test.design.core.DrivingUxState
import com.test.design.core.LocalDrivingUxState

private val AppColorScheme = darkColorScheme()

@Composable
fun AppTheme(
    drivingUxState: DrivingUxState = DrivingUxState.Parked,
    content: @Composable () -> Unit,
) {
    val motionScheme = when (drivingUxState) {
        DrivingUxState.Parked -> MotionScheme.expressive()
        DrivingUxState.Driving,
        DrivingUxState.Restricted,
        -> MotionScheme.standard()
    }

    CompositionLocalProvider(LocalDrivingUxState provides drivingUxState) {
        MaterialTheme(
            colorScheme = AppColorScheme,
            motionScheme = motionScheme,
            content = content,
        )
    }
}
