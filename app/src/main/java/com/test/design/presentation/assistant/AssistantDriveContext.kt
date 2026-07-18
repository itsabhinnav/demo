package com.test.design.presentation.assistant

import com.test.design.core.DrivingUxState
import com.test.design.core.cluster.ClusterUiState
import com.test.design.presentation.ivi.vehicle.VehicleUiState

/**
 * Proactive dialogue beats derived from driving UX + vehicle state.
 * Prefixed ahead of the main immersive script when the cabin context warrants it.
 */
internal fun buildDriveContextBeats(
    drivingUx: DrivingUxState,
    vehicle: VehicleUiState?,
    cluster: ClusterUiState = ClusterUiState.fromDrivingUx(drivingUx),
): List<DialogueBeat> {
    val beats = mutableListOf<DialogueBeat>()

    when (drivingUx) {
        DrivingUxState.Restricted -> beats += DialogueBeat(
            speaker = DialogueSpeaker.System,
            text = "Driver focus mode — keeping this glanceable",
            mood = AssistantMood.Listening,
            holdMs = 1800,
        )
        DrivingUxState.Driving -> beats += DialogueBeat(
            speaker = DialogueSpeaker.System,
            text = "You're at ${cluster.speedMph} mph · gear ${cluster.gear}",
            mood = AssistantMood.Idle,
            holdMs = 1600,
        )
        DrivingUxState.Parked -> Unit
    }

    val battery = vehicle?.batteryPercent
    val range = vehicle?.rangeMiles
    if (battery != null && battery <= 25) {
        beats += DialogueBeat(
            speaker = DialogueSpeaker.Assistant,
            text = "Battery is at $battery% — want a charger along the route?",
            mood = AssistantMood.Speaking,
            holdMs = 2800,
        )
    } else if (range != null && range <= 60) {
        beats += DialogueBeat(
            speaker = DialogueSpeaker.Assistant,
            text = "Range is about $range miles. I can find a charge stop.",
            mood = AssistantMood.Speaking,
            holdMs = 2600,
        )
    }

    if (vehicle?.isCharging == true) {
        beats += DialogueBeat(
            speaker = DialogueSpeaker.Assistant,
            text = "Charging at ${vehicle.chargeRateKw.toInt()} kW — you're all set.",
            mood = AssistantMood.Happy,
            holdMs = 2200,
        )
    }

    // Late-night / fatigue cue when already driving slowly in restricted mode.
    if (drivingUx == DrivingUxState.Restricted && (battery == null || battery > 25)) {
        beats += DialogueBeat(
            speaker = DialogueSpeaker.Assistant,
            text = "Long stretch ahead — I can keep watch and stay quiet.",
            mood = AssistantMood.Tired,
            holdMs = 2400,
        )
    }

    return beats
}

/**
 * Whether the immersive session should hand presence off to the cluster glance.
 */
internal fun shouldHandOffToCluster(drivingUx: DrivingUxState): Boolean =
    drivingUx == DrivingUxState.Restricted || drivingUx == DrivingUxState.Driving

/**
 * Cabin mic zone → gaze. Negative X looks toward the driver (LHD).
 */
internal fun gazeForSpeaker(speaker: DialogueSpeaker): Pair<Float, Float> = when (speaker) {
    DialogueSpeaker.User -> -0.42f to 0.05f
    DialogueSpeaker.Assistant -> 0f to -0.02f
    DialogueSpeaker.System -> 0.08f to 0f
}

internal fun faceGestureForText(text: String): FaceGesture = when {
    isAffirmativeUtterance(text) -> FaceGesture.Nod
    isNegativeUtterance(text) -> FaceGesture.Shake
    else -> FaceGesture.None
}

enum class FaceGesture {
    None,
    Nod,
    Shake,
}
