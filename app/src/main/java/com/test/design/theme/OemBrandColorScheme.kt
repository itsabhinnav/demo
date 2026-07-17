package com.test.design.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

/**
 * Refined OEM palette: graphite surfaces with a single warm accent.
 * Designed for production IVI — restrained, high contrast, no neon clutter.
 */
val OemBrandDarkColorScheme = darkColorScheme(
    primary = Color(0xFFC9A96E),
    onPrimary = Color(0xFF1A150C),
    primaryContainer = Color(0xFF2E2618),
    onPrimaryContainer = Color(0xFFE8D9BC),
    secondary = Color(0xFF7A8B9E),
    onSecondary = Color(0xFF0E1218),
    secondaryContainer = Color(0xFF252C36),
    onSecondaryContainer = Color(0xFFB8C4D0),
    tertiary = Color(0xFF6E8F78),
    onTertiary = Color(0xFF0C1810),
    tertiaryContainer = Color(0xFF1E2E24),
    onTertiaryContainer = Color(0xFFB8D4BE),
    background = CarBackgroundTokens.CanvasDark,
    onBackground = Color.White,
    surface = Color(0xFF22252A),
    onSurface = Color.White,
    surfaceVariant = Color(0xFF2A2F36),
    onSurfaceVariant = Color(0xFFE0E3E8),
    surfaceContainer = Color(0xFF282B31),
    surfaceContainerHigh = Color(0xFF32363D),
    surfaceContainerHighest = Color(0xFF3C4149),
    surfaceContainerLow = Color(0xFF1E2126),
    surfaceContainerLowest = Color(0xFF16181C),
    outline = Color(0xFF4A5058),
    outlineVariant = Color(0xFF383D44),
)

val OemBrandLightColorScheme = lightColorScheme(
    primary = Color(0xFF8A7348),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE8D9BC),
    onPrimaryContainer = Color(0xFF1A150C),
    secondary = Color(0xFF546E7A),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFD5DEE6),
    onSecondaryContainer = Color(0xFF0E1218),
    tertiary = Color(0xFF4A6B54),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFD0E4D6),
    onTertiaryContainer = Color(0xFF0C1810),
    background = CarBackgroundTokens.CanvasLight,
    onBackground = Color(0xFF1A1C1E),
    surface = Color(0xFFF4F5F7),
    onSurface = Color(0xFF1A1C1E),
    surfaceVariant = Color(0xFFE2E5EA),
    onSurfaceVariant = Color(0xFF42474E),
    surfaceContainer = Color(0xFFEEF0F3),
    surfaceContainerHigh = Color(0xFFE8EAEE),
    surfaceContainerHighest = Color(0xFFE2E5EA),
    surfaceContainerLow = Color(0xFFF7F8FA),
    surfaceContainerLowest = Color.White,
    outline = Color(0xFF72787F),
    outlineVariant = Color(0xFFC2C7CE),
)

@Deprecated("Use OemBrandDarkColorScheme", ReplaceWith("OemBrandDarkColorScheme"))
val OemBrandColorScheme = OemBrandDarkColorScheme
