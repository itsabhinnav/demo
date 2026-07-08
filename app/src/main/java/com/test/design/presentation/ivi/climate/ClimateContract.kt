package com.test.design.presentation.ivi.climate

enum class AirflowMode(val label: String) {
    Face("Face"),
    Feet("Feet"),
    Windshield("Windshield"),
    Auto("Auto"),
}

data class ClimateUiState(
    val temperatureCelsius: Int = 22,
    val minTemperature: Int = 16,
    val maxTemperature: Int = 30,
    val airflowMode: AirflowMode = AirflowMode.Auto,
    val fanSpeed: Int = 3,
    val isAcEnabled: Boolean = true,
)

sealed interface ClimateEvent {
    data object IncreaseTemperature : ClimateEvent
    data object DecreaseTemperature : ClimateEvent
    data class SelectAirflow(val mode: AirflowMode) : ClimateEvent
    data object ToggleAc : ClimateEvent
}
