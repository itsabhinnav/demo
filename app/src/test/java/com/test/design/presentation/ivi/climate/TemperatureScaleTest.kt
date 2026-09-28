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
    fun labelsFollowTheUnit() {
        assertEquals(4, temperatureScaleLabelGap(TemperatureUnit.Celsius))
        assertEquals(8, temperatureScaleLabelGap(TemperatureUnit.Fahrenheit))
        assertTrue(temperatureScaleIsMajorTick(16f, TemperatureUnit.Celsius))
        assertTrue(temperatureScaleIsMajorTick(20f, TemperatureUnit.Celsius))
        assertFalse(temperatureScaleIsMajorTick(18f, TemperatureUnit.Celsius))
        assertFalse(temperatureScaleIsMajorTick(22.5f, TemperatureUnit.Celsius))
        assertTrue(temperatureScaleIsMajorTick(64f, TemperatureUnit.Fahrenheit))
        assertTrue(temperatureScaleIsMajorTick(72f, TemperatureUnit.Fahrenheit))
        assertFalse(temperatureScaleIsMajorTick(68f, TemperatureUnit.Fahrenheit))
        assertFalse(temperatureScaleIsMajorTick(70f, TemperatureUnit.Fahrenheit))
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
    fun leftOfTheNeedleStaysBrighterThanTheRight() {
        val left = temperatureScaleSideBrightness(-80f, 200f)
        val right = temperatureScaleSideBrightness(80f, 200f)
        assertEquals(1f, left, 0.001f)
        assertTrue(right < left)
        assertTrue(right > 0.6f)
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