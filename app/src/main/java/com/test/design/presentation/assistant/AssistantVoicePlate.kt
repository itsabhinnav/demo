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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

private val PanelShape = RoundedCornerShape(28.dp)

/**
 * Scalable UI–style assistant panel (Gemini-inspired).
 * Slides over the map from the trailing edge — no voice waveform.
 */
@Composable
fun AssistantSidePanel(
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
        modifier = modifier
            .width(420.dp)
            .fillMaxHeight(),
        shape = PanelShape,
        color = Color(0xE610141C),
        shadowElevation = 16.dp,
        tonalElevation = 0.dp,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color(0xF0181E2A),
                            Color(0xF00A0C12),
                        ),
                    ),
                ),
        ) {
            // Ambient field behind content
            AssistantPresence(
                mood = mood,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
                    .align(Alignment.TopCenter)
                    .padding(top = 48.dp),
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Column {
                        Text(
                            text = "Assistant",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White,
                        )
                        AnimatedContent(
                            targetState = mood.voiceLabel,
                            transitionSpec = {
                                fadeIn(tween(220)) togetherWith fadeOut(tween(140))
                            },
                            label = "mood",
                        ) { label ->
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelLarge,
                                color = mood.glowColor,
                            )
                        }
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

                Spacer(Modifier.height(28.dp))

                AssistantFace(
                    mood = mood,
                    modifier = Modifier.size(168.dp),
                )

                Spacer(modifier = Modifier.weight(1f))

                AnimatedContent(
                    targetState = beat,
                    transitionSpec = {
                        fadeIn(tween(300)) togetherWith fadeOut(tween(180))
                    },
                    label = "prompt",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp),
                ) { current ->
                    PanelInlinePrompt(
                        beat = current,
                        userPrompt = playback.latestUserPrompt,
                    )
                }

                Spacer(Modifier.height(12.dp))

                Text(
                    text = "Map stays live under this panel",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.35f),
                )
            }
        }
    }
}

@Composable
private fun PanelInlinePrompt(
    beat: DialogueBeat,
    userPrompt: String?,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        when (beat.speaker) {
            DialogueSpeaker.User -> {
                Text(
                    text = "You",
                    style = MaterialTheme.typography.labelMedium,
                    color = beat.mood.glowColor.copy(alpha = 0.9f),
                )
                Text(
                    text = "“${beat.text}”",
                    style = MaterialTheme.typography.titleLarge,
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
                        color = Color.White.copy(alpha = 0.4f),
                        textAlign = TextAlign.Center,
                        fontStyle = FontStyle.Italic,
                        maxLines = 2,
                    )
                }
                Text(
                    text = beat.text,
                    style = MaterialTheme.typography.titleLarge,
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

/** Shared mood label for chips + captions. */
val AssistantMood.voiceLabel: String
    get() = label

/** Back-compat alias while callers migrate off the old voice plate name. */
@Composable
fun AssistantVoicePlate(
    mood: AssistantMood,
    onMoodChange: (AssistantMood) -> Unit,
    modifier: Modifier = Modifier,
    onDismiss: (() -> Unit)? = null,
    autoPlay: Boolean = true,
) {
    AssistantSidePanel(
        mood = mood,
        onMoodChange = onMoodChange,
        modifier = modifier,
        onDismiss = onDismiss,
        autoPlay = autoPlay,
    )
}
