package com.test.design.component.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.test.design.component.theme.OemError
import com.test.design.component.theme.OemGray
import com.test.design.component.theme.OemGrayLight
import com.test.design.component.theme.OemInfo
import com.test.design.component.theme.OemOnPrimary
import com.test.design.component.theme.OemOutline
import com.test.design.component.theme.OemPrimary
import com.test.design.component.theme.OemSpacing
import com.test.design.component.theme.OemSuccess
import com.test.design.component.theme.OemVisuals
import com.test.design.component.theme.OemWarning
import com.test.design.component.theme.oemSurfaceBorder

@Composable
fun CustomBadge(
    count: Int,
    modifier: Modifier = Modifier,
    maxCount: Int = 99,
) {
    val shape = OemVisuals.chipShape
    Box(
        modifier = modifier
            .clip(shape)
            .background(OemPrimary)
            .oemSurfaceBorder(shape, OemOutline)
            .padding(horizontal = OemSpacing.sm, vertical = OemSpacing.xs),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = if (count > maxCount) "$maxCount+" else count.toString(),
            style = MaterialTheme.typography.labelMedium,
            color = OemOnPrimary,
        )
    }
}

@Composable
fun CustomBadgedIcon(
    icon: ImageVector,
    contentDescription: String?,
    badgeCount: Int,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.size(OemSpacing.lg),
        )
        if (badgeCount > 0) {
            CustomBadge(
                count = badgeCount,
                modifier = Modifier.align(Alignment.TopEnd),
            )
        }
    }
}

@Composable
fun CustomDivider(modifier: Modifier = Modifier) {
    HorizontalDivider(
        modifier = modifier,
        thickness = 1.dp,
        color = OemOutline.copy(alpha = 0.5f),
    )
}

enum class StatusLevel { Normal, Warning, Critical, Info }

@Composable
fun CustomStatusIndicator(
    label: String,
    level: StatusLevel,
    modifier: Modifier = Modifier,
) {
    val shape = OemVisuals.chipShape
    Row(
        modifier = modifier
            .clip(shape)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .oemSurfaceBorder(shape)
            .padding(horizontal = OemSpacing.md, vertical = OemSpacing.sm),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(OemVisuals.iconContainerShape)
                .background(statusColor(level)),
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(start = OemSpacing.sm),
        )
    }
}

@Composable
private fun statusColor(level: StatusLevel): Color = when (level) {
    StatusLevel.Normal -> OemSuccess
    StatusLevel.Warning -> OemWarning
    StatusLevel.Critical -> OemError
    StatusLevel.Info -> OemInfo
}
