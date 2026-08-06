package com.test.design.presentation.ivi.climate

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.test.design.data.car.CarClimateRepository
import com.test.design.data.car.ClimateHvacConnection
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

class ClimateViewModel(
    application: Application,
) : AndroidViewModel(application) {

    private val repository = CarClimateRepository(application)

    private val _state = MutableStateFlow(ClimateUiState())
    val state: StateFlow<ClimateUiState> = _state.asStateFlow()

    init {
        repository.connection
            .onEach { connection -> applyConnection(connection) }
            .launchIn(viewModelScope)
        repository.start()
    }

    fun onEvent(event: ClimateEvent) {
        val live = _state.value.isLive
        when (event) {
            ClimateEvent.IncreaseTemperature -> adjustActiveTemperature(+1, live)
            ClimateEvent.DecreaseTemperature -> adjustActiveTemperature(-1, live)
            is ClimateEvent.AdjustZoneTemperature ->
                adjustZoneTemperature(event.zone, event.delta, live)
            is ClimateEvent.SelectAirflow -> {
                if (live) {
                    repository.setAirflowMode(event.mode)
                } else {
                    setState { copy(airflowMode = event.mode) }
                }
            }
            ClimateEvent.ToggleAc -> {
                val next = !_state.value.isAcEnabled
                if (live) repository.setAcEnabled(next) else setState { copy(isAcEnabled = next) }
            }
            ClimateEvent.ToggleSync -> {
                val next = !_state.value.isSyncEnabled
                if (live) repository.setSyncEnabled(next) else setState { copy(isSyncEnabled = next) }
            }
            ClimateEvent.ToggleRecirculation -> {
                val next = !_state.value.isRecirculationOn
                if (live) {
                    repository.setRecirculation(next)
                } else {
                    setState { copy(isRecirculationOn = next) }
                }
            }
            ClimateEvent.ToggleFrontDefrost -> {
                val next = !_state.value.isFrontDefrostOn
                if (live) {
                    repository.setFrontDefrost(next)
                } else {
                    setState { copy(isFrontDefrostOn = next) }
                }
            }
            ClimateEvent.ToggleRearDefrost -> {
                val next = !_state.value.isRearDefrostOn
                if (live) {
                    repository.setRearDefrost(next)
                } else {
                    setState { copy(isRearDefrostOn = next) }
                }
            }
            ClimateEvent.ToggleTemperatureUnit -> {
                val next = when (_state.value.temperatureUnit) {
                    TemperatureUnit.Celsius -> TemperatureUnit.Fahrenheit
                    TemperatureUnit.Fahrenheit -> TemperatureUnit.Celsius
                }
                if (live && _state.value.capabilities.hasTemperatureUnit) {
                    repository.setTemperatureUnit(next)
                } else {
                    setState { copy(temperatureUnit = next) }
                }
            }
            is ClimateEvent.SelectZone -> setState { copy(activeZone = event.zone) }
            is ClimateEvent.SetFanSpeed -> {
                val speed = event.speed.coerceIn(1, _state.value.maxFanSpeed)
                if (live) repository.setFanSpeed(speed) else setState { copy(fanSpeed = speed) }
            }
            ClimateEvent.CycleSeatHeat -> {
                val next = nextLevel(_state.value.seatHeatLevel, _state.value.maxSeatHeatLevel)
                if (live) repository.setSeatHeatLevel(next) else setState { copy(seatHeatLevel = next) }
            }
            ClimateEvent.CycleSteeringHeat -> {
                val next = nextLevel(_state.value.steeringHeatLevel, _state.value.maxSteeringHeatLevel)
                if (live) {
                    repository.setSteeringHeatLevel(next)
                } else {
                    setState { copy(steeringHeatLevel = next) }
                }
            }
            ClimateEvent.CycleSeatVent -> {
                val next = nextLevel(_state.value.seatVentLevel, _state.value.maxSeatVentLevel)
                if (live) repository.setSeatVentLevel(next) else setState { copy(seatVentLevel = next) }
            }
            ClimateEvent.IncreaseSeatHeat -> {
                val next = (_state.value.seatHeatLevel + 1).coerceAtMost(_state.value.maxSeatHeatLevel)
                if (live) repository.setSeatHeatLevel(next) else setState { copy(seatHeatLevel = next) }
            }
            ClimateEvent.DecreaseSeatHeat -> {
                val next = (_state.value.seatHeatLevel - 1).coerceAtLeast(0)
                if (live) repository.setSeatHeatLevel(next) else setState { copy(seatHeatLevel = next) }
            }
        }
    }

    fun activeTemperature(): Float = when (_state.value.activeZone) {
        ClimateZone.Driver -> _state.value.temperatureCelsius
        ClimateZone.Passenger -> _state.value.passengerTemperatureCelsius
    }

    override fun onCleared() {
        repository.stop()
        super.onCleared()
    }

    private fun applyConnection(connection: ClimateHvacConnection) {
        if (!connection.isLive) {
            setState {
                copy(
                    isLive = false,
                    capabilities = ClimateCapabilities(),
                    temperatureStepCelsius = 1f,
                    temperatureStepFahrenheit = 1f,
                    minTemperatureFahrenheit = null,
                )
            }
            return
        }
        setState {
            copy(
                isLive = true,
                capabilities = connection.capabilities,
                temperatureCelsius = connection.driverTempCelsius,
                passengerTemperatureCelsius = connection.passengerTempCelsius,
                minTemperature = connection.minTemperature,
                maxTemperature = connection.maxTemperature,
                temperatureStepCelsius = connection.temperatureStepCelsius,
                temperatureStepFahrenheit = connection.temperatureStepFahrenheit,
                minTemperatureFahrenheit = connection.minTemperatureFahrenheit,
                fanSpeed = connection.fanSpeed,
                maxFanSpeed = connection.maxFanSpeed,
                airflowMode = connection.airflowMode,
                isAcEnabled = connection.isAcEnabled,
                isSyncEnabled = connection.isSyncEnabled,
                isRecirculationOn = connection.isRecirculationOn,
                isFrontDefrostOn = connection.isFrontDefrostOn,
                isRearDefrostOn = connection.isRearDefrostOn,
                seatHeatLevel = connection.seatHeatLevel,
                maxSeatHeatLevel = connection.maxSeatHeatLevel,
                seatVentLevel = connection.seatVentLevel,
                maxSeatVentLevel = connection.maxSeatVentLevel,
                steeringHeatLevel = connection.steeringHeatLevel,
                maxSteeringHeatLevel = connection.maxSteeringHeatLevel,
                temperatureUnit = connection.temperatureUnit,
            )
        }
    }

    private fun nextLevel(current: Int, max: Int): Int =
        if (current >= max) 0 else current + 1

    private fun nextTemp(current: Float, deltaSteps: Int, state: ClimateUiState): Float =
        stepTemperature(
            currentCelsius = current,
            deltaSteps = deltaSteps,
            unit = state.temperatureUnit,
            minCelsius = state.minTemperature,
            maxCelsius = state.maxTemperature,
            celsiusStep = state.temperatureStepCelsius,
            fahrenheitStep = state.temperatureStepFahrenheit,
        )

    private fun adjustZoneTemperature(zone: ClimateZone, delta: Int, live: Boolean) {
        val state = _state.value
        if (live) {
            if (state.isSyncEnabled) {
                if (state.capabilities.hasDriverTemp) {
                    repository.setZoneTemperature(
                        ClimateZone.Driver,
                        nextTemp(state.temperatureCelsius, delta, state),
                    )
                }
                if (state.capabilities.hasPassengerTemp) {
                    repository.setZoneTemperature(
                        ClimateZone.Passenger,
                        nextTemp(state.passengerTemperatureCelsius, delta, state),
                    )
                }
            } else {
                when (zone) {
                    ClimateZone.Driver -> if (state.capabilities.hasDriverTemp) {
                        repository.setZoneTemperature(
                            ClimateZone.Driver,
                            nextTemp(state.temperatureCelsius, delta, state),
                        )
                    }
                    ClimateZone.Passenger -> if (state.capabilities.hasPassengerTemp) {
                        repository.setZoneTemperature(
                            ClimateZone.Passenger,
                            nextTemp(state.passengerTemperatureCelsius, delta, state),
                        )
                    }
                }
            }
            setState { copy(activeZone = zone) }
            return
        }

        setState {
            if (isSyncEnabled) {
                copy(
                    temperatureCelsius = nextTemp(temperatureCelsius, delta, this),
                    passengerTemperatureCelsius = nextTemp(passengerTemperatureCelsius, delta, this),
                    activeZone = zone,
                )
            } else {
                when (zone) {
                    ClimateZone.Driver -> copy(
                        temperatureCelsius = nextTemp(temperatureCelsius, delta, this),
                        activeZone = ClimateZone.Driver,
                    )
                    ClimateZone.Passenger -> copy(
                        passengerTemperatureCelsius = nextTemp(passengerTemperatureCelsius, delta, this),
                        activeZone = ClimateZone.Passenger,
                    )
                }
            }
        }
    }

    private fun adjustActiveTemperature(delta: Int, live: Boolean) {
        adjustZoneTemperature(_state.value.activeZone, delta, live)
    }

    private fun setState(reducer: ClimateUiState.() -> ClimateUiState) {
        _state.update(reducer)
    }
}
