package com.test.design.presentation.ivi.media.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.test.design.theme.CarDesignTokens

@Composable
fun MediaAlbumArt(
    album: String,
    albumShape: Shape,
    modifier: Modifier = Modifier,
    compact: Boolean = false,
) {
    Box(
        modifier = modifier
            .then(
                if (compact) {
                    Modifier.size(48.dp)
                } else {
                    Modifier
                        .fillMaxHeight(0.9f)
                },
            )
            .clip(albumShape)
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
            text = if (compact) album.take(2).uppercase() else album,
            style = if (compact) MaterialTheme.typography.labelLarge else MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = if (compact) Modifier else Modifier.padding(CarDesignTokens.TouchTargetSpacing),
            maxLines = if (compact) 1 else 3,
            overflow = TextOverflow.Ellipsis,
        )
    }
}
