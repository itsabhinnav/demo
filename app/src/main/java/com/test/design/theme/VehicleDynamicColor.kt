package com.test.design.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import com.test.design.presentation.ivi.vehicle.DriveMode

private val EcoPrimary = Color(0xFF66BB6A)
private val EcoOnPrimary = Color(0xFF06200A)
private val EcoSecondary = Color(0xFF81C784)
private val EcoSurface = Color(0xFF0A140C)
private val EcoContainer = Color(0xFF1B3A22)

private val ComfortPrimary = Color(0xFF64B5F6)
private val ComfortOnPrimary = Color(0xFF041820)
private val ComfortSecondary = Color(0xFF90CAF9)
private val ComfortSurface = Color(0xFF0A1018)
private val ComfortContainer = Color(0xFF1A2F4A)

private val SportPrimary = Color(0xFFFF7043)
private val SportOnPrimary = Color(0xFF2A0C04)
private val SportSecondary = Color(0xFFFFAB91)
private val SportSurface = Color(0xFF180A08)
private val SportContainer = Color(0xFF4A1C12)

private val LowBatteryTint = Color(0xFFFF5252)
private val VehicleOnSurface = Color(0xFFF2F4F7)
private val VehicleOnSurfaceVariant = Color(0xFFC5CAD3)

/**
 * Expressive color scheme keyed to [DriveMode], with a low-battery warning tint.
 *
 * Vehicle surfaces stay dark for the cockpit look; content colors are always light
 * so labels remain readable regardless of the app light/dark [base] theme.
 */
fun vehicleColorScheme(
    driveMode: DriveMode,
    batteryFraction: Float,
    base: ColorScheme,
    isCharging: Boolean = false,
): ColorScheme {
    val primary: Color
    val onPrimary: Color
    val secondary: Color
    val surface: Color
    val container: Color
    when (driveMode) {
        DriveMode.Eco -> {
            primary = EcoPrimary
            onPrimary = EcoOnPrimary
            secondary = EcoSecondary
            surface = EcoSurface
            container = EcoContainer
        }
        DriveMode.Comfort -> {
            primary = ComfortPrimary
            onPrimary = ComfortOnPrimary
            secondary = ComfortSecondary
            surface = ComfortSurface
            container = ComfortContainer
        }
        DriveMode.Sport -> {
            primary = SportPrimary
            onPrimary = SportOnPrimary
            secondary = SportSecondary
            surface = SportSurface
            container = SportContainer
        }
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
    val blendedOnPrimary = when {
        isCharging -> Color(0xFF003440)
        else -> onPrimary
    }
    val blendedSurface = lerp(surface, Color(0xFF1A0808), lowBatteryBlend * 0.35f)
        .let { if (isCharging) lerp(it, Color(0xFF061820), 0.4f) else it }

    return base.copy(
        primary = blendedPrimary,
        onPrimary = blendedOnPrimary,
        primaryContainer = container,
        onPrimaryContainer = lerp(Color(0xFFC8E6C9), Color(0xFFFFCCBC), lowBatteryBlend),
        inversePrimary = secondary,
        secondary = secondary,
        onSecondary = Color(0xFF0A140C),
        secondaryContainer = lerp(Color(0xFF1B5E20), Color(0xFFBF360C), lowBatteryBlend),
        onSecondaryContainer = Color(0xFFE8F5E9),
        tertiary = lerp(primary, secondary, 0.5f),
        onTertiary = Color(0xFF0A140C),
        tertiaryContainer = lerp(container, Color(0xFF5D2108), lowBatteryBlend),
        onTertiaryContainer = Color(0xFFE8EAF0),
        background = blendedSurface,
        onBackground = VehicleOnSurface,
        surface = blendedSurface,
        onSurface = VehicleOnSurface,
        surfaceVariant = lerp(Color(0xFF1E3A2A), Color(0xFF3E1A12), lowBatteryBlend),
        onSurfaceVariant = VehicleOnSurfaceVariant,
        surfaceTint = blendedPrimary,
        outline = lerp(Color(0xFF6A8A7A), Color(0xFF8A4A3A), lowBatteryBlend),
        outlineVariant = lerp(Color(0xFF2A4A3A), Color(0xFF5A2A1A), lowBatteryBlend),
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
