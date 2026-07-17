package com.test.design.presentation.assistant

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

private val PlateShape = RoundedCornerShape(topStart = 36.dp, topEnd = 36.dp)

/**
 * Google Assistant–style bottom voice plate.
 * User prompts and assistant replies render as the plate’s main text — no chat list.
 */
@Composable
fun AssistantVoicePlate(
    mood: AssistantMood,
    onMoodChange: (AssistantMood) -> Unit,
    modifier: Modifier = Modifier,
    onDismiss: (() -> Unit)? = null,
    autoPlay: Boolean = true,
) {
    val playback = rememberDialoguePlayback(autoPlay = autoPlay)
    DialoguePlaybackEffects(
        playback = playback,
        onMoodChange = onMoodChange,
    )
    val beat = playback.activeBeat

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = PlateShape,
        color = Color(0xF00A0C12),
        shadowElevation = 24.dp,
        tonalElevation = 0.dp,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF151925),
                            Color(0xFF080A10),
                        ),
                    ),
                )
                .padding(horizontal = 24.dp, vertical = 14.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier
                    .size(width = 44.dp, height = 4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color.White.copy(alpha = 0.22f)),
            )

            Spacer(Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                AnimatedContent(
                    targetState = mood.voiceLabel,
                    transitionSpec = { fadeIn(tween(220)) togetherWith fadeOut(tween(140)) },
                    label = "mood_chip",
                ) { label ->
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelLarge,
                        color = mood.glowColor,
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    FilledTonalIconButton(onClick = playback::playPause) {
                        Icon(
                            imageVector = if (playback.playing) {
                                Icons.Default.Pause
                            } else {
                                Icons.Default.PlayArrow
                            },
                            contentDescription = if (playback.playing) "Pause" else "Play",
                        )
                    }
                    FilledTonalIconButton(onClick = playback::replay) {
                        Icon(Icons.Default.Replay, contentDescription = "Replay")
                    }
                    if (onDismiss != null) {
                        FilledTonalIconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, contentDescription = "Close")
                        }
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            AssistantFace(
                mood = mood,
                modifier = Modifier.size(148.dp),
            )

            Spacer(Modifier.height(8.dp))

            VoiceWaveform(
                mood = mood,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .padding(horizontal = 8.dp),
            )

            Spacer(Modifier.height(16.dp))

            // Inline prompt / reply — the only dialogue surface in the plate
            AnimatedContent(
                targetState = beat,
                transitionSpec = { fadeIn(tween(280)) togetherWith fadeOut(tween(160)) },
                label = "plate_prompt",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 4.dp),
            ) { current ->
                VoicePlateInlinePrompt(
                    beat = current,
                    userPrompt = playback.latestUserPrompt,
                )
            }

            Spacer(Modifier.height(12.dp))
        }
    }
}

/**
 * Shows the active line in the plate: user prompts as heard speech,
 * assistant replies as spoken text — no conversation transcript list.
 */
@Composable
private fun VoicePlateInlinePrompt(
    beat: DialogueBeat,
    userPrompt: String?,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        when (beat.speaker) {
            DialogueSpeaker.User -> {
                Text(
                    text = "You",
                    style = MaterialTheme.typography.labelMedium,
                    color = beat.mood.glowColor.copy(alpha = 0.85f),
                )
                Text(
                    text = "“${beat.text}”",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    fontStyle = FontStyle.Italic,
                )
            }
            DialogueSpeaker.Assistant -> {
                if (!userPrompt.isNullOrBlank()) {
                    Text(
                        text = "You · “$userPrompt”",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.45f),
                        textAlign = TextAlign.Center,
                        fontStyle = FontStyle.Italic,
                        maxLines = 2,
                    )
                }
                Text(
                    text = beat.text,
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                )
            }
            DialogueSpeaker.System -> {
                Text(
                    text = beat.text,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.White.copy(alpha = 0.65f),
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

/** Voice-plate facing label. */
val AssistantMood.voiceLabel: String
    get() = label
