package com.test.design.presentation.ivi.climate

/**
 * Per-band layout profile for in-place Climate screen rearrangement.
 *
 * Cool expands the fluid dial and ambient readouts, Comfort keeps a balanced
 * two-column grid, Warm prioritizes controls and stronger heating motion.
 */
enum class ClimateTemperatureBand(val label: String, val subtitle: String) {
    Cool("Cool", "Chilling cabin"),
    Comfort("Comfort", "Ideal cabin"),
    Warm("Warm", "Warming cabin"),
}

data class ClimateTemperatureLayout(
    val dialWeight: Float,
    val controlsWeight: Float,
    val ambientWeight: Float,
    val showAmbientPanel: Boolean,
    val dialPulseIntensity: Float,
    val fluidDriftMillis: Int,
    val layoutLabel: String,
)

fun Int.toClimateTemperatureBand(min: Int, max: Int): ClimateTemperatureBand {
    val comfortMid = (min + max) / 2
    return when {
        this <= comfortMid - 2 -> ClimateTemperatureBand.Cool
        this >= comfortMid + 2 -> ClimateTemperatureBand.Warm
        else -> ClimateTemperatureBand.Comfort
    }
}

fun ClimateTemperatureBand.layoutProfile(): ClimateTemperatureLayout = when (this) {
    ClimateTemperatureBand.Cool -> ClimateTemperatureLayout(
        dialWeight = 0.52f,
        controlsWeight = 0.48f,
        ambientWeight = 0.38f,
        showAmbientPanel = true,
        dialPulseIntensity = 0.06f,
        fluidDriftMillis = 7200,
        layoutLabel = "Cooling-first layout",
    )
    ClimateTemperatureBand.Comfort -> ClimateTemperatureLayout(
        dialWeight = 0.45f,
        controlsWeight = 0.55f,
        ambientWeight = 0.30f,
        showAmbientPanel = true,
        dialPulseIntensity = 0.04f,
        fluidDriftMillis = 5600,
        layoutLabel = "Balanced layout",
    )
    ClimateTemperatureBand.Warm -> ClimateTemperatureLayout(
        dialWeight = 0.40f,
        controlsWeight = 0.60f,
        ambientWeight = 0.42f,
        showAmbientPanel = true,
        dialPulseIntensity = 0.08f,
        fluidDriftMillis = 4200,
        layoutLabel = "Heating-first layout",
    )
}
