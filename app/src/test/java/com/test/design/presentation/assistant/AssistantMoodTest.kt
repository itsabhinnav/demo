package com.test.design.presentation.assistant

import org.junit.Assert.assertEquals
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
                    "Working",
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

    @Test
    fun facePoseKeepsOneCharacterBounds() {
        AssistantMood.entries.forEach { mood ->
            val pose = mood.toFacePose()
            assertTrue(pose.eyeOpen in 0.4f..1.3f)
            assertTrue(pose.eyeSmile in 0f..1f)
            assertTrue(pose.eyeDroop in 0f..1f)
            assertTrue(pose.mouthWidth in 0.1f..0.8f)
            assertTrue(pose.mouthOpen in 0f..1f)
            assertEquals(mood.glowIntensity, pose.glowIntensity, 0.001f)
        }
    }

    @Test
    fun wavePoseLayersStayContinuous() {
        AssistantMood.entries.forEach { mood ->
            val wave = mood.toWavePose()
            assertTrue(wave.ringAmount in 0f..1f)
            assertTrue(wave.ribbonAmount in 0f..1f)
            assertTrue(wave.barAmount in 0f..1f)
            assertTrue(wave.haloAmount in 0f..1f)
            assertTrue(wave.energy in 0f..1f)
        }
        assertTrue(AssistantMood.Speaking.toWavePose().barAmount > AssistantMood.Idle.toWavePose().barAmount)
        assertTrue(AssistantMood.Listening.toWavePose().ringAmount > AssistantMood.Sad.toWavePose().ringAmount)
    }
}
