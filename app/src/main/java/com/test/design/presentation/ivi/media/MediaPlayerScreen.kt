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
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.test.design.presentation.ivi.common.DetailSurfaceCard
import com.test.design.presentation.ivi.common.WidgetScreenHeader
import com.test.design.presentation.ivi.dashboard.model.DashboardWidget
import com.test.design.presentation.ivi.media.components.AnimatedTrackInfo
import com.test.design.presentation.ivi.media.components.MediaQueueSidePanel
import com.test.design.presentation.ivi.media.components.MediaSourceChips
import com.test.design.presentation.ivi.media.components.MorphingPlayPauseButton
import com.test.design.presentation.ivi.media.components.PlaybackProgressSection
import com.test.design.presentation.ivi.media.components.RepeatModeLabel
import com.test.design.theme.CarDesignTokens
import com.test.design.theme.MediaAlbumShape
import com.test.design.theme.WidgetCardShape
import com.test.design.theme.carTouchTarget

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

    Box(
        modifier = modifier
            .fillMaxSize()
            .sharedBounds(
                rememberSharedContentState(key = DashboardWidget.Media.sharedElementKey),
                animatedVisibilityScope = animatedVisibilityScope,
                resizeMode = SharedTransitionScope.ResizeMode.RemeasureToBounds,
                clipInOverlayDuringTransition = OverlayClip(WidgetCardShape),
            )
            .background(MaterialTheme.colorScheme.background)
            .padding(CarDesignTokens.ContentPadding),
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(CarDesignTokens.SectionSpacing),
        ) {
            MediaNowPlayingPanel(
                uiState = uiState,
                onEvent = onEvent,
                onBack = onBack,
                animatedVisibilityScope = animatedVisibilityScope,
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
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(CarDesignTokens.SectionSpacing),
    ) {
        WidgetScreenHeader(title = "Media", onBack = onBack)

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
            Box(
                modifier = Modifier
                    .weight(0.36f)
                    .fillMaxHeight(0.9f)
                    .sharedElement(
                        rememberSharedContentState(key = "album_art"),
                        animatedVisibilityScope = animatedVisibilityScope,
                    )
                    .clip(MediaAlbumShape)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primaryContainer,
                                MaterialTheme.colorScheme.secondaryContainer,
                                MaterialTheme.colorScheme.tertiaryContainer,
                            ),
                        ),
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = uiState.currentTrack.album,
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(CarDesignTokens.TouchTargetSpacing),
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            Column(
                modifier = Modifier
                    .weight(0.64f)
                    .fillMaxHeight(),
                verticalArrangement = Arrangement.Center,
            ) {
                AnimatedTrackInfo(track = uiState.currentTrack)
                Spacer(modifier = Modifier.height(CarDesignTokens.SectionSpacing))
                PlaybackProgressSection(
                    progress = uiState.progress,
                    elapsedLabel = uiState.elapsedLabel,
                    durationLabel = uiState.currentTrack.durationLabel,
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
                DetailSurfaceCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        IconButton(
                            onClick = { onEvent(MediaEvent.ToggleQueue) },
                            modifier = Modifier.carTouchTarget(),
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.QueueMusic,
                                contentDescription = "Show queue",
                                modifier = Modifier.size(CarDesignTokens.PrimaryIcon),
                            )
                        }
                        Spacer(modifier = Modifier.width(CarDesignTokens.TouchTargetSpacing))
                        IconButton(
                            onClick = { onEvent(MediaEvent.PreviousTrack) },
                            modifier = Modifier.carTouchTarget(),
                        ) {
                            Icon(
                                imageVector = Icons.Default.SkipPrevious,
                                contentDescription = "Previous track",
                                modifier = Modifier.size(CarDesignTokens.PrimaryIcon),
                            )
                        }
                        Spacer(modifier = Modifier.width(CarDesignTokens.TouchTargetSpacing))
                        MorphingPlayPauseButton(
                            isPlaying = uiState.isPlaying,
                            onClick = { onEvent(MediaEvent.TogglePlayback) },
                        )
                        Spacer(modifier = Modifier.width(CarDesignTokens.TouchTargetSpacing))
                        IconButton(
                            onClick = { onEvent(MediaEvent.NextTrack) },
                            modifier = Modifier.carTouchTarget(),
                        ) {
                            Icon(
                                imageVector = Icons.Default.SkipNext,
                                contentDescription = "Next track",
                                modifier = Modifier.size(CarDesignTokens.PrimaryIcon),
                            )
                        }
                    }
                }
            }
        }
    }
}
