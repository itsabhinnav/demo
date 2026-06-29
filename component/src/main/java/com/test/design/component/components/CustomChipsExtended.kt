package com.test.design.component.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import com.test.design.component.core.oemTouchTarget
import com.test.design.component.theme.OemBorder
import com.test.design.component.theme.OemOnSurfaceVariant
import com.test.design.component.theme.OemRed
import com.test.design.component.theme.OemSpacing
import com.test.design.component.theme.OemSurfaceElevated
import com.test.design.component.theme.OemVisuals
import com.test.design.component.theme.oemSurfaceBorder
import androidx.compose.ui.unit.dp

@Composable
fun CustomAssistChip(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: ImageVector? = null,
) {
    val shape = OemVisuals.chipShape
    val interactionSource = remember { MutableInteractionSource() }
    Row(
        modifier = modifier
            .oemTouchTarget()
            .clip(shape)
            .background(OemSurfaceElevated)
            .oemSurfaceBorder(shape, OemBorder)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick,
            )
            .padding(horizontal = OemSpacing.md, vertical = OemSpacing.sm),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(OemSpacing.sm),
    ) {
        leadingIcon?.let {
            Icon(it, contentDescription = null, tint = OemRed, modifier = Modifier.size(OemSpacing.lg))
        }
        Text(label.uppercase(), style = MaterialTheme.typography.labelLarge, color = OemRed)
    }
}

@Composable
fun CustomSuggestionChip(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    CustomChip(label = label, selected = false, onClick = onClick, modifier = modifier)
}

@Composable
fun CustomInputChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    CustomChip(label = label, selected = selected, onClick = onClick, modifier = modifier)
}

@Composable
fun CustomListTile(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    leadingIcon: ImageVector? = null,
    showChevron: Boolean = true,
    onClick: (() -> Unit)? = null,
) {
    val shape = OemVisuals.cardShape
    val interactionSource = remember { MutableInteractionSource() }
    val clickableModifier = if (onClick != null) {
        Modifier.clickable(
            interactionSource = interactionSource,
            indication = null,
            onClick = onClick,
        )
    } else {
        Modifier
    }

    Column(modifier = modifier.fillMaxWidth().padding(vertical = OemSpacing.xs)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(OemSpacing.listItemHeight)
                .clip(shape)
                .background(OemSurfaceElevated)
                .oemSurfaceBorder(shape, OemBorder)
                .then(clickableModifier)
                .padding(horizontal = OemSpacing.md, vertical = OemSpacing.sm),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (leadingIcon != null) {
                Box(
                    modifier = Modifier
                        .size(OemSpacing.xl)
                        .clip(OemVisuals.iconContainerShape)
                        .background(OemVisuals.iconGradient),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        imageVector = leadingIcon,
                        contentDescription = null,
                        tint = OemRed,
                        modifier = Modifier.size(OemSpacing.lg),
                    )
                }
            }
            CustomListItemRow(
                title = title,
                subtitle = subtitle,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = if (leadingIcon != null) OemSpacing.md else 0.dp),
            )
            if (showChevron && onClick != null) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = OemRed.copy(alpha = 0.7f),
                )
            }
        }
    }
}
