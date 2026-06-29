package com.test.design.component.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.test.design.component.core.DrivingUxState
import com.test.design.component.core.LocalDrivingUxState

private val OemDarkColorScheme = darkColorScheme(
    primary = OemPrimary,
    onPrimary = OemOnPrimary,
    primaryContainer = OemPrimaryContainer,
    onPrimaryContainer = OemOnPrimaryContainer,
    secondary = OemGrayLight,
    onSecondary = OemBlack,
    background = OemBackground,
    onBackground = OemOnSurface,
    surface = OemSurface,
    onSurface = OemOnSurface,
    surfaceVariant = OemSurfaceVariant,
    surfaceContainer = OemSurfaceElevated,
    surfaceContainerHigh = OemSurfaceVariant,
    onSurfaceVariant = OemOnSurfaceVariant,
    outline = OemOutline,
    error = OemError,
    onError = OemBlack,
    tertiary = OemGray,
)

@Composable
fun OemTheme(
    drivingUxState: DrivingUxState = DrivingUxState.Parked,
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(LocalDrivingUxState provides drivingUxState) {
        MaterialTheme(
            colorScheme = OemDarkColorScheme,
            typography = OemTypography,
            shapes = OemShapes,
            content = content,
        )
    }
}
