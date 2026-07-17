package com.test.design.presentation.assistant

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.delay

/**
 * Shared playback controller for the simulated dialogue script.
 */
class DialoguePlaybackState internal constructor(
    autoPlay: Boolean,
) {
    var beatIndex by mutableIntStateOf(0)
        internal set
    var playing by mutableStateOf(autoPlay)
        internal set
    var visibleCount by mutableIntStateOf(1)
        internal set

    val activeBeat: DialogueBeat
        get() = DemoDialogueScript.getOrNull(beatIndex.coerceIn(0, DemoDialogueScript.lastIndex))
            ?: DemoDialogueScript.first()

    val visibleBeats: List<DialogueBeat>
        get() = DemoDialogueScript.take(visibleCount)

    fun playPause() {
        if (!playing && beatIndex >= DemoDialogueScript.lastIndex) {
            beatIndex = 0
            visibleCount = 1
        }
        playing = !playing
    }

    fun replay() {
        beatIndex = 0
        visibleCount = 1
        playing = true
    }
}

@Composable
fun rememberDialoguePlayback(autoPlay: Boolean = true): DialoguePlaybackState {
    return remember { DialoguePlaybackState(autoPlay = autoPlay) }
}

@Composable
fun DialoguePlaybackEffects(
    playback: DialoguePlaybackState,
    onMoodChange: (AssistantMood) -> Unit,
    listState: LazyListState = rememberLazyListState(),
) {
    LaunchedEffect(playback.beatIndex, playback.playing) {
        if (!playback.playing) return@LaunchedEffect
        val beat = DemoDialogueScript.getOrNull(playback.beatIndex) ?: return@LaunchedEffect
        onMoodChange(beat.mood)
        playback.visibleCount = (playback.beatIndex + 1).coerceAtMost(DemoDialogueScript.size)
        delay(beat.holdMs)
        if (playback.beatIndex < DemoDialogueScript.lastIndex) {
            playback.beatIndex += 1
        } else {
            playback.playing = false
        }
    }

    LaunchedEffect(playback.visibleCount) {
        if (playback.visibleCount > 0) {
            listState.animateScrollToItem((playback.visibleCount - 1).coerceAtLeast(0))
        }
    }
}
