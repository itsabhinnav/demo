package com.test.design.presentation.ivi.vehicle

data class VehicleDriveModeLayout(
    val energyWeight: Float,
    val centerWeight: Float,
    val sideWeight: Float,
    val showDriveInsights: Boolean,
    val driveSelectorFirst: Boolean,
    val layoutLabel: String,
)

fun DriveMode.layoutProfile(): VehicleDriveModeLayout = when (this) {
    DriveMode.Eco -> VehicleDriveModeLayout(
        energyWeight = 0.44f,
        centerWeight = 0.38f,
        sideWeight = 0.18f,
        showDriveInsights = false,
        driveSelectorFirst = true,
        layoutLabel = "Range-first layout",
    )
    DriveMode.Comfort -> VehicleDriveModeLayout(
        energyWeight = 0.36f,
        centerWeight = 0.34f,
        sideWeight = 0.30f,
        showDriveInsights = true,
        driveSelectorFirst = false,
        layoutLabel = "Balanced layout",
    )
    DriveMode.Sport -> VehicleDriveModeLayout(
        energyWeight = 0.30f,
        centerWeight = 0.32f,
        sideWeight = 0.38f,
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
