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
                    "Excited",
                    "Bored",
                    "Drowsy",
                    "Tired",
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
        assertTrue(AssistantMood.Excited.glowIntensity > AssistantMood.Tired.glowIntensity)
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
            assertTrue(pose.eyeOpen in 0.3f..1.35f)
            assertTrue(pose.eyeWidth in 0.5f..1.3f)
            assertTrue(pose.eyeHeight in 0.35f..1.3f)
            assertTrue(pose.borderGlow in 0f..1.2f)
            assertTrue(pose.blush in 0f..1f)
            assertTrue(pose.roundness in 0.5f..1f)
            assertTrue(pose.mouthCurve in -1.1f..1.1f)
            assertTrue(pose.mouthOpen in 0f..1f)
            assertTrue(pose.mouthWidth in 0.5f..1.3f)
            assertTrue(pose.eyeStyle in -1.1f..1.1f)
        }
        assertTrue(AssistantMood.Happy.toFacePose().mouthCurve > AssistantMood.Sad.toFacePose().mouthCurve)
        assertTrue(AssistantMood.Speaking.toFacePose().mouthOpen > AssistantMood.Idle.toFacePose().mouthOpen)
        assertTrue(AssistantMood.Excited.toFacePose().eyeOpen > AssistantMood.Drowsy.toFacePose().eyeOpen)
    }

    @Test
    fun immersiveEyePoseConveysEmotionShapes() {
        AssistantMood.entries.forEach { mood ->
            val pose = mood.toImmersiveEyePose()
            assertTrue(pose.eyeOpen in 0.2f..1.5f)
            assertTrue(pose.eyeWidth in 0.8f..1.5f)
            assertTrue(pose.eyeHeight in 0.3f..1.4f)
            assertTrue(pose.faceGlow in 0f..1.2f)
            assertTrue(pose.mouthVisible in 0f..1f)
            assertTrue(pose.blinkSpeed in 0.2f..1.6f)
        }
        // Subtle single persona — moods only nudge mouth / openness slightly
        assertTrue(
            AssistantMood.Happy.toImmersiveEyePose().mouthCurve >
                AssistantMood.Sad.toImmersiveEyePose().mouthCurve,
        )
        assertTrue(
            AssistantMood.Listening.toImmersiveEyePose().eyeOpen >=
                AssistantMood.Drowsy.toImmersiveEyePose().eyeOpen,
        )
        assertTrue(AssistantMood.Speaking.toImmersiveEyePose().mouthVisible > 0.5f)
        assertTrue(
            AssistantMood.Bored.toImmersiveEyePose().eyeOpen <=
                AssistantMood.Idle.toImmersiveEyePose().eyeOpen,
        )
    }

    @Test
    fun droidFacePoseMapsMoodToGlyph() {
        AssistantMood.entries.forEach { mood ->
            val pose = mood.toDroidFacePose()
            assertTrue(pose.glyph == mood.toDroidFaceGlyph())
        }
        assertTrue(AssistantMood.Happy.toDroidFaceGlyph() == DroidFaceGlyph.SquintSmile)
        assertTrue(AssistantMood.Sad.toDroidFaceGlyph() == DroidFaceGlyph.Sad)
        assertTrue(AssistantMood.Excited.toDroidFaceGlyph() == DroidFaceGlyph.StarEyes)
        assertTrue(AssistantMood.Drowsy.toDroidFaceGlyph() == DroidFaceGlyph.Sleeping)
        assertTrue(AssistantMood.Searching.toDroidFaceGlyph() == DroidFaceGlyph.Search)
        assertEquals(36, DroidFaceGlyph.entries.size)
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
    fun presencePoseMorphsEnergyByMood() {
        AssistantMood.entries.forEach { mood ->
            val pose = mood.toPresencePose()
            assertTrue(pose.energy in 0f..1.2f)
            assertTrue(pose.spread in 0f..1.2f)
            assertTrue(pose.sparkle in 0f..1.2f)
        }
        assertTrue(AssistantMood.Listening.toPresencePose().energy > AssistantMood.Idle.toPresencePose().energy)
        assertTrue(AssistantMood.Searching.toPresencePose().sparkle > AssistantMood.Sad.toPresencePose().sparkle)
    }

    @Test
    fun dialogueScriptCoversAllMoods() {
        val moods = DemoDialogueScript.map { it.mood }.toSet()
        assertTrue(moods.containsAll(AssistantMood.entries.toSet()))
        assertTrue(DemoDialogueScript.any { it.speaker == DialogueSpeaker.User })
        assertTrue(DemoDialogueScript.any { it.speaker == DialogueSpeaker.Assistant })
        assertTrue(
            DemoDialogueScript.any {
                it.speaker == DialogueSpeaker.User && it.text.isNotBlank()
            },
        )
    }

    @Test
    fun immersiveScriptShowsOneLinePhasesAndEmotions() {
        val moods = ImmersiveDialogueScript.map { it.mood }.toSet()
        assertTrue(moods.contains(AssistantMood.Listening))
        assertTrue(moods.contains(AssistantMood.Thinking))
        assertTrue(moods.contains(AssistantMood.Speaking))
        // Single-persona script — no emotion carousel
        assertTrue(moods.none { it == AssistantMood.Excited || it == AssistantMood.Bored })
        assertTrue(ImmersiveDialogueScript.all { it.text.isNotBlank() })
        assertTrue(ImmersiveDialogueScript.any { it.speaker == DialogueSpeaker.User })
        assertTrue(ImmersiveDialogueScript.any { it.speaker == DialogueSpeaker.Assistant })
    }
}
