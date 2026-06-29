package com.test.design.component.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val NissanDarkColorScheme = darkColorScheme(
    primary = NissanRed,
    onPrimary = NissanOnPrimary,
    primaryContainer = NissanPrimaryContainer,
    onPrimaryContainer = NissanOnPrimaryContainer,
    secondary = NissanCarAccent,
    onSecondary = NissanBackground,
    background = NissanBackground,
    onBackground = NissanOnSurface,
    surface = NissanSurface,
    onSurface = NissanOnSurface,
    surfaceVariant = NissanSurfaceVariant,
    onSurfaceVariant = NissanOnSurfaceVariant,
    outline = NissanOutline,
    error = NissanError,
    tertiary = NissanCarAccent,
)

@Composable
fun NissanTheme(
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = NissanDarkColorScheme,
        typography = NissanTypography,
        shapes = NissanShapes,
        content = content,
    )
}
