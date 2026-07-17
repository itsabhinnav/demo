package com.test.design.presentation.ivi.dashboard.components

import androidx.compose.ui.unit.dp
import org.junit.Assert.assertEquals
import org.junit.Test

class MapChromeContentStartTest {

    @Test
    fun hiddenRail_usesOverlayInsetOnly() {
        assertEquals(16.dp, mapChromeContentStart(sidebarWidth = 400.dp, reveal = 0f))
    }

    @Test
    fun fullyRevealedRail_clearsSidebarWidth() {
        assertEquals(416.dp, mapChromeContentStart(sidebarWidth = 400.dp, reveal = 1f))
    }

    @Test
    fun partialReveal_scalesWithRailWidth() {
        assertEquals(216.dp, mapChromeContentStart(sidebarWidth = 400.dp, reveal = 0.5f))
    }

    @Test
    fun revealIsCoercedIntoUnitInterval() {
        assertEquals(16.dp, mapChromeContentStart(sidebarWidth = 400.dp, reveal = -1f))
        assertEquals(416.dp, mapChromeContentStart(sidebarWidth = 400.dp, reveal = 2f))
    }
}
