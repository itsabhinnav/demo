package com.test.design.presentation.ivi.dashboard

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class FloatingSystemBarsVisibilityTest {

    @Before
    fun reset() {
        FloatingSystemBarsVisibility.hide()
    }

    @Test
    fun startsHidden() {
        assertFalse(FloatingSystemBarsVisibility.visible.value)
    }

    @Test
    fun showAndHide() {
        FloatingSystemBarsVisibility.show()
        assertTrue(FloatingSystemBarsVisibility.visible.value)
        FloatingSystemBarsVisibility.hide()
        assertFalse(FloatingSystemBarsVisibility.visible.value)
    }

    @Test
    fun toggleFlipsVisibility() {
        FloatingSystemBarsVisibility.toggle()
        assertTrue(FloatingSystemBarsVisibility.visible.value)
        FloatingSystemBarsVisibility.toggle()
        assertFalse(FloatingSystemBarsVisibility.visible.value)
    }

    @Test
    fun setVisible() {
        FloatingSystemBarsVisibility.setVisible(true)
        assertTrue(FloatingSystemBarsVisibility.visible.value)
        FloatingSystemBarsVisibility.setVisible(false)
        assertFalse(FloatingSystemBarsVisibility.visible.value)
    }
}
