package com.test.design.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import com.test.design.core.DrivingUxState
import com.test.design.core.LocalDrivingUxState
import com.test.design.core.motion.AppMotionScheme
import com.test.design.core.motion.LocalAppMotionScheme
import com.test.design.core.motion.LocalEffectiveMotionScheme
import com.test.design.core.motion.resolveMotionScheme

private val AppColorScheme = darkColorScheme(
    primary = Color(0xFF90CAF9),
    onPrimary = Color(0xFF0D2A40),
    primaryContainer = Color(0xFF2A4A62),
    onPrimaryContainer = Color(0xFFD4EAFF),
    secondary = Color(0xFFB0BEC5),
    onSecondary = Color(0xFF1A2228),
    secondaryContainer = Color(0xFF3A454D),
    onSecondaryContainer = Color(0xFFDDE4E8),
    tertiary = Color(0xFF80CBC4),
    onTertiary = Color(0xFF0A2E2A),
    tertiaryContainer = Color(0xFF2A4A46),
    onTertiaryContainer = Color(0xFFB8E8E2),
    background = CarBackgroundTokens.CanvasGray,
    onBackground = Color(0xFFE8EAED),
    surface = Color(0xFF22252A),
    onSurface = Color(0xFFE8EAED),
    surfaceVariant = Color(0xFF2E3238),
    onSurfaceVariant = Color(0xFFB8BDC6),
    surfaceContainer = Color(0xFF282B31),
    surfaceContainerHigh = Color(0xFF32363D),
    surfaceContainerHighest = Color(0xFF3C4149),
    surfaceContainerLow = Color(0xFF1E2126),
    surfaceContainerLowest = Color(0xFF16181C),
    outline = Color(0xFF4A5058),
    outlineVariant = Color(0xFF383D44),
)

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
