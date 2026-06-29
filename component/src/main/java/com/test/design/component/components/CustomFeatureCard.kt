package com.test.design.component.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import com.test.design.component.theme.OemOnSurface
import com.test.design.component.theme.OemOnSurfaceVariant
import com.test.design.component.theme.OemSpacing
import com.test.design.component.theme.OemSurfaceVariant
import com.test.design.component.theme.OemVisuals

@Composable
fun CustomFeatureCard(
    title: String,
    description: String,
    icon: ImageVector,
    categoryLabel: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    CustomCard(
        modifier = modifier,
        style = CardStyle.Filled,
        onClick = onClick,
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top,
        ) {
            Box(
                modifier = Modifier
                    .size(OemSpacing.xl + OemSpacing.sm)
                    .clip(OemVisuals.iconContainerShape)
                    .background(OemSurfaceVariant),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = OemOnSurface,
                    modifier = Modifier.size(OemSpacing.lg),
                )
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = OemSpacing.md),
            ) {
                Text(
                    text = categoryLabel,
                    style = MaterialTheme.typography.labelMedium,
                    color = OemOnSurfaceVariant,
                )
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge,
                    color = OemOnSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = OemSpacing.xs),
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = OemOnSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = OemSpacing.xs),
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = OemOnSurfaceVariant,
                modifier = Modifier
                    .align(Alignment.CenterVertically)
                    .size(OemSpacing.lg),
            )
        }
    }
}
