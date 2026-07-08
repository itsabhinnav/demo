package com.test.design.presentation.ivi.media.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.test.design.presentation.ivi.media.Track
import com.test.design.theme.CarDesignTokens
import com.test.design.theme.ExpressiveShapes
import com.test.design.theme.carListItemHeight
import com.test.design.theme.carTouchTarget

@Composable
fun MediaQueueSidePanel(
    queue: List<Track>,
    currentTrackId: String,
    onSelectTrack: (Track) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxHeight(),
        shape = ExpressiveShapes.large,
        color = MaterialTheme.colorScheme.surfaceContainerHigh,
        tonalElevation = 4.dp,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(CarDesignTokens.ContentPadding),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Up Next",
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurface,
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
                                MaterialTheme.colorScheme.surfaceContainerHigh
                            },
                        ),
                    )
                    HorizontalDivider()
                }
            }
        }
    }
}
