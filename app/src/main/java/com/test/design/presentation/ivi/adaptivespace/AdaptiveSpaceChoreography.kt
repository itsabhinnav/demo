package com.test.design.presentation.ivi.adaptivespace

import kotlinx.coroutines.delay

/**
 * Scripted Adaptive Space “cockpit ballet” for customer demos.
 * Steps mirror Scalable UI panel events: map → media → split → parking → collapse.
 */
enum class AdaptiveSpaceScene(
    val mediaOpen: Boolean,
    val parkingOpen: Boolean,
    val splitFraction: Float,
    val holdMs: Long,
) {
    MapOnly(mediaOpen = false, parkingOpen = false, splitFraction = 0f, holdMs = 500),
    MediaOverlay(mediaOpen = true, parkingOpen = false, splitFraction = 0f, holdMs = 1_400),
    SplitWithMedia(mediaOpen = true, parkingOpen = false, splitFraction = 0.5f, holdMs = 1_400),
    ParkingWithSplit(mediaOpen = false, parkingOpen = true, splitFraction = 0.5f, holdMs = 1_400),
    Collapse(mediaOpen = false, parkingOpen = false, splitFraction = 0f, holdMs = 400),
}

data class AdaptiveSpacePanelState(
    val mediaOpen: Boolean = false,
    val parkingOpen: Boolean = false,
    val splitFraction: Float = 0f,
)

fun AdaptiveSpaceScene.toPanelState(): AdaptiveSpacePanelState =
    AdaptiveSpacePanelState(
        mediaOpen = mediaOpen,
        parkingOpen = parkingOpen,
        splitFraction = splitFraction,
    )

/**
 * Plays the demo sequence. [onScene] is invoked at the start of each scene;
 * the coroutine delays for that scene’s [AdaptiveSpaceScene.holdMs] before advancing.
 *
 * @param delayScale multiply hold times (use `0` in unit tests).
 */
suspend fun playAdaptiveSpaceChoreography(
    onScene: (AdaptiveSpaceScene) -> Unit,
    scenes: List<AdaptiveSpaceScene> = AdaptiveSpaceScene.entries,
    delayScale: Double = 1.0,
) {
    scenes.forEach { scene ->
        onScene(scene)
        val hold = (scene.holdMs * delayScale).toLong().coerceAtLeast(0L)
        if (hold > 0L) delay(hold)
    }
}
