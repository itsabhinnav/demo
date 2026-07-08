package com.test.design.presentation.ivi.climate

enum class AirflowMode(val label: String) {
    Face("Face"),
    Feet("Feet"),
    Windshield("Windshield"),
    Auto("Auto"),
}

enum class ClimateZone(val label: String) {
    Driver("Driver"),
    Passenger("Passenger"),
}

data class ClimateUiState(
    val temperatureCelsius: Int = 22,
    val passengerTemperatureCelsius: Int = 21,
    val minTemperature: Int = 16,
    val maxTemperature: Int = 30,
    val airflowMode: AirflowMode = AirflowMode.Auto,
    val fanSpeed: Int = 3,
    val maxFanSpeed: Int = 5,
    val isAcEnabled: Boolean = true,
    val isSyncEnabled: Boolean = true,
    val isRecirculationOn: Boolean = false,
    val activeZone: ClimateZone = ClimateZone.Driver,
    val seatHeatLevel: Int = 1,
    val maxSeatHeatLevel: Int = 3,
)

sealed interface ClimateEvent {
    data object IncreaseTemperature : ClimateEvent
    data object DecreaseTemperature : ClimateEvent
    data class SelectAirflow(val mode: AirflowMode) : ClimateEvent
    data object ToggleAc : ClimateEvent
    data object ToggleSync : ClimateEvent
    data object ToggleRecirculation : ClimateEvent
    data class SelectZone(val zone: ClimateZone) : ClimateEvent
    data class SetFanSpeed(val speed: Int) : ClimateEvent
    data object IncreaseSeatHeat : ClimateEvent
    data object DecreaseSeatHeat : ClimateEvent
}
