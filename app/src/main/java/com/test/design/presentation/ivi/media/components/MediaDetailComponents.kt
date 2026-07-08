package com.test.design.presentation.ivi.media.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.FilterChip
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.test.design.presentation.ivi.media.MediaSource
import com.test.design.presentation.ivi.media.RepeatMode
import com.test.design.presentation.ivi.media.Track
import com.test.design.theme.CarDesignTokens
import com.test.design.theme.MediaAlbumShape
import com.test.design.theme.carTouchTarget

@Composable
fun AnimatedTrackInfo(
    track: Track,
    modifier: Modifier = Modifier,
) {
    val spatialSpec = MaterialTheme.motionScheme.defaultSpatialSpec<androidx.compose.ui.unit.IntOffset>()
    val effectsSpec = MaterialTheme.motionScheme.defaultEffectsSpec<Float>()
    AnimatedContent(
        targetState = track.id,
        modifier = modifier,
        transitionSpec = {
            slideInVertically(animationSpec = spatialSpec) { it / 2 } + fadeIn(animationSpec = effectsSpec) togetherWith
                slideOutVertically(animationSpec = spatialSpec) { -it / 2 } + fadeOut(animationSpec = effectsSpec)
        },
        label = "track_info",
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = track.title,
                style = MaterialTheme.typography.displaySmall,
                color = MaterialTheme.colorScheme.onBackground,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = track.artist,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = track.album,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
fun MediaSourceChips(
    selected: MediaSource,
    onSelected: (MediaSource) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(CarDesignTokens.TouchTargetSpacing),
    ) {
        items(MediaSource.entries, key = { it.name }) { source ->
            FilterChip(
                selected = selected == source,
                onClick = { onSelected(source) },
                modifier = Modifier
                    .carTouchTarget()
                    .height(CarDesignTokens.MinTouchTarget),
                label = {
                    Text(source.label, style = MaterialTheme.typography.labelLarge)
                },
            )
        }
    }
}

@Composable
fun PlaybackProgressSection(
    progress: Float,
    elapsedLabel: String,
    durationLabel: String,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .clip(MediaAlbumShape),
        )
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(elapsedLabel, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(durationLabel, style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun RepeatModeLabel(mode: RepeatMode): String = when (mode) {
    RepeatMode.Off -> "Repeat off"
    RepeatMode.All -> "Repeat all"
    RepeatMode.One -> "Repeat one"
}
