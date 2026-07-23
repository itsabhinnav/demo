package com.test.design.presentation.assistant.gallery

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AssistantUiStyleTest {

    @Test
    fun atLeastTwelveSemiTransparentStyles() {
        assertTrue(AssistantUiStyle.entries.size >= 12)
    }

    @Test
    fun requiredFamiliesPresent() {
        val titles = AssistantUiStyle.entries.map { it.title }.toSet()
        assertTrue(titles.contains("Voice plate"))
        assertTrue(titles.contains("Face only"))
        assertTrue(titles.contains("Waveform"))
        assertTrue(titles.contains("Orb"))
        assertTrue(titles.contains("Capsule face"))
        assertTrue(titles.contains("Bar"))
        assertTrue(titles.contains("Side rail"))
        assertTrue(titles.contains("Equalizer"))
        assertTrue(titles.contains("Listening rings"))
        assertTrue(titles.contains("Corner bubble"))
        assertTrue(titles.contains("Wave + face"))
        assertTrue(titles.contains("Ambient pill"))
        assertTrue(titles.contains("Immersive eyes"))
        assertTrue(titles.contains("Immersive glow"))
        assertTrue(titles.contains("Droid face"))
        assertTrue(titles.contains("EPORO"))
        assertTrue(titles.contains("Fusion"))
        assertTrue(titles.contains("Fusion glow"))
        assertTrue(titles.contains("Fusion eyes"))
    }

    @Test
    fun styleNamesAreUnique() {
        assertEquals(AssistantUiStyle.entries.size, AssistantUiStyle.entries.map { it.name }.toSet().size)
    }
}
