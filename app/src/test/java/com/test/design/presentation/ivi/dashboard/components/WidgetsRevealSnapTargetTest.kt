package com.test.design.presentation.ivi.dashboard.components

import org.junit.Assert.assertEquals
import org.junit.Test

class WidgetsRevealSnapTargetTest {

    @Test
    fun flingLeft_hidesWidgets() {
        assertEquals(0f, widgetsRevealSnapTarget(currentFraction = 0.8f, velocityPxPerSec = -1200f))
    }

    @Test
    fun flingRight_showsWidgets() {
        assertEquals(1f, widgetsRevealSnapTarget(currentFraction = 0.2f, velocityPxPerSec = 1200f))
    }

    @Test
    fun releasePastHalfway_showsWidgets() {
        assertEquals(1f, widgetsRevealSnapTarget(currentFraction = 0.55f, velocityPxPerSec = 0f))
    }

    @Test
    fun releaseBeforeHalfway_hidesWidgets() {
        assertEquals(0f, widgetsRevealSnapTarget(currentFraction = 0.4f, velocityPxPerSec = 0f))
    }
}
