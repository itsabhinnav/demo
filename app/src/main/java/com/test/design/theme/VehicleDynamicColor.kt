package com.test.design.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import com.test.design.presentation.ivi.vehicle.DriveMode

private val EcoPrimary = Color(0xFF66BB6A)
private val EcoSecondary = Color(0xFF2E7D32)
private val EcoSurface = Color(0xFF0A140C)
private val EcoContainer = Color(0xFF1B3A22)

private val ComfortPrimary = Color(0xFF64B5F6)
private val ComfortSecondary = Color(0xFF1565C0)
private val ComfortSurface = Color(0xFF0A1018)
private val ComfortContainer = Color(0xFF1A2F4A)

private val SportPrimary = Color(0xFFFF7043)
private val SportSecondary = Color(0xFFD84315)
private val SportSurface = Color(0xFF180A08)
private val SportContainer = Color(0xFF4A1C12)

private val LowBatteryTint = Color(0xFFFF5252)

/**
 * Expressive color scheme keyed to [DriveMode], with a low-battery warning tint.
 */
fun vehicleColorScheme(
    driveMode: DriveMode,
    batteryFraction: Float,
    isCharging: Boolean = false,
): ColorScheme {
    val (primary, secondary, surface, container) = when (driveMode) {
        DriveMode.Eco -> listOf(EcoPrimary, EcoSecondary, EcoSurface, EcoContainer)
        DriveMode.Comfort -> listOf(ComfortPrimary, ComfortSecondary, ComfortSurface, ComfortContainer)
        DriveMode.Sport -> listOf(SportPrimary, SportSecondary, SportSurface, SportContainer)
    }
    val lowBatteryBlend = if (batteryFraction < 0.3f) {
        ((0.3f - batteryFraction) / 0.3f).coerceIn(0f, 1f)
    } else {
        0f
    }
    val blendedPrimary = when {
        isCharging -> lerp(primary, Color(0xFF00E5FF), 0.55f)
        else -> lerp(primary, LowBatteryTint, lowBatteryBlend * 0.45f)
    }
    val blendedSurface = lerp(surface, Color(0xFF1A0808), lowBatteryBlend * 0.35f)
        .let { if (isCharging) lerp(it, Color(0xFF061820), 0.4f) else it }

    return ColorScheme(
        primary = blendedPrimary,
        onPrimary = Color.White,
        primaryContainer = container,
        onPrimaryContainer = lerp(Color(0xFFC8E6C9), Color(0xFFFFCCBC), lowBatteryBlend),
        inversePrimary = secondary,
        secondary = secondary,
        onSecondary = Color.White,
        secondaryContainer = lerp(Color(0xFF1B5E20), Color(0xFFBF360C), lowBatteryBlend),
        onSecondaryContainer = Color(0xFFE3F2FD),
        tertiary = lerp(primary, secondary, 0.5f),
        onTertiary = Color.White,
        tertiaryContainer = lerp(container, Color(0xFF5D2108), lowBatteryBlend),
        onTertiaryContainer = Color(0xFFE8EAF0),
        background = blendedSurface,
        onBackground = Color(0xFFE8EAF0),
        surface = blendedSurface,
        onSurface = Color(0xFFE8EAF0),
        surfaceVariant = lerp(Color(0xFF1E3A2A), Color(0xFF3E1A12), lowBatteryBlend),
        onSurfaceVariant = Color(0xFFB0BEC5),
        surfaceTint = blendedPrimary,
        inverseSurface = Color(0xFFE8EAF0),
        inverseOnSurface = Color(0xFF1A1C1E),
        error = Color(0xFFFFB4AB),
        onError = Color(0xFF690005),
        errorContainer = Color(0xFF93000A),
        onErrorContainer = Color(0xFFFFDAD6),
        outline = lerp(Color(0xFF4A6A5A), Color(0xFF8A4A3A), lowBatteryBlend),
        outlineVariant = lerp(Color(0xFF2A4A3A), Color(0xFF5A2A1A), lowBatteryBlend),
        scrim = Color.Black,
        surfaceBright = lerp(Color(0xFF2A4A3A), Color(0xFF5A2A1A), lowBatteryBlend),
        surfaceDim = lerp(Color(0xFF0A120E), Color(0xFF150808), lowBatteryBlend),
        surfaceContainer = lerp(Color(0xFF152520), Color(0xFF2A1010), lowBatteryBlend),
        surfaceContainerHigh = lerp(Color(0xFF1E3030), Color(0xFF351818), lowBatteryBlend),
        surfaceContainerHighest = lerp(Color(0xFF283A38), Color(0xFF402020), lowBatteryBlend),
        surfaceContainerLow = lerp(Color(0xFF101A16), Color(0xFF1E0C0C), lowBatteryBlend),
        surfaceContainerLowest = lerp(Color(0xFF080F0C), Color(0xFF100606), lowBatteryBlend),
    )
}

fun batteryToFraction(percent: Int): Float = (percent / 100f).coerceIn(0f, 1f)
