package com.test.design.presentation.ivi.map

import com.test.design.theme.windowLayoutInfo
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import androidx.compose.ui.unit.dp

class MapOverlayMetricsTest {

    @Test
    fun compactPanel_usesDenseCardsAndHidesSecondary() {
        val metrics = mapOverlayMetricsForLayout(windowLayoutInfo(400.dp, 500.dp))

        assertTrue(metrics.compactCards)
        assertFalse(metrics.showSecondaryPane)
        assertFalse(metrics.showFavorites)
        assertEquals(10.dp, metrics.cardSpacing)
    }

    @Test
    fun wideTallPanel_showsSecondaryAndFavorites() {
        val metrics = mapOverlayMetricsForLayout(windowLayoutInfo(1200.dp, 800.dp))

        assertFalse(metrics.compactCards)
        assertTrue(metrics.showSecondaryPane)
        assertTrue(metrics.showFavorites)
        assertTrue(metrics.showRouteStepsInline)
    }

    @Test
    fun mediumPanel_capsCardWidth() {
        val metrics = mapOverlayMetricsForLayout(windowLayoutInfo(720.dp, 600.dp))

        assertEquals(520.dp, metrics.cardMaxWidth)
        assertTrue(metrics.compactCards)
    }
}
