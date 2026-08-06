package com.test.design.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.luminance

private val CoolPrimary = Color(0xFF4FC3F7)
private val CoolSecondary = Color(0xFF0288D1)
private val CoolTertiary = Color(0xFF80DEEA)
private val CoolContainer = Color(0xFF1B3A4B)

private val WarmPrimary = Color(0xFFFF7043)
private val WarmSecondary = Color(0xFFE64A19)
private val WarmTertiary = Color(0xFFFFAB91)
private val WarmContainer = Color(0xFF4A1C12)

/**
 * Interpolates an expressive color scheme between cool (blue) and warm (red) hues
 * based on normalized temperature [0f, 1f], preserving [base] canvas roles for light/dark.
 */
fun climateColorScheme(
    temperatureFraction: Float,
    base: ColorScheme,
): ColorScheme {
    val fraction = temperatureFraction.coerceIn(0f, 1f)
    val darkCanvas = base.background.luminance() < 0.5f
    val surfaceBlend = if (darkCanvas) 0.85f else 0.28f
    val coolSurface = Color(0xFF0D1B2A)
    val warmSurface = Color(0xFF1A0A08)

    return base.copy(
        primary = lerp(CoolPrimary, WarmPrimary, fraction),
        onPrimary = if (darkCanvas) Color.White else Color(0xFF0D2A40),
        primaryContainer = lerp(CoolContainer, WarmContainer, fraction).let {
            if (darkCanvas) it else lerp(base.primaryContainer, it, 0.45f)
        },
        onPrimaryContainer = lerp(Color(0xFFB3E5FC), Color(0xFFFFCCBC), fraction).let {
            if (darkCanvas) it else Color(0xFF1A1C1E)
        },
        inversePrimary = lerp(CoolSecondary, WarmSecondary, fraction),
        secondary = lerp(CoolSecondary, WarmSecondary, fraction),
        onSecondary = if (darkCanvas) Color.White else Color.White,
        secondaryContainer = lerp(Color(0xFF01579B), Color(0xFFBF360C), fraction).let {
            if (darkCanvas) it else lerp(base.secondaryContainer, it, 0.4f)
        },
        onSecondaryContainer = lerp(Color(0xFFB3E5FC), Color(0xFFFFCCBC), fraction).let {
            if (darkCanvas) it else Color(0xFF1A1C1E)
        },
        tertiary = lerp(CoolTertiary, WarmTertiary, fraction),
        onTertiary = Color(0xFF00363D),
        tertiaryContainer = lerp(Color(0xFF004D5A), Color(0xFF5D2108), fraction).let {
            if (darkCanvas) it else lerp(base.tertiaryContainer, it, 0.4f)
        },
        onTertiaryContainer = lerp(Color(0xFFB2EBF2), Color(0xFFFFCCBC), fraction).let {
            if (darkCanvas) it else Color(0xFF1A1C1E)
        },
        surface = lerp(base.surface, lerp(coolSurface, warmSurface, fraction), surfaceBlend),
        surfaceVariant = lerp(
            base.surfaceVariant,
            lerp(Color(0xFF1E3A4F), Color(0xFF3E1A12), fraction),
            surfaceBlend,
        ),
        surfaceTint = lerp(CoolPrimary, WarmPrimary, fraction),
        outline = lerp(
            base.outline,
            lerp(Color(0xFF4A6A7A), Color(0xFF8A4A3A), fraction),
            0.5f,
        ),
        outlineVariant = lerp(
            base.outlineVariant,
            lerp(Color(0xFF2A4A5A), Color(0xFF5A2A1A), fraction),
            0.5f,
        ),
        surfaceBright = lerp(
            base.surfaceBright,
            lerp(Color(0xFF2A4A5A), Color(0xFF5A2A1A), fraction),
            surfaceBlend,
        ),
        surfaceDim = lerp(
            base.surfaceDim,
            lerp(Color(0xFF0A1520), Color(0xFF150808), fraction),
            surfaceBlend,
        ),
        surfaceContainer = lerp(
            base.surfaceContainer,
            lerp(Color(0xFF152535), Color(0xFF2A1010), fraction),
            surfaceBlend,
        ),
        surfaceContainerHigh = lerp(
            base.surfaceContainerHigh,
            lerp(Color(0xFF1E3040), Color(0xFF351818), fraction),
            surfaceBlend,
        ),
        surfaceContainerHighest = lerp(
            base.surfaceContainerHighest,
            lerp(Color(0xFF283A4A), Color(0xFF402020), fraction),
            surfaceBlend,
        ),
        surfaceContainerLow = lerp(
            base.surfaceContainerLow,
            lerp(Color(0xFF101E2A), Color(0xFF1E0C0C), fraction),
            surfaceBlend,
        ),
        surfaceContainerLowest = lerp(
            base.surfaceContainerLowest,
            lerp(Color(0xFF080F18), Color(0xFF100606), fraction),
            surfaceBlend,
        ),
    )
}

fun temperatureToFraction(celsius: Float, min: Float = 16f, max: Float = 30f): Float {
    val span = (max - min).takeIf { it > 0f } ?: return 0f
    return ((celsius - min) / span).coerceIn(0f, 1f)
}

fun temperatureToFraction(celsius: Int, min: Int = 16, max: Int = 30): Float =
    temperatureToFraction(celsius.toFloat(), min.toFloat(), max.toFloat())

/** Normalized fraction at/below which snowflakes begin (≈22°C for 16–30). */
const val ClimateCoolThresholdFraction = 0.42f

/**
 * Snow intensity for one cabin zone.
 * The warmer side of a dual-zone split always returns 0 so flakes only appear
 * on the cooler side (both sides may show when temperatures match).
 */
fun zoneCoolIntensity(ownFraction: Float, otherFraction: Float): Float {
    if (ownFraction > otherFraction) return 0f
    return ((ClimateCoolThresholdFraction - ownFraction) / ClimateCoolThresholdFraction)
        .coerceIn(0f, 1f)
}

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
