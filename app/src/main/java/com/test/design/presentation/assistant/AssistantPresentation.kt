package com.test.design.presentation.assistant

/**
 * Visual phase of an immersive assistant session.
 *
 * [Compact] — corner bubble while listening (non-blocking).
 * [Immersive] — full-screen overlay once ready to respond.
 */
enum class AssistantPresentation {
    Compact,
    Immersive,
}

/** Whether this beat should expand the session from corner bubble to fullscreen. */
internal fun shouldExpandToImmersive(beat: DialogueBeat): Boolean {
    if (beat.speaker == DialogueSpeaker.Assistant) return true
    return when (beat.mood) {
        AssistantMood.Thinking,
        AssistantMood.Searching,
        AssistantMood.Reading,
        AssistantMood.Speaking,
        AssistantMood.Happy,
        AssistantMood.Sad,
        AssistantMood.Excited,
        -> true
        else -> false
    }
}
