package com.test.design.presentation.ivi.media

import com.test.design.core.mvi.MviViewModel

class MediaViewModel : MviViewModel<MediaUiState, MediaEvent>(MediaUiState()) {

    override fun onEvent(event: MediaEvent) {
        when (event) {
            MediaEvent.TogglePlayback -> setState { copy(isPlaying = !isPlaying) }
            MediaEvent.ToggleQueue -> setState { copy(isQueueVisible = !isQueueVisible) }
            MediaEvent.NextTrack -> setState {
                val index = queue.indexOfFirst { it.id == currentTrack.id }
                val next = queue.getOrNull((index + 1).coerceAtMost(queue.lastIndex)) ?: currentTrack
                copy(currentTrack = next, isPlaying = true, progress = 0f)
            }
            MediaEvent.PreviousTrack -> setState {
                val index = queue.indexOfFirst { it.id == currentTrack.id }
                val previous = queue.getOrNull((index - 1).coerceAtLeast(0)) ?: currentTrack
                copy(currentTrack = previous, isPlaying = true, progress = 0f)
            }
            is MediaEvent.SelectTrack -> setState {
                copy(currentTrack = event.track, isPlaying = true, progress = 0f, isQueueVisible = false)
            }
        }
    }
}
