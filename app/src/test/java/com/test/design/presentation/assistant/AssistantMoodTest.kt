package com.test.design.presentation.assistant

import org.junit.Assert.assertTrue
import org.junit.Test

class AssistantMoodTest {

    @Test
    fun moodsCoverRequestedPersonalityStates() {
        val labels = AssistantMood.entries.map { it.label }.toSet()
        assertTrue(
            labels.containsAll(
                listOf(
                    "Idle",
                    "Listening",
                    "Speaking",
                    "Thinking",
                    "Happy",
                    "Sad",
                    "Reading",
                    "Searching",
                ),
            ),
        )
    }

    @Test
    fun listeningUsesStrongerGlowThanIdle() {
        assertTrue(AssistantMood.Listening.glowIntensity > AssistantMood.Idle.glowIntensity)
        assertTrue(AssistantMood.Searching.glowIntensity > AssistantMood.Sad.glowIntensity)
    }

    @Test
    fun glowIntensityStaysInUnitRange() {
        AssistantMood.entries.forEach { mood ->
            assertTrue(mood.glowIntensity in 0f..1f)
        }
    }
}
