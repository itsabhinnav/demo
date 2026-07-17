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
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

/**
 * Side persona rail + dialogue simulation that cycles moods as the chat plays.
 */
@Composable
fun AssistantDialogueStage(
    mood: AssistantMood,
    onMoodChange: (AssistantMood) -> Unit,
    modifier: Modifier = Modifier,
    autoPlay: Boolean = true,
) {
    var beatIndex by remember { mutableIntStateOf(0) }
    var playing by remember { mutableStateOf(autoPlay) }
    var visibleCount by remember { mutableIntStateOf(1) }
    val listState = rememberLazyListState()

    val activeBeat = DemoDialogueScript.getOrNull(beatIndex.coerceIn(0, DemoDialogueScript.lastIndex))
        ?: DemoDialogueScript.first()

    LaunchedEffect(beatIndex, playing) {
        if (!playing) return@LaunchedEffect
        val beat = DemoDialogueScript.getOrNull(beatIndex) ?: return@LaunchedEffect
        onMoodChange(beat.mood)
        visibleCount = (beatIndex + 1).coerceAtMost(DemoDialogueScript.size)
        delay(beat.holdMs)
        if (beatIndex < DemoDialogueScript.lastIndex) {
            beatIndex += 1
        } else {
            playing = false
        }
    }

    LaunchedEffect(visibleCount) {
        if (visibleCount > 0) {
            listState.animateScrollToItem((visibleCount - 1).coerceAtLeast(0))
        }
    }

    Row(
        modifier = modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        DialoguePanel(
            beats = DemoDialogueScript.take(visibleCount),
            activeIndex = beatIndex,
            playing = playing,
            onPlayPause = {
                if (!playing && beatIndex >= DemoDialogueScript.lastIndex) {
                    beatIndex = 0
                    visibleCount = 1
                }
                playing = !playing
            },
            onReplay = {
                beatIndex = 0
                visibleCount = 1
                playing = true
            },
            listState = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
        )

        AssistantSideRail(
            mood = mood,
            caption = activeBeat.let { beat ->
                when (beat.speaker) {
                    DialogueSpeaker.System -> beat.text
                    else -> mood.caption
                }
            },
            modifier = Modifier
                .width(280.dp)
                .fillMaxHeight(),
        )
    }
}

@Composable
private fun DialoguePanel(
    beats: List<DialogueBeat>,
    activeIndex: Int,
    playing: Boolean,
    onPlayPause: () -> Unit,
    onReplay: () -> Unit,
    listState: androidx.compose.foundation.lazy.LazyListState,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(28.dp),
        color = Color(0xE612151E),
        shadowElevation = 10.dp,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF161A24), Color(0xFF0A0C12)),
                    ),
                )
                .padding(18.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Column {
                    Text(
                        text = "Conversation",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        text = "Simulated dialogue · all moods",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.55f),
                    )
                }
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    FilledTonalIconButton(onClick = onPlayPause) {
                        Icon(
                            imageVector = if (playing) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (playing) "Pause" else "Play",
                        )
                    }
                    FilledTonalIconButton(onClick = onReplay) {
                        Icon(
                            imageVector = Icons.Default.Replay,
                            contentDescription = "Replay",
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp),
                contentPadding = PaddingValues(bottom = 8.dp),
            ) {
                items(
                    count = beats.size,
                    key = { index -> "beat_$index" },
                ) { index ->
                    val beat = beats[index]
                    DialogueBubble(
                        beat = beat,
                        active = index == activeIndex,
                    )
                }
            }
        }
    }
}

@Composable
private fun DialogueBubble(
    beat: DialogueBeat,
    active: Boolean,
) {
    val isUser = beat.speaker == DialogueSpeaker.User
    val isSystem = beat.speaker == DialogueSpeaker.System
    val align = when {
        isSystem -> Alignment.CenterHorizontally
        isUser -> Alignment.End
        else -> Alignment.Start
    }
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = align,
    ) {
        if (!isSystem) {
            Text(
                text = if (isUser) "You" else "Assistant · ${beat.mood.voiceLabel}",
                style = MaterialTheme.typography.labelMedium,
                color = beat.mood.glowColor.copy(alpha = 0.9f),
                modifier = Modifier.padding(bottom = 4.dp, start = 4.dp, end = 4.dp),
            )
        }
        Surface(
            shape = RoundedCornerShape(
                topStart = 18.dp,
                topEnd = 18.dp,
                bottomStart = if (isUser) 18.dp else 6.dp,
                bottomEnd = if (isUser) 6.dp else 18.dp,
            ),
            color = when {
                isSystem -> Color(0xFF1E2430)
                isUser -> Color(0xFF2A3A52)
                else -> Color(0xFF1A2233)
            },
            border = if (active) {
                androidx.compose.foundation.BorderStroke(1.dp, beat.mood.glowColor.copy(alpha = 0.55f))
            } else {
                null
            },
            modifier = Modifier.widthIn(max = 520.dp),
        ) {
            Text(
                text = beat.text,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White.copy(alpha = if (isSystem) 0.7f else 0.95f),
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            )
        }
    }
}

@Composable
fun AssistantSideRail(
    mood: AssistantMood,
    caption: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(28.dp),
        color = Color(0xE60A0C12),
        shadowElevation = 12.dp,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF141822), Color(0xFF080A10)),
                    ),
                )
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Persona",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.White.copy(alpha = 0.5f),
                )
                Spacer(Modifier.height(8.dp))
                AssistantFace(
                    mood = mood,
                    modifier = Modifier.size(168.dp),
                )
            }

            VoiceWaveform(
                mood = mood,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp),
            )

            AnimatedContent(
                targetState = mood to caption,
                transitionSpec = { fadeIn(tween(260)) togetherWith fadeOut(tween(160)) },
                label = "side_caption",
            ) { (currentMood, line) ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = currentMood.voiceLabel,
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White,
                        textAlign = TextAlign.Center,
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = line,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.6f),
                        textAlign = TextAlign.Center,
                        maxLines = 3,
                    )
                }
            }
        }
    }
}
