package com.test.design.presentation.assistant

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Legacy side-by-side stage — delegates to the bottom voice plate experience.
 */
@Composable
fun AssistantDialogueStage(
    mood: AssistantMood,
    onMoodChange: (AssistantMood) -> Unit,
    modifier: Modifier = Modifier,
    autoPlay: Boolean = true,
) {
    AssistantVoicePlate(
        mood = mood,
        onMoodChange = onMoodChange,
        autoPlay = autoPlay,
        modifier = modifier.fillMaxSize(),
    )
}
