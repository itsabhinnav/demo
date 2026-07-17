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
            assertTrue(pose.eyeWidth in 0.5f..1.3f)
            assertTrue(pose.eyeHeight in 0.4f..1.3f)
            assertTrue(pose.borderGlow in 0f..1.2f)
            assertTrue(pose.blush in 0f..1f)
            assertTrue(pose.roundness in 0.3f..1f)
        }
    }

    @Test
    fun wavePoseSharesSameStyleWithStateEnergy() {
        AssistantMood.entries.forEach { mood ->
            val wave = mood.toWavePose()
            assertTrue(wave.amplitude in 0f..1.2f)
            assertTrue(wave.speed in 0f..1.5f)
            assertTrue(wave.thickness in 0f..1.5f)
            assertTrue(wave.bloom in 0f..1.2f)
        }
        assertTrue(AssistantMood.Speaking.toWavePose().amplitude > AssistantMood.Idle.toWavePose().amplitude)
        assertTrue(AssistantMood.Listening.toWavePose().amplitude > AssistantMood.Sad.toWavePose().amplitude)
        assertTrue(AssistantMood.Speaking.toWavePose().speed > AssistantMood.Thinking.toWavePose().speed)
    }

    @Test
    fun dialogueScriptCoversAllMoods() {
        val moods = DemoDialogueScript.map { it.mood }.toSet()
        assertTrue(moods.containsAll(AssistantMood.entries.toSet()))
        assertTrue(DemoDialogueScript.any { it.speaker == DialogueSpeaker.User })
        assertTrue(DemoDialogueScript.any { it.speaker == DialogueSpeaker.Assistant })
    }
}
