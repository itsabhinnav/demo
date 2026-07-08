package com.test.design.presentation.ivi.vehicle

/**
 * Per-mode layout profile for in-place Vehicle screen rearrangement.
 *
 * Eco prioritizes range/energy, Comfort keeps a balanced three-column grid,
 * Sport expands performance/motion tooling on the right.
 */
data class VehicleDriveModeLayout(
    val energyWeight: Float,
    val centerWeight: Float,
    val sideWeight: Float,
    val motionStudioWeight: Float,
    val statsWeight: Float,
    val showMotionStudio: Boolean,
    val driveSelectorFirst: Boolean,
    val layoutLabel: String,
)

fun DriveMode.layoutProfile(): VehicleDriveModeLayout = when (this) {
    DriveMode.Eco -> VehicleDriveModeLayout(
        energyWeight = 0.44f,
        centerWeight = 0.38f,
        sideWeight = 0.18f,
        motionStudioWeight = 0f,
        statsWeight = 1f,
        showMotionStudio = false,
        driveSelectorFirst = true,
        layoutLabel = "Range-first layout",
    )
    DriveMode.Comfort -> VehicleDriveModeLayout(
        energyWeight = 0.36f,
        centerWeight = 0.34f,
        sideWeight = 0.30f,
        motionStudioWeight = 0.58f,
        statsWeight = 0.42f,
        showMotionStudio = true,
        driveSelectorFirst = false,
        layoutLabel = "Balanced layout",
    )
    DriveMode.Sport -> VehicleDriveModeLayout(
        energyWeight = 0.28f,
        centerWeight = 0.30f,
        sideWeight = 0.42f,
        motionStudioWeight = 0.72f,
        statsWeight = 0.28f,
        showMotionStudio = true,
        driveSelectorFirst = false,
        layoutLabel = "Performance layout",
    )
}

fun DriveMode.defaultRegenLevel(): RegenLevel = when (this) {
    DriveMode.Eco -> RegenLevel.High
    DriveMode.Comfort -> RegenLevel.Standard
    DriveMode.Sport -> RegenLevel.Low
}
