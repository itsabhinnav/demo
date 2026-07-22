package com.test.design.presentation.assistant

import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle

/**
 * Renders the active [AssistantFaceKind] from [AssistantFaceConfig] (or an override).
 * [AssistantFaceKind.None] draws nothing — transcript / chrome still show.
 */
@Composable
fun ConfigurableAssistantFace(
    mood: AssistantMood,
    modifier: Modifier = Modifier,
    kind: AssistantFaceKind? = null,
    gazeX: Float? = null,
    gazeY: Float? = null,
    mouthAmplitude: Float? = null,
    brandGlow: Color = Color(0xFF8AB4F8),
    highContrast: Boolean = false,
    gesture: FaceGesture = FaceGesture.None,
) {
    val configured by AssistantFaceConfig.kind.collectAsStateWithLifecycle()
    when (kind ?: configured) {
        AssistantFaceKind.None -> Box(modifier)
        AssistantFaceKind.ImmersiveEyes -> ImmersiveEyesFace(
            mood = mood,
            modifier = modifier,
            gazeX = gazeX,
            gazeY = gazeY,
            mouthAmplitude = mouthAmplitude,
            brandGlow = brandGlow,
            highContrast = highContrast,
            gesture = gesture,
        )
        AssistantFaceKind.Eporo -> EporoAssistantFace(
            mood = mood,
            modifier = modifier,
            brandGlow = brandGlow,
            gazeX = gazeX,
            gazeY = gazeY,
            mouthAmplitude = mouthAmplitude,
            gesture = gesture,
        )
        AssistantFaceKind.Droid -> DroidAssistantFace(
            mood = mood,
            modifier = modifier,
        )
        AssistantFaceKind.Glyph -> AssistantFace(
            mood = mood,
            modifier = modifier,
        )
    }
}
