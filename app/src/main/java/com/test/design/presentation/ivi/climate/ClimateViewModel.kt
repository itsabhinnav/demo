package com.test.design.presentation.ivi.climate

import com.test.design.core.mvi.MviViewModel

class ClimateViewModel : MviViewModel<ClimateUiState, ClimateEvent>(ClimateUiState()) {

    override fun onEvent(event: ClimateEvent) {
        when (event) {
            ClimateEvent.IncreaseTemperature -> setState {
                copy(temperatureCelsius = (temperatureCelsius + 1).coerceAtMost(maxTemperature))
            }
            ClimateEvent.DecreaseTemperature -> setState {
                copy(temperatureCelsius = (temperatureCelsius - 1).coerceAtLeast(minTemperature))
            }
            is ClimateEvent.SelectAirflow -> setState { copy(airflowMode = event.mode) }
            ClimateEvent.ToggleAc -> setState { copy(isAcEnabled = !isAcEnabled) }
        }
    }
}
