package com.test.design.presentation.ivi.vehicle

import com.test.design.core.motion.AppMotionScheme

enum class DriveMode(val label: String, val subtitle: String) {
    Eco("Eco", "Max range"),
    Comfort("Comfort", "Balanced"),
    Sport("Sport", "Peak power"),
}

enum class RegenLevel(val label: String, val strength: Float) {
    Off("Off", 0f),
    Low("Low", 0.35f),
    Standard("Standard", 0.65f),
    High("High", 1f),
}

enum class SystemHealth(val label: String) {
    Good("Good"),
    Caution("Check soon"),
    Warning("Service"),
}

data class VehicleSystemMetric(
    val id: String,
    val label: String,
    val valuePercent: Int,
    val detail: String,
    val health: SystemHealth,
)

data class VehicleUiState(
    val batteryPercent: Int = 82,
    val rangeMiles: Int = 240,
    val driveMode: DriveMode = DriveMode.Comfort,
    val efficiencyMpkWh: Float = 3.8f,
    val odometerMiles: Int = 18420,
    val tripEnergyKwh: Float = 12.4f,
    val isCharging: Boolean = false,
    val chargeRateKw: Float = 11f,
    val regenLevel: RegenLevel = RegenLevel.Standard,
    val systems: List<VehicleSystemMetric> = defaultSystems(),
    val selectedSystemId: String? = null,
    val screenMotionScheme: AppMotionScheme = AppMotionScheme.Expressive,
    val motionPreviewTrigger: Int = 0,
    val activeMotionToken: Int = 0,
)

sealed interface VehicleEvent {
    data class SelectDriveMode(val mode: DriveMode) : VehicleEvent
    data object CycleBatteryDemo : VehicleEvent
    data object ToggleCharging : VehicleEvent
    data object CycleRegenLevel : VehicleEvent
    data class SelectSystem(val id: String) : VehicleEvent
    data class SelectScreenMotionScheme(val scheme: AppMotionScheme) : VehicleEvent
    data object ReplayMotionPreview : VehicleEvent
    data class SelectMotionToken(val index: Int) : VehicleEvent
}

private fun defaultSystems() = listOf(
    VehicleSystemMetric("motor", "Drive motor", 94, "Nominal temperature", SystemHealth.Good),
    VehicleSystemMetric("brakes", "Brake pads", 78, "12,400 mi remaining", SystemHealth.Good),
    VehicleSystemMetric("battery", "HV battery", 88, "Cell balance optimal", SystemHealth.Good),
    VehicleSystemMetric("cabin", "Cabin filter", 42, "Replace in 6 weeks", SystemHealth.Caution),
)
