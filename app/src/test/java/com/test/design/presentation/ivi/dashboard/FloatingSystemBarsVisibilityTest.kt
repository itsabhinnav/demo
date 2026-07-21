package com.test.design.presentation.ivi.dashboard

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class FloatingSystemBarsVisibilityTest {

    @Before
    fun reset() {
        FloatingSystemBarsVisibility.show()
    }

    @Test
    fun startsVisible() {
        assertTrue(FloatingSystemBarsVisibility.visible.value)
    }

    @Test
    fun hideAndShow() {
        FloatingSystemBarsVisibility.hide()
        assertFalse(FloatingSystemBarsVisibility.visible.value)
        FloatingSystemBarsVisibility.show()
        assertTrue(FloatingSystemBarsVisibility.visible.value)
    }

    @Test
    fun toggleFlipsVisibility() {
        FloatingSystemBarsVisibility.toggle()
        assertFalse(FloatingSystemBarsVisibility.visible.value)
        FloatingSystemBarsVisibility.toggle()
        assertTrue(FloatingSystemBarsVisibility.visible.value)
    }

    @Test
    fun setVisible() {
        FloatingSystemBarsVisibility.setVisible(false)
        assertFalse(FloatingSystemBarsVisibility.visible.value)
        FloatingSystemBarsVisibility.setVisible(true)
        assertTrue(FloatingSystemBarsVisibility.visible.value)
    }
}
