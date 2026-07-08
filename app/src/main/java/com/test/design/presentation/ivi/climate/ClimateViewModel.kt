package com.test.design.presentation.ivi.climate

import com.test.design.core.mvi.MviViewModel

class ClimateViewModel : MviViewModel<ClimateUiState, ClimateEvent>(ClimateUiState()) {

    override fun onEvent(event: ClimateEvent) {
        when (event) {
            ClimateEvent.IncreaseTemperature -> adjustTemperature(+1)
            ClimateEvent.DecreaseTemperature -> adjustTemperature(-1)
            is ClimateEvent.SelectAirflow -> setState { copy(airflowMode = event.mode) }
            ClimateEvent.ToggleAc -> setState { copy(isAcEnabled = !isAcEnabled) }
            ClimateEvent.ToggleSync -> setState { copy(isSyncEnabled = !isSyncEnabled) }
            ClimateEvent.ToggleRecirculation -> setState { copy(isRecirculationOn = !isRecirculationOn) }
            is ClimateEvent.SelectZone -> setState { copy(activeZone = event.zone) }
            is ClimateEvent.SetFanSpeed -> setState {
                copy(fanSpeed = event.speed.coerceIn(1, maxFanSpeed))
            }
            ClimateEvent.IncreaseSeatHeat -> setState {
                copy(seatHeatLevel = (seatHeatLevel + 1).coerceAtMost(maxSeatHeatLevel))
            }
            ClimateEvent.DecreaseSeatHeat -> setState {
                copy(seatHeatLevel = (seatHeatLevel - 1).coerceAtLeast(0))
            }
        }
    }

    fun activeTemperature(): Int = when (currentState().activeZone) {
        ClimateZone.Driver -> currentState().temperatureCelsius
        ClimateZone.Passenger -> currentState().passengerTemperatureCelsius
    }

    private fun adjustTemperature(delta: Int) {
        setState {
            if (isSyncEnabled) {
                copy(
                    temperatureCelsius = (temperatureCelsius + delta).coerceIn(minTemperature, maxTemperature),
                    passengerTemperatureCelsius = (passengerTemperatureCelsius + delta).coerceIn(minTemperature, maxTemperature),
                )
            } else {
                when (activeZone) {
                    ClimateZone.Driver -> copy(
                        temperatureCelsius = (temperatureCelsius + delta).coerceIn(minTemperature, maxTemperature),
                    )
                    ClimateZone.Passenger -> copy(
                        passengerTemperatureCelsius = (passengerTemperatureCelsius + delta).coerceIn(minTemperature, maxTemperature),
                    )
                }
            }
        }
    }
}
