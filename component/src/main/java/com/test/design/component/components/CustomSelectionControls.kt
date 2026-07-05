package com.test.design.component.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.test.design.component.theme.OemBorder
import com.test.design.component.theme.OemOnPrimary
import com.test.design.component.theme.OemOnSurface
import com.test.design.component.theme.OemOnSurfaceVariant
import com.test.design.component.theme.OemPrimary
import com.test.design.component.theme.OemSpacing
import com.test.design.component.theme.OemSurfaceVariant
import com.test.design.component.theme.OemVisuals

private val controlSize = 32.dp
private val switchTrackWidth = 60.dp
private val switchTrackHeight = 36.dp
private val switchThumbSize = 28.dp

@Composable
fun CustomSwitch(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(OemSpacing.listItemHeight)
            .toggleable(
                value = checked,
                role = Role.Switch,
                enabled = enabled,
                onValueChange = onCheckedChange,
            )
            .padding(horizontal = OemSpacing.sm),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = if (enabled) OemOnSurface else OemOnSurfaceVariant.copy(alpha = 0.4f),
        )
        OemSwitchControl(checked = checked, enabled = enabled)
    }
}

@Composable
private fun OemSwitchControl(
    checked: Boolean,
    enabled: Boolean,
    modifier: Modifier = Modifier,
) {
    val shape = OemVisuals.chipShape
    val thumbOffset by animateFloatAsState(
        targetValue = if (checked) 1f else 0f,
        label = "oemSwitchThumb",
    )
    val trackColor = when {
        !enabled -> OemSurfaceVariant.copy(alpha = 0.4f)
        checked -> OemPrimary
        else -> OemSurfaceVariant
    }
    val borderColor = when {
        !enabled -> OemBorder.copy(alpha = 0.4f)
        checked -> OemPrimary
        else -> OemBorder
    }

    Box(
        modifier = modifier
            .size(width = switchTrackWidth, height = switchTrackHeight)
            .clip(shape)
            .background(trackColor)
            .border(1.dp, borderColor, shape)
            .padding(OemSpacing.xs),
    ) {
        Box(
            modifier = Modifier
                .size(switchThumbSize)
                .offset(x = (switchTrackWidth - switchThumbSize - OemSpacing.sm * 2) * thumbOffset)
                .clip(OemVisuals.iconContainerShape)
                .background(if (checked) OemOnPrimary else OemOnSurface),
        )
    }
}

@Composable
fun CustomCheckbox(
    label: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(OemSpacing.listItemHeight)
            .toggleable(
                value = checked,
                role = Role.Checkbox,
                enabled = enabled,
                onValueChange = onCheckedChange,
            )
            .padding(horizontal = OemSpacing.sm),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        OemCheckboxControl(checked = checked, enabled = enabled)
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = if (enabled) OemOnSurface else OemOnSurfaceVariant.copy(alpha = 0.4f),
            modifier = Modifier.padding(start = OemSpacing.sm),
        )
    }
}

@Composable
private fun OemCheckboxControl(
    checked: Boolean,
    enabled: Boolean,
    modifier: Modifier = Modifier,
) {
    val shape = OemVisuals.iconContainerShape
    Box(
        modifier = modifier
            .size(controlSize)
            .clip(shape)
            .background(
                when {
                    !enabled && checked -> OemPrimary.copy(alpha = 0.4f)
                    checked -> OemPrimary
                    else -> OemSurfaceVariant
                },
            )
            .border(
                width = 2.dp,
                color = when {
                    !enabled -> OemBorder.copy(alpha = 0.4f)
                    checked -> OemPrimary
                    else -> OemBorder
                },
                shape = shape,
            ),
        contentAlignment = Alignment.Center,
    ) {
        if (checked) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = null,
                tint = OemOnPrimary,
                modifier = Modifier.size(OemSpacing.lg),
            )
        }
    }
}

@Composable
fun CustomRadioButton(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(OemSpacing.listItemHeight)
            .toggleable(
                value = selected,
                role = Role.RadioButton,
                enabled = enabled,
                onValueChange = { if (!selected) onClick() },
            )
            .padding(horizontal = OemSpacing.sm),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        OemRadioControl(selected = selected, enabled = enabled)
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = if (enabled) OemOnSurface else OemOnSurfaceVariant.copy(alpha = 0.4f),
            modifier = Modifier.padding(start = OemSpacing.sm),
        )
    }
}

@Composable
private fun OemRadioControl(
    selected: Boolean,
    enabled: Boolean,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(controlSize)
            .clip(CircleShape)
            .background(if (enabled) OemSurfaceVariant else OemSurfaceVariant.copy(alpha = 0.4f))
            .border(
                width = 2.dp,
                color = when {
                    !enabled -> OemBorder.copy(alpha = 0.4f)
                    selected -> OemPrimary
                    else -> OemBorder
                },
                shape = CircleShape,
            ),
        contentAlignment = Alignment.Center,
    ) {
        if (selected) {
            Box(
                modifier = Modifier
                    .size(14.dp)
                    .clip(CircleShape)
                    .background(if (enabled) OemPrimary else OemPrimary.copy(alpha = 0.4f)),
            )
        }
    }
}
