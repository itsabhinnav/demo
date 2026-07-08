package com.test.design.presentation.ivi.media.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.QueueMusic
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.test.design.presentation.ivi.common.MorphingDetailSurfaceCard
import com.test.design.theme.CarDesignTokens
import com.test.design.theme.MediaCardPlayingRadii
import com.test.design.theme.MediaCardRestRadii
import com.test.design.theme.carTouchTarget

@Composable
fun MediaTransportControlsBar(
    isPlaying: Boolean,
    onToggleQueue: () -> Unit,
    onPrevious: () -> Unit,
    onTogglePlayback: () -> Unit,
    onNext: () -> Unit,
    modifier: Modifier = Modifier,
    compact: Boolean = false,
    showQueue: Boolean = true,
) {
    val iconSize = if (compact) 20.dp else CarDesignTokens.PrimaryIcon
    val spacing = if (compact) 4.dp else CarDesignTokens.TouchTargetSpacing

    MorphingDetailSurfaceCard(
        morphExpanded = isPlaying,
        compactRadii = MediaCardRestRadii,
        expandedRadii = MediaCardPlayingRadii,
        modifier = modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (showQueue) {
                IconButton(
                    onClick = onToggleQueue,
                    modifier = Modifier.carTouchTarget(),
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.QueueMusic,
                        contentDescription = "Show queue",
                        modifier = Modifier.size(iconSize),
                    )
                }
                Spacer(modifier = Modifier.width(spacing))
            }
            IconButton(
                onClick = onPrevious,
                modifier = Modifier.carTouchTarget(),
            ) {
                Icon(
                    imageVector = Icons.Default.SkipPrevious,
                    contentDescription = "Previous track",
                    modifier = Modifier.size(iconSize),
                )
            }
            Spacer(modifier = Modifier.width(spacing))
            MorphingPlayPauseButton(
                isPlaying = isPlaying,
                onClick = onTogglePlayback,
                compact = compact,
            )
            Spacer(modifier = Modifier.width(spacing))
            IconButton(
                onClick = onNext,
                modifier = Modifier.carTouchTarget(),
            ) {
                Icon(
                    imageVector = Icons.Default.SkipNext,
                    contentDescription = "Next track",
                    modifier = Modifier.size(iconSize),
                )
            }
        }
    }
}
