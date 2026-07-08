package com.test.design.presentation.ivi.media

data class Track(
    val id: String,
    val title: String,
    val artist: String,
    val album: String,
    val durationLabel: String,
)

data class MediaUiState(
    val currentTrack: Track = Track(
        id = "1",
        title = "Neon Horizon",
        artist = "Aurora Drive",
        album = "Synthwave Nights",
        durationLabel = "4:12",
    ),
    val queue: List<Track> = listOf(
        Track("1", "Neon Horizon", "Aurora Drive", "Synthwave Nights", "4:12"),
        Track("2", "Midnight Cruise", "Aurora Drive", "Synthwave Nights", "3:48"),
        Track("3", "Chrome Sunset", "Velvet Circuit", "Retro Future", "5:01"),
        Track("4", "Electric Drift", "Velvet Circuit", "Retro Future", "3:22"),
        Track("5", "Starlight Lane", "Nova Pulse", "Highway Dreams", "4:35"),
    ),
    val isPlaying: Boolean = true,
    val progress: Float = 0.42f,
    val isQueueVisible: Boolean = false,
)

sealed interface MediaEvent {
    data object TogglePlayback : MediaEvent
    data object ToggleQueue : MediaEvent
    data object NextTrack : MediaEvent
    data object PreviousTrack : MediaEvent
    data class SelectTrack(val track: Track) : MediaEvent
}
