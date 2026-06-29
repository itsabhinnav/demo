package com.test.design.component.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.test.design.component.theme.OemCarAccent
import com.test.design.component.theme.OemSpacing

@Composable
fun CustomBadge(
    count: Int,
    modifier: Modifier = Modifier,
    maxCount: Int = 99,
) {
    Badge(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
    ) {
        Text(
            text = if (count > maxCount) "$maxCount+" else count.toString(),
            style = MaterialTheme.typography.labelMedium,
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
    BadgedBox(
        modifier = modifier,
        badge = { if (badgeCount > 0) CustomBadge(count = badgeCount) },
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.size(OemSpacing.lg),
        )
    }
}

@Composable
fun CustomDivider(modifier: Modifier = Modifier) {
    HorizontalDivider(
        modifier = modifier,
        thickness = 1.dp,
        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.35f),
    )
}

enum class StatusLevel { Normal, Warning, Critical, Info }

@Composable
fun CustomStatusIndicator(
    label: String,
    level: StatusLevel,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(statusColor(level), CircleShape),
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(start = OemSpacing.sm),
        )
    }
}

@Composable
private fun statusColor(level: StatusLevel): Color = when (level) {
    StatusLevel.Normal -> Color(0xFF4CAF50)
    StatusLevel.Warning -> Color(0xFFFFB300)
    StatusLevel.Critical -> MaterialTheme.colorScheme.error
    StatusLevel.Info -> OemCarAccent
}
