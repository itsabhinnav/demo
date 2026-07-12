package com.test.design.presentation.ivi.climate

enum class AirflowMode(val label: String) {
    Face("Face"),
    BiLevel("Bi-level"),
    Feet("Feet"),
    Auto("Auto"),
}

enum class ClimateZone(val label: String) {
    Driver("Driver"),
    Passenger("Passenger"),
}

enum class TemperatureUnit(val symbol: String, val shortLabel: String) {
    Celsius("°C", "°C"),
    Fahrenheit("°F", "°F"),
}

/** Converts stored Celsius to the value shown for [unit]. */
fun Int.toDisplayTemperature(unit: TemperatureUnit): Int = when (unit) {
    TemperatureUnit.Celsius -> this
    TemperatureUnit.Fahrenheit -> (this * 9 / 5) + 32
}

fun formatTemperature(celsius: Int, unit: TemperatureUnit): String =
    "${celsius.toDisplayTemperature(unit)}${unit.symbol}"

data class ClimateUiState(
    val temperatureCelsius: Int = 22,
    val passengerTemperatureCelsius: Int = 21,
    val minTemperature: Int = 16,
    val maxTemperature: Int = 30,
    val temperatureUnit: TemperatureUnit = TemperatureUnit.Celsius,
    val airflowMode: AirflowMode = AirflowMode.Auto,
    val fanSpeed: Int = 3,
    val maxFanSpeed: Int = 5,
    val isAcEnabled: Boolean = true,
    val isSyncEnabled: Boolean = true,
    val isRecirculationOn: Boolean = false,
    val isFrontDefrostOn: Boolean = false,
    val isRearDefrostOn: Boolean = false,
    val activeZone: ClimateZone = ClimateZone.Driver,
    val seatHeatLevel: Int = 1,
    val maxSeatHeatLevel: Int = 3,
    val steeringHeatLevel: Int = 0,
    val maxSteeringHeatLevel: Int = 3,
    val seatVentLevel: Int = 0,
    val maxSeatVentLevel: Int = 3,
)

sealed interface ClimateEvent {
    data object IncreaseTemperature : ClimateEvent
    data object DecreaseTemperature : ClimateEvent
    data class AdjustZoneTemperature(val zone: ClimateZone, val delta: Int) : ClimateEvent
    data class SelectAirflow(val mode: AirflowMode) : ClimateEvent
    data object ToggleAc : ClimateEvent
    data object ToggleSync : ClimateEvent
    data object ToggleRecirculation : ClimateEvent
    data object ToggleFrontDefrost : ClimateEvent
    data object ToggleRearDefrost : ClimateEvent
    data object ToggleTemperatureUnit : ClimateEvent
    data class SelectZone(val zone: ClimateZone) : ClimateEvent
    data class SetFanSpeed(val speed: Int) : ClimateEvent
    data object CycleSeatHeat : ClimateEvent
    data object CycleSteeringHeat : ClimateEvent
    data object CycleSeatVent : ClimateEvent
    data object IncreaseSeatHeat : ClimateEvent
    data object DecreaseSeatHeat : ClimateEvent
}
