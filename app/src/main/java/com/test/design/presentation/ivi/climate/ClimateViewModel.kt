package com.test.design.presentation.ivi.climate

import com.test.design.core.mvi.MviViewModel

class ClimateViewModel : MviViewModel<ClimateUiState, ClimateEvent>(ClimateUiState()) {

    override fun onEvent(event: ClimateEvent) {
        when (event) {
            ClimateEvent.IncreaseTemperature -> adjustActiveTemperature(+1)
            ClimateEvent.DecreaseTemperature -> adjustActiveTemperature(-1)
            is ClimateEvent.AdjustZoneTemperature -> adjustZoneTemperature(event.zone, event.delta)
            is ClimateEvent.SelectAirflow -> setState { copy(airflowMode = event.mode) }
            ClimateEvent.ToggleAc -> setState { copy(isAcEnabled = !isAcEnabled) }
            ClimateEvent.ToggleSync -> setState { copy(isSyncEnabled = !isSyncEnabled) }
            ClimateEvent.ToggleRecirculation -> setState { copy(isRecirculationOn = !isRecirculationOn) }
            ClimateEvent.ToggleFrontDefrost -> setState { copy(isFrontDefrostOn = !isFrontDefrostOn) }
            ClimateEvent.ToggleRearDefrost -> setState { copy(isRearDefrostOn = !isRearDefrostOn) }
            ClimateEvent.ToggleTemperatureUnit -> setState {
                copy(
                    temperatureUnit = when (temperatureUnit) {
                        TemperatureUnit.Celsius -> TemperatureUnit.Fahrenheit
                        TemperatureUnit.Fahrenheit -> TemperatureUnit.Celsius
                    },
                )
            }
            is ClimateEvent.SelectZone -> setState { copy(activeZone = event.zone) }
            is ClimateEvent.SetFanSpeed -> setState {
                copy(fanSpeed = event.speed.coerceIn(1, maxFanSpeed))
            }
            ClimateEvent.CycleSeatHeat -> setState {
                copy(seatHeatLevel = nextLevel(seatHeatLevel, maxSeatHeatLevel))
            }
            ClimateEvent.CycleSteeringHeat -> setState {
                copy(steeringHeatLevel = nextLevel(steeringHeatLevel, maxSteeringHeatLevel))
            }
            ClimateEvent.CycleSeatVent -> setState {
                copy(seatVentLevel = nextLevel(seatVentLevel, maxSeatVentLevel))
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

    private fun nextLevel(current: Int, max: Int): Int =
        if (current >= max) 0 else current + 1

    private fun adjustZoneTemperature(zone: ClimateZone, delta: Int) {
        setState {
            if (isSyncEnabled) {
                copy(
                    temperatureCelsius = (temperatureCelsius + delta)
                        .coerceIn(minTemperature, maxTemperature),
                    passengerTemperatureCelsius = (passengerTemperatureCelsius + delta)
                        .coerceIn(minTemperature, maxTemperature),
                    activeZone = zone,
                )
            } else {
                when (zone) {
                    ClimateZone.Driver -> copy(
                        temperatureCelsius = (temperatureCelsius + delta)
                            .coerceIn(minTemperature, maxTemperature),
                        activeZone = ClimateZone.Driver,
                    )
                    ClimateZone.Passenger -> copy(
                        passengerTemperatureCelsius = (passengerTemperatureCelsius + delta)
                            .coerceIn(minTemperature, maxTemperature),
                        activeZone = ClimateZone.Passenger,
                    )
                }
            }
        }
    }

    private fun adjustActiveTemperature(delta: Int) {
        adjustZoneTemperature(currentState().activeZone, delta)
    }
}
