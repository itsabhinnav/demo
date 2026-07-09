package com.test.design.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.Color

/**
 * Refined OEM palette: graphite surfaces with a single warm accent.
 * Designed for production IVI — restrained, high contrast, no neon clutter.
 */
val OemBrandColorScheme = ColorScheme(
    primary = Color(0xFFC9A96E),
    onPrimary = Color(0xFF1A150C),
    primaryContainer = Color(0xFF2E2618),
    onPrimaryContainer = Color(0xFFE8D9BC),
    inversePrimary = Color(0xFF8A7348),
    secondary = Color(0xFF7A8B9E),
    onSecondary = Color(0xFF0E1218),
    secondaryContainer = Color(0xFF252C36),
    onSecondaryContainer = Color(0xFFB8C4D0),
    tertiary = Color(0xFF6E8F78),
    onTertiary = Color(0xFF0C1810),
    tertiaryContainer = Color(0xFF1E2E24),
    onTertiaryContainer = Color(0xFFB8D4BE),
    background = CarBackgroundTokens.CanvasGray,
    onBackground = Color.White,
    surface = Color(0xFF22252A),
    onSurface = Color.White,
    surfaceVariant = Color(0xFF2A2F36),
    onSurfaceVariant = Color(0xFFE0E3E8),
    surfaceTint = Color(0xFFC9A96E),
    inverseSurface = Color(0xFFE4E6EA),
    inverseOnSurface = Color(0xFF1A1F26),
    error = Color(0xFFE07A6E),
    onError = Color(0xFF2A0A08),
    errorContainer = Color(0xFF4A1814),
    onErrorContainer = Color(0xFFF5D0CC),
    outline = Color(0xFF4A5058),
    outlineVariant = Color(0xFF383D44),
    scrim = Color(0xCC000000),
    surfaceBright = Color(0xFF32363D),
    surfaceDim = Color(0xFF16181C),
    surfaceContainer = Color(0xFF282B31),
    surfaceContainerHigh = Color(0xFF32363D),
    surfaceContainerHighest = Color(0xFF3C4149),
    surfaceContainerLow = Color(0xFF1E2126),
    surfaceContainerLowest = Color(0xFF16181C),
)
