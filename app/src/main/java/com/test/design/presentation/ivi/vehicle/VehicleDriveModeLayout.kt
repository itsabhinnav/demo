package com.test.design.presentation.ivi.vehicle

/**
 * Per-mode layout profile for in-place Vehicle screen rearrangement.
 */
data class VehicleDriveModeLayout(
    val energyWeight: Float,
    val centerWeight: Float,
    val sideWeight: Float,
    val insightsWeight: Float,
    val statsWeight: Float,
    val showDriveInsights: Boolean,
    val driveSelectorFirst: Boolean,
    val layoutLabel: String,
)

fun DriveMode.layoutProfile(): VehicleDriveModeLayout = when (this) {
    DriveMode.Eco -> VehicleDriveModeLayout(
        energyWeight = 0.44f,
        centerWeight = 0.38f,
        sideWeight = 0.18f,
        insightsWeight = 0f,
        statsWeight = 1f,
        showDriveInsights = false,
        driveSelectorFirst = true,
        layoutLabel = "Range-first layout",
    )
    DriveMode.Comfort -> VehicleDriveModeLayout(
        energyWeight = 0.36f,
        centerWeight = 0.34f,
        sideWeight = 0.30f,
        insightsWeight = 0.55f,
        statsWeight = 0.45f,
        showDriveInsights = true,
        driveSelectorFirst = false,
        layoutLabel = "Balanced layout",
    )
    DriveMode.Sport -> VehicleDriveModeLayout(
        energyWeight = 0.28f,
        centerWeight = 0.30f,
        sideWeight = 0.42f,
        insightsWeight = 0.65f,
        statsWeight = 0.35f,
        showDriveInsights = true,
        driveSelectorFirst = false,
        layoutLabel = "Performance layout",
    )
}

fun DriveMode.defaultRegenLevel(): RegenLevel = when (this) {
    DriveMode.Eco -> RegenLevel.High
    DriveMode.Comfort -> RegenLevel.Standard
    DriveMode.Sport -> RegenLevel.Low
}
