package com.test.design.presentation.ivi.adaptivespace

import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AdaptiveSpaceChoreographyTest {

    @Test
    fun sceneOrderCoversMapMediaSplitParkingCollapse() {
        assertEquals(
            listOf(
                AdaptiveSpaceScene.MapOnly,
                AdaptiveSpaceScene.MediaOverlay,
                AdaptiveSpaceScene.SplitWithMedia,
                AdaptiveSpaceScene.ParkingWithSplit,
                AdaptiveSpaceScene.Collapse,
            ),
            AdaptiveSpaceScene.entries,
        )
    }

    @Test
    fun mediaAndParkingAreMutuallyExclusiveInScenes() {
        AdaptiveSpaceScene.entries.forEach { scene ->
            assertFalse(
                "Scene ${scene.name} opens media and parking together",
                scene.mediaOpen && scene.parkingOpen,
            )
        }
    }

    @Test
    fun playChoreographyEmitsEveryScene() = runBlocking {
        val seen = mutableListOf<AdaptiveSpaceScene>()
        playAdaptiveSpaceChoreography(
            onScene = { seen += it },
            delayScale = 0.0,
        )
        assertEquals(AdaptiveSpaceScene.entries, seen)
    }

    @Test
    fun toPanelStateMapsFlags() {
        val media = AdaptiveSpaceScene.MediaOverlay.toPanelState()
        assertTrue(media.mediaOpen)
        assertFalse(media.parkingOpen)
        assertEquals(0f, media.splitFraction)

        val split = AdaptiveSpaceScene.SplitWithMedia.toPanelState()
        assertTrue(split.mediaOpen)
        assertEquals(0.5f, split.splitFraction)

        val parking = AdaptiveSpaceScene.ParkingWithSplit.toPanelState()
        assertTrue(parking.parkingOpen)
        assertFalse(parking.mediaOpen)
        assertEquals(0.5f, parking.splitFraction)
    }
}
