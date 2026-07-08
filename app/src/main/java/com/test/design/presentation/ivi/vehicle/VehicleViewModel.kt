package com.test.design.presentation.ivi.vehicle

import com.test.design.core.mvi.MviViewModel

class VehicleViewModel : MviViewModel<VehicleUiState, VehicleEvent>(VehicleUiState()) {

    override fun onEvent(event: VehicleEvent) {
        when (event) {
            is VehicleEvent.SelectDriveMode -> setState { copy(driveMode = event.mode) }
            is VehicleEvent.SelectScreenMotionScheme -> setState {
                copy(
                    screenMotionScheme = event.scheme,
                    motionPreviewTrigger = motionPreviewTrigger + 1,
                )
            }
            is VehicleEvent.SelectMotionToken -> setState {
                copy(
                    activeMotionToken = event.index,
                    motionPreviewTrigger = motionPreviewTrigger + 1,
                )
            }
            VehicleEvent.ReplayMotionPreview -> setState {
                copy(motionPreviewTrigger = motionPreviewTrigger + 1)
            }
            VehicleEvent.ToggleCharging -> setState { copy(isCharging = !isCharging) }
            is VehicleEvent.CycleTirePressure -> setState {
                copy(
                    selectedTirePosition = event.position,
                    tirePressures = tirePressures.map { tire ->
                        if (tire.position != event.position) tire
                        else {
                            val nextPsi = if (tire.psi >= 38) 32 else tire.psi + 1
                            tire.copy(
                                psi = nextPsi,
                                isOptimal = nextPsi in TirePressure.OPTIMAL_MIN_PSI..TirePressure.OPTIMAL_MAX_PSI,
                            )
                        }
                    },
                )
            }
            VehicleEvent.CycleBatteryDemo -> setState {
                val next = when {
                    batteryPercent >= 90 -> 62
                    batteryPercent >= 70 -> 45
                    else -> 88
                }
                copy(
                    batteryPercent = next,
                    rangeMiles = (next * 2.9f).toInt(),
                    isCharging = next < 70,
                )
            }
        }
    }
}
