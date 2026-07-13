package com.test.design.theme

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import androidx.compose.ui.unit.dp

class WindowLayoutInfoTest {

    @Test
    fun compactWidthClass() {
        val info = windowLayoutInfo(480.dp, 800.dp)
        assertEquals(WindowWidthClass.Compact, info.widthClass)
        assertFalse(info.useSideBySide)
    }

    @Test
    fun expandedLandscape_usesSideBySide() {
        val info = windowLayoutInfo(1280.dp, 720.dp)
        assertEquals(WindowWidthClass.Expanded, info.widthClass)
        assertTrue(info.useSideBySide)
    }
}
