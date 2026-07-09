package com.test.design.presentation.ivi.climate.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.test.design.theme.CarDesignTokens
import com.test.design.theme.carTouchTarget

@Composable
fun ClimateTemperatureSection(
    temperature: Int,
    isAcEnabled: Boolean,
    dialShape: Shape,
    onDecrease: () -> Unit,
    onIncrease: () -> Unit,
    modifier: Modifier = Modifier,
    compact: Boolean = false,
    contentModifier: Modifier = Modifier,
) {
    val dialSize = if (compact) 72.dp else 240.dp
    val buttonSize = if (compact) 36.dp else CarDesignTokens.MinTouchTarget
    val iconSize = if (compact) 18.dp else CarDesignTokens.PrimaryIcon
    val spacing = if (compact) 4.dp else CarDesignTokens.TouchTargetSpacing

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(spacing, Alignment.CenterHorizontally),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TemperatureAdjustButton(
            icon = Icons.Default.Remove,
            contentDescription = "Decrease temperature",
            onClick = onDecrease,
            size = buttonSize,
            iconSize = iconSize,
        )
        Box(
            modifier = contentModifier
                .size(dialSize)
                .clip(dialShape)
                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.65f)),
            contentAlignment = Alignment.Center,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                AnimatedTemperatureCounter(
                    temperature = temperature,
                    compact = compact,
                )
                if (!compact) {
                    Text(
                        text = if (isAcEnabled) "A/C On" else "A/C Off",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                }
            }
        }
        TemperatureAdjustButton(
            icon = Icons.Default.Add,
            contentDescription = "Increase temperature",
            onClick = onIncrease,
            size = buttonSize,
            iconSize = iconSize,
        )
    }
}

@Composable
fun TemperatureAdjustButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    size: Dp = CarDesignTokens.MinTouchTarget,
    iconSize: Dp = CarDesignTokens.PrimaryIcon,
) {
    FilledIconButton(
        onClick = onClick,
        modifier = Modifier
            .size(size)
            .clip(CircleShape)
            .carTouchTarget(),
        colors = IconButtonDefaults.filledIconButtonColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        ),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            modifier = Modifier.size(iconSize),
        )
    }
}
