package com.test.design.core.cluster

import com.test.design.core.DrivingUxState

/**
 * Simulated instrument-cluster glance values for the Display Safety / DriverUI story.
 * Not wired to VHAL — always labeled Simulated in the UI.
 */
data class ClusterUiState(
    val speedMph: Int,
    val speedLimitMph: Int = 60,
    val gear: String,
    val isSimulated: Boolean = true,
) {
    companion object {
        fun fromDrivingUx(state: DrivingUxState): ClusterUiState = when (state) {
            DrivingUxState.Parked -> ClusterUiState(speedMph = 0, gear = "P")
            DrivingUxState.Driving -> ClusterUiState(speedMph = 54, gear = "D")
            DrivingUxState.Restricted -> ClusterUiState(speedMph = 32, gear = "D")
        }
    }
}
