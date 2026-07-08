package com.test.design.presentation.ivi.vehicle

import com.test.design.core.motion.AppMotionScheme

enum class DriveMode(val label: String, val subtitle: String) {
    Eco("Eco", "Max range"),
    Comfort("Comfort", "Balanced"),
    Sport("Sport", "Peak power"),
}

data class TirePressure(
    val position: String,
    val psi: Int,
    val isOptimal: Boolean,
)

data class VehicleUiState(
    val batteryPercent: Int = 82,
    val rangeMiles: Int = 240,
    val driveMode: DriveMode = DriveMode.Comfort,
    val efficiencyMpkWh: Float = 3.8f,
    val tirePressures: List<TirePressure> = listOf(
        TirePressure("FL", 36, true),
        TirePressure("FR", 36, true),
        TirePressure("RL", 35, true),
        TirePressure("RR", 34, true),
    ),
    val odometerMiles: Int = 18420,
    val tripEnergyKwh: Float = 12.4f,
    val isCharging: Boolean = false,
    val chargeRateKw: Float = 11f,
    val screenMotionScheme: AppMotionScheme = AppMotionScheme.Expressive,
    val motionPreviewTrigger: Int = 0,
    val activeMotionToken: Int = 0,
)

sealed interface VehicleEvent {
    data class SelectDriveMode(val mode: DriveMode) : VehicleEvent
    data object CycleBatteryDemo : VehicleEvent
    data object ToggleCharging : VehicleEvent
    data class SelectScreenMotionScheme(val scheme: AppMotionScheme) : VehicleEvent
    data object ReplayMotionPreview : VehicleEvent
    data class SelectMotionToken(val index: Int) : VehicleEvent
    data class CycleTirePressure(val position: String) : VehicleEvent
}
