package com.test.design.presentation.ivi.vehicle

import com.test.design.core.mvi.MviViewModel

class VehicleViewModel : MviViewModel<VehicleUiState, VehicleEvent>(VehicleUiState()) {

    override fun onEvent(event: VehicleEvent) {
        when (event) {
            is VehicleEvent.SelectDriveMode -> setState { copy(driveMode = event.mode) }
            VehicleEvent.CycleBatteryDemo -> setState {
                val next = when {
                    batteryPercent >= 90 -> 62
                    batteryPercent >= 70 -> 45
                    else -> 88
                }
                copy(
                    batteryPercent = next,
                    rangeMiles = (next * 2.9f).toInt(),
                )
            }
        }
    }
}
