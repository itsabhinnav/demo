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
            is VehicleEvent.SelectSystem -> setState {
                copy(
                    selectedSystemId = event.id,
                    systems = systems.map { system ->
                        if (system.id != event.id) system
                        else {
                            val next = when {
                                system.valuePercent >= 95 -> 38
                                system.valuePercent >= 70 -> system.valuePercent - 8
                                else -> system.valuePercent + 12
                            }.coerceIn(20, 100)
                            system.copy(
                                valuePercent = next,
                                health = when {
                                    next >= 75 -> SystemHealth.Good
                                    next >= 50 -> SystemHealth.Caution
                                    else -> SystemHealth.Warning
                                },
                            )
                        }
                    },
                )
            }
            VehicleEvent.CycleRegenLevel -> setState {
                val levels = RegenLevel.entries
                val next = levels[(levels.indexOf(regenLevel) + 1) % levels.size]
                copy(regenLevel = next)
            }
            VehicleEvent.ReplayMotionPreview -> setState {
                copy(motionPreviewTrigger = motionPreviewTrigger + 1)
            }
            VehicleEvent.ToggleCharging -> setState { copy(isCharging = !isCharging) }
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
