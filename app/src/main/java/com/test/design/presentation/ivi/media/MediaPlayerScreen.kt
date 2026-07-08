package com.test.design.presentation.ivi.media

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.QueueMusic
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOne
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.test.design.presentation.ivi.media.components.MediaSourceChips
import com.test.design.presentation.ivi.media.components.MorphingPlayPauseButton
import com.test.design.presentation.ivi.media.components.PlaybackProgressSection
import com.test.design.presentation.ivi.media.components.RepeatModeLabel
import com.test.design.theme.CarDesignTokens
import com.test.design.theme.MediaAlbumShape
import com.test.design.theme.WidgetCardShape
import com.test.design.theme.carListItemHeight
import com.test.design.theme.carTouchTarget

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.MediaPlayerScreen(
    uiState: MediaUiState,
    onEvent: (MediaEvent) -> Unit,
    onBack: () -> Unit,
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier,
) {
    val motionSpec = MaterialTheme.motionScheme.defaultSpatialSpec<Float>()

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
        AnimatedContent(
            targetState = uiState.isQueueVisible,
            modifier = Modifier.fillMaxSize(),
            transitionSpec = {
                if (targetState) {
                    (scaleIn(animationSpec = motionSpec, initialScale = 0.92f) + fadeIn(animationSpec = motionSpec))
                        .togetherWith(
                            scaleOut(animationSpec = motionSpec, targetScale = 1.04f) +
                                fadeOut(animationSpec = motionSpec),
                        )
                } else {
                    (scaleIn(animationSpec = motionSpec, initialScale = 1.04f) + fadeIn(animationSpec = motionSpec))
                        .togetherWith(
                            scaleOut(animationSpec = motionSpec, targetScale = 0.92f) +
                                fadeOut(animationSpec = motionSpec),
                        )
                }
            },
            label = "media_z_axis_queue",
        ) { queueVisible ->
            if (queueVisible) {
                MediaQueuePanel(
                    queue = uiState.queue,
                    currentTrackId = uiState.currentTrack.id,
                    onSelectTrack = { onEvent(MediaEvent.SelectTrack(it)) },
                    onClose = { onEvent(MediaEvent.ToggleQueue) },
                )
            } else {
                MediaNowPlayingPanel(
                    uiState = uiState,
                    onEvent = onEvent,
                    onBack = onBack,
                    animatedVisibilityScope = animatedVisibilityScope,
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
) {
    Column(
        modifier = Modifier.fillMaxSize(),
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

@Composable
private fun MediaQueuePanel(
    queue: List<Track>,
    currentTrackId: String,
    onSelectTrack: (Track) -> Unit,
    onClose: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = "Up Next",
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Text(
                text = "Close",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .carTouchTarget()
                    .clickable(onClick = onClose)
                    .padding(horizontal = CarDesignTokens.TouchTargetSpacing),
            )
        }
        Spacer(modifier = Modifier.height(CarDesignTokens.TouchTargetSpacing))
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            items(queue, key = { it.id }) { track ->
                val isCurrent = track.id == currentTrackId
                ListItem(
                    modifier = Modifier
                        .carListItemHeight()
                        .clickable { onSelectTrack(track) },
                    headlineContent = {
                        Text(
                            text = track.title,
                            style = MaterialTheme.typography.bodyLarge,
                            color = if (isCurrent) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onSurface
                            },
                        )
                    },
                    supportingContent = {
                        Text(
                            text = "${track.artist} · ${track.durationLabel}",
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    },
                    colors = ListItemDefaults.colors(
                        containerColor = if (isCurrent) {
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
                        } else {
                            MaterialTheme.colorScheme.surface
                        },
                    ),
                )
                HorizontalDivider()
            }
        }
    }
}
