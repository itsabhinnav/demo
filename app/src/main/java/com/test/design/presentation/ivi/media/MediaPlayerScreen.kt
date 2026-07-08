package com.test.design.presentation.ivi.media

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.animateDpAsState
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.QueueMusic
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOne
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.test.design.presentation.common.ScreenBackground
import com.test.design.presentation.ivi.common.MorphingDetailSurfaceCard
import com.test.design.presentation.ivi.common.WidgetScreenHeader
import com.test.design.presentation.ivi.dashboard.model.DashboardWidget
import com.test.design.presentation.ivi.dashboard.widgetContainerTransform
import com.test.design.presentation.ivi.dashboard.widgetContentSharedElement
import com.test.design.presentation.ivi.dashboard.widgetControlsSharedElement
import com.test.design.presentation.ivi.media.components.AnimatedTrackInfo
import com.test.design.presentation.ivi.media.components.MediaAlbumArt
import com.test.design.presentation.ivi.media.components.MediaQueueSidePanel
import com.test.design.presentation.ivi.media.components.MediaSourceChips
import com.test.design.presentation.ivi.media.components.MediaTransportControlsBar
import com.test.design.presentation.ivi.media.components.PlaybackProgressSection
import com.test.design.presentation.ivi.media.components.RepeatModeLabel
import com.test.design.theme.CarBackgroundTokens
import com.test.design.theme.CarDesignTokens
import com.test.design.theme.MediaCardPlayingRadii
import com.test.design.theme.MediaCardRestRadii
import com.test.design.theme.rememberMediaAlbumShape

private val QueuePanelWidth = 400.dp

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.MediaPlayerScreen(
    uiState: MediaUiState,
    onEvent: (MediaEvent) -> Unit,
    onBack: () -> Unit,
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier,
) {
    val spatialSpec = MaterialTheme.motionScheme.slowSpatialSpec<androidx.compose.ui.unit.Dp>()
    val queueWidth by animateDpAsState(
        targetValue = if (uiState.isQueueVisible) QueuePanelWidth else 0.dp,
        animationSpec = spatialSpec,
        label = "queue_panel_width",
    )
    val albumShape = rememberMediaAlbumShape(playing = uiState.isPlaying)

    Box(
        modifier = widgetContainerTransform(
            widget = DashboardWidget.Media,
            animatedVisibilityScope = animatedVisibilityScope,
            modifier = modifier.fillMaxSize(),
        ),
    ) {
        ScreenBackground()
        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    MaterialTheme.colorScheme.surfaceContainerLow.copy(
                        alpha = CarBackgroundTokens.MediaOverlayAlpha,
                    ),
                )
                .padding(CarDesignTokens.ContentPadding),
            horizontalArrangement = Arrangement.spacedBy(CarDesignTokens.SectionSpacing),
        ) {
            MediaNowPlayingPanel(
                uiState = uiState,
                onEvent = onEvent,
                onBack = onBack,
                animatedVisibilityScope = animatedVisibilityScope,
                albumShape = albumShape,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
            )

            if (queueWidth > 0.dp) {
                MediaQueueSidePanel(
                    queue = uiState.queue,
                    currentTrackId = uiState.currentTrack.id,
                    onSelectTrack = { onEvent(MediaEvent.SelectTrack(it)) },
                    onClose = { onEvent(MediaEvent.ToggleQueue) },
                    modifier = Modifier
                        .width(queueWidth)
                        .fillMaxHeight(),
                )
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun SharedTransitionScope.MediaNowPlayingPanel(
    uiState: MediaUiState,
    onEvent: (MediaEvent) -> Unit,
    onBack: () -> Unit,
    animatedVisibilityScope: AnimatedVisibilityScope,
    albumShape: Shape,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(CarDesignTokens.SectionSpacing),
    ) {
        WidgetScreenHeader(
            widget = DashboardWidget.Media,
            onBack = onBack,
            animatedVisibilityScope = animatedVisibilityScope,
        )

        MediaSourceChips(
            selected = uiState.source,
            onSelected = { onEvent(MediaEvent.SelectSource(it)) },
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(CarDesignTokens.SectionSpacing),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            MediaAlbumArt(
                album = uiState.currentTrack.album,
                albumShape = albumShape,
                modifier = Modifier
                    .weight(0.36f)
                    .sharedElement(
                        rememberSharedContentState(key = "album_art"),
                        animatedVisibilityScope = animatedVisibilityScope,
                    ),
            )

            Column(
                modifier = Modifier
                    .weight(0.64f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.Center,
            ) {
                AnimatedTrackInfo(
                    track = uiState.currentTrack,
                    modifier = widgetContentSharedElement(
                        widget = DashboardWidget.Media,
                        animatedVisibilityScope = animatedVisibilityScope,
                    ),
                )
                Spacer(modifier = Modifier.height(CarDesignTokens.SectionSpacing))
                PlaybackProgressSection(
                    progress = uiState.progress,
                    elapsedLabel = uiState.elapsedLabel,
                    durationLabel = uiState.currentTrack.durationLabel,
                    progressShape = albumShape,
                )
                Spacer(modifier = Modifier.height(CarDesignTokens.TouchTargetSpacing))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(CarDesignTokens.TouchTargetSpacing),
                ) {
                    FilterChip(
                        selected = uiState.isShuffleOn,
                        onClick = { onEvent(MediaEvent.ToggleShuffle) },
                        label = { Text("Shuffle", style = MaterialTheme.typography.labelLarge) },
                        leadingIcon = {
                            Icon(Icons.Default.Shuffle, contentDescription = null, modifier = Modifier.size(20.dp))
                        },
                    )
                    FilterChip(
                        selected = uiState.repeatMode != RepeatMode.Off,
                        onClick = { onEvent(MediaEvent.CycleRepeat) },
                        label = { Text(RepeatModeLabel(uiState.repeatMode), style = MaterialTheme.typography.labelLarge) },
                        leadingIcon = {
                            Icon(
                                imageVector = if (uiState.repeatMode == RepeatMode.One) Icons.Default.RepeatOne else Icons.Default.Repeat,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp),
                            )
                        },
                    )
                }
                Spacer(modifier = Modifier.height(CarDesignTokens.TouchTargetSpacing))
                MediaTransportControlsBar(
                    isPlaying = uiState.isPlaying,
                    onToggleQueue = { onEvent(MediaEvent.ToggleQueue) },
                    onPrevious = { onEvent(MediaEvent.PreviousTrack) },
                    onTogglePlayback = { onEvent(MediaEvent.TogglePlayback) },
                    onNext = { onEvent(MediaEvent.NextTrack) },
                    modifier = widgetControlsSharedElement(
                        widget = DashboardWidget.Media,
                        animatedVisibilityScope = animatedVisibilityScope,
                        modifier = Modifier.fillMaxWidth(),
                    ),
                )
            }
        }
    }
}
