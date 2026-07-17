package com.test.design.presentation.assistant

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

private val PlateShape = RoundedCornerShape(topStart = 36.dp, topEnd = 36.dp)

/**
 * Google Assistant–style bottom voice plate: face, waveform, and live dialogues inside.
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
    val listState = rememberLazyListState()
    DialoguePlaybackEffects(
        playback = playback,
        onMoodChange = onMoodChange,
        listState = listState,
    )

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
                .padding(horizontal = 20.dp, vertical = 14.dp),
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

            Spacer(Modifier.height(8.dp))

            // Persona + waveform row (compact Google Assistant–like header)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                AssistantFace(
                    mood = mood,
                    modifier = Modifier.size(120.dp),
                )
                Column(modifier = Modifier.weight(1f)) {
                    VoiceWaveform(
                        mood = mood,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                    )
                    Spacer(Modifier.height(6.dp))
                    AnimatedContent(
                        targetState = playback.activeBeat,
                        transitionSpec = { fadeIn(tween(240)) togetherWith fadeOut(tween(140)) },
                        label = "active_line",
                    ) { beat ->
                        Text(
                            text = when (beat.speaker) {
                                DialogueSpeaker.User -> "Listening…"
                                DialogueSpeaker.System -> beat.text
                                DialogueSpeaker.Assistant -> beat.text
                            },
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.78f),
                            maxLines = 2,
                        )
                    }
                }
            }

            Spacer(Modifier.height(14.dp))

            Text(
                text = "Conversation",
                style = MaterialTheme.typography.labelLarge,
                color = Color.White.copy(alpha = 0.45f),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp),
            )

            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 160.dp, max = 280.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(bottom = 8.dp),
            ) {
                items(
                    count = playback.visibleBeats.size,
                    key = { index -> "plate_beat_$index" },
                ) { index ->
                    VoicePlateDialogueBubble(
                        beat = playback.visibleBeats[index],
                        active = index == playback.beatIndex,
                    )
                }
            }
        }
    }
}

@Composable
internal fun VoicePlateDialogueBubble(
    beat: DialogueBeat,
    active: Boolean,
) {
    val isUser = beat.speaker == DialogueSpeaker.User
    val isSystem = beat.speaker == DialogueSpeaker.System
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = when {
            isSystem -> Alignment.CenterHorizontally
            isUser -> Alignment.End
            else -> Alignment.Start
        },
    ) {
        if (!isSystem) {
            Text(
                text = if (isUser) "You" else "Assistant · ${beat.mood.voiceLabel}",
                style = MaterialTheme.typography.labelSmall,
                color = beat.mood.glowColor.copy(alpha = 0.9f),
                modifier = Modifier.padding(bottom = 3.dp, start = 2.dp, end = 2.dp),
            )
        }
        Surface(
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (isUser) 16.dp else 5.dp,
                bottomEnd = if (isUser) 5.dp else 16.dp,
            ),
            color = when {
                isSystem -> Color(0xFF1E2430)
                isUser -> Color(0xFF2A3A52)
                else -> Color(0xFF1A2233)
            },
            border = if (active) {
                BorderStroke(1.dp, beat.mood.glowColor.copy(alpha = 0.55f))
            } else {
                null
            },
            modifier = Modifier.widthIn(max = 560.dp),
        ) {
            Text(
                text = beat.text,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = if (isSystem) 0.7f else 0.95f),
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
            )
        }
    }
}

/** Voice-plate facing label. */
val AssistantMood.voiceLabel: String
    get() = label
