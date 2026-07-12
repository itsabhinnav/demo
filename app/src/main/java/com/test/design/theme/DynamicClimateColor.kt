package com.test.design.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp

private val CoolPrimary = Color(0xFF4FC3F7)
private val CoolSecondary = Color(0xFF0288D1)
private val CoolTertiary = Color(0xFF80DEEA)
private val CoolSurface = Color(0xFF0D1B2A)
private val CoolContainer = Color(0xFF1B3A4B)

private val WarmPrimary = Color(0xFFFF7043)
private val WarmSecondary = Color(0xFFE64A19)
private val WarmTertiary = Color(0xFFFFAB91)
private val WarmSurface = Color(0xFF1A0A08)
private val WarmContainer = Color(0xFF4A1C12)

/**
 * Interpolates an expressive color scheme between cool (blue) and warm (red) hues
 * based on normalized temperature [0f, 1f].
 */
fun climateColorScheme(temperatureFraction: Float): ColorScheme {
    val fraction = temperatureFraction.coerceIn(0f, 1f)
    return ColorScheme(
        primary = lerp(CoolPrimary, WarmPrimary, fraction),
        onPrimary = Color.White,
        primaryContainer = lerp(CoolContainer, WarmContainer, fraction),
        onPrimaryContainer = lerp(Color(0xFFB3E5FC), Color(0xFFFFCCBC), fraction),
        inversePrimary = lerp(CoolSecondary, WarmSecondary, fraction),
        secondary = lerp(CoolSecondary, WarmSecondary, fraction),
        onSecondary = Color.White,
        secondaryContainer = lerp(Color(0xFF01579B), Color(0xFFBF360C), fraction),
        onSecondaryContainer = lerp(Color(0xFFB3E5FC), Color(0xFFFFCCBC), fraction),
        tertiary = lerp(CoolTertiary, WarmTertiary, fraction),
        onTertiary = Color(0xFF00363D),
        tertiaryContainer = lerp(Color(0xFF004D5A), Color(0xFF5D2108), fraction),
        onTertiaryContainer = lerp(Color(0xFFB2EBF2), Color(0xFFFFCCBC), fraction),
        background = CarBackgroundTokens.CanvasGray,
        onBackground = Color.White,
        surface = CarBackgroundTokens.CanvasGray,
        onSurface = Color.White,
        surfaceVariant = lerp(Color(0xFF1E3A4F), Color(0xFF3E1A12), fraction),
        onSurfaceVariant = Color(0xFFE0E3E8),
        surfaceTint = lerp(CoolPrimary, WarmPrimary, fraction),
        inverseSurface = Color(0xFFE8EAF0),
        inverseOnSurface = Color(0xFF1A1C1E),
        error = Color(0xFFFFB4AB),
        onError = Color(0xFF690005),
        errorContainer = Color(0xFF93000A),
        onErrorContainer = Color(0xFFFFDAD6),
        outline = lerp(Color(0xFF4A6A7A), Color(0xFF8A4A3A), fraction),
        outlineVariant = lerp(Color(0xFF2A4A5A), Color(0xFF5A2A1A), fraction),
        scrim = Color.Black,
        surfaceBright = lerp(Color(0xFF2A4A5A), Color(0xFF5A2A1A), fraction),
        surfaceDim = lerp(Color(0xFF0A1520), Color(0xFF150808), fraction),
        surfaceContainer = lerp(Color(0xFF152535), Color(0xFF2A1010), fraction),
        surfaceContainerHigh = lerp(Color(0xFF1E3040), Color(0xFF351818), fraction),
        surfaceContainerHighest = lerp(Color(0xFF283A4A), Color(0xFF402020), fraction),
        surfaceContainerLow = lerp(Color(0xFF101E2A), Color(0xFF1E0C0C), fraction),
        surfaceContainerLowest = lerp(Color(0xFF080F18), Color(0xFF100606), fraction),
    )
}

fun temperatureToFraction(celsius: Int, min: Int = 16, max: Int = 30): Float =
    ((celsius - min).toFloat() / (max - min).toFloat()).coerceIn(0f, 1f)

/** Ambient glow / pill fill for a single zone temperature. */
fun climateAmbientColor(temperatureFraction: Float): Color =
    lerp(CoolPrimary, WarmPrimary, temperatureFraction.coerceIn(0f, 1f))

fun climatePillContainer(temperatureFraction: Float): Color =
    lerp(CoolContainer, WarmContainer, temperatureFraction.coerceIn(0f, 1f))

fun climateOnPill(temperatureFraction: Float): Color =
    lerp(Color(0xFFB3E5FC), Color(0xFFFFCCBC), temperatureFraction.coerceIn(0f, 1f))

/** Font weight thickens as temperature rises (cool = light, warm = bold). */
fun climateTemperatureFontWeight(temperatureFraction: Float): Int =
    (280 + temperatureFraction.coerceIn(0f, 1f) * 520).toInt().coerceIn(280, 800)
