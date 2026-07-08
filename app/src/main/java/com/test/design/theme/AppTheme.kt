package com.test.design.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.test.design.core.DrivingUxState
import com.test.design.core.LocalDrivingUxState
import com.test.design.core.motion.AppMotionScheme
import com.test.design.core.motion.LocalAppMotionScheme
import com.test.design.core.motion.LocalEffectiveMotionScheme
import com.test.design.core.motion.resolveMotionScheme

private val AppColorScheme = darkColorScheme()

@Composable
fun AppTheme(
    drivingUxState: DrivingUxState = DrivingUxState.Parked,
    appMotionScheme: AppMotionScheme = AppMotionScheme.Expressive,
    content: @Composable () -> Unit,
) {
    val effectiveScheme = resolveMotionScheme(drivingUxState, appMotionScheme)
    val motionScheme = effectiveScheme.toMotionScheme()

    CompositionLocalProvider(
        LocalDrivingUxState provides drivingUxState,
        LocalAppMotionScheme provides appMotionScheme,
        LocalEffectiveMotionScheme provides effectiveScheme,
    ) {
        MaterialTheme(
            colorScheme = AppColorScheme,
            typography = CarTypography,
            motionScheme = motionScheme,
            content = content,
        )
    }
}
