package com.test.design.presentation.ivi.climate

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class TemperatureScaleTest {

    @Test
    fun indexCountsStepsFromTheMinimum() {
        assertEquals(0, temperatureScaleIndex(16f, 16f, 1f))
        assertEquals(6, temperatureScaleIndex(22f, 16f, 1f))
        assertEquals(12, temperatureScaleIndex(22f, 16f, 0.5f))
    }

    @Test
    fun dragRightSelectsCooler() {
        assertEquals(-1, temperatureScaleDragSteps(dragPx = 40f, spacingPx = 36f))
        assertEquals(2, temperatureScaleDragSteps(dragPx = -80f, spacingPx = 36f))
        assertEquals(0, temperatureScaleDragSteps(dragPx = 10f, spacingPx = 36f))
    }

    @Test
    fun labelsAreSixDegreesApart() {
        assertTrue(temperatureScaleIsMajorTick(18f))
        assertTrue(temperatureScaleIsMajorTick(24f))
        assertTrue(temperatureScaleIsMajorTick(72f))
        assertFalse(temperatureScaleIsMajorTick(21f))
        assertFalse(temperatureScaleIsMajorTick(22f))
        assertFalse(temperatureScaleIsMajorTick(22.5f))
        assertFalse(temperatureScaleIsMajorTick(73f))
    }

    @Test
    fun celsiusUsesHalfDegreeSegmentsAndFahrenheitUsesWholeSteps() {
        assertEquals(0.5f, temperatureScaleSegmentStep(TemperatureUnit.Celsius, 1f))
        assertEquals(0.5f, temperatureScaleSegmentStep(TemperatureUnit.Celsius, 0.5f))
        assertEquals(0.5f, temperatureScaleSegmentStep(TemperatureUnit.Fahrenheit, 0.5f))
        assertEquals(1f, temperatureScaleSegmentStep(TemperatureUnit.Fahrenheit, 1f))
    }

    @Test
    fun tickHeightPeaksAtTheCenter() {
        val center = temperatureScaleTickHeightFraction(0f, 200f)
        val edge = temperatureScaleTickHeightFraction(200f, 200f)
        assertEquals(1f, center, 0.001f)
        assertTrue(edge < center)
        assertTrue(edge > 0.6f)
    }

    @Test
    fun edgeFadeIsSolidInTheMiddleAndGoneAtTheCorner() {
        assertEquals(1f, temperatureScaleEdgeFade(0f, 200f), 0.001f)
        assertEquals(1f, temperatureScaleEdgeFade(80f, 200f), 0.001f)
        val corner = temperatureScaleEdgeFade(200f, 200f)
        assertEquals(0f, corner, 0.001f)
        val nearCorner = temperatureScaleEdgeFade(170f, 200f)
        assertTrue(nearCorner in 0.05f..0.6f)
    }
}