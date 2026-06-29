package com.test.design.component.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.test.design.component.theme.OemBorder
import com.test.design.component.theme.OemOnPrimary
import com.test.design.component.theme.OemOnSurfaceVariant
import com.test.design.component.theme.OemPrimary
import com.test.design.component.theme.OemSpacing
import com.test.design.component.theme.OemSurface
import com.test.design.component.theme.OemVisuals
import com.test.design.component.theme.oemSurfaceBorder

@Composable
fun CustomSegmentedButton(
    options: List<String>,
    selectedIndex: Int,
    onOptionSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val containerShape = OemVisuals.tabContainerShape
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(OemSpacing.minTouchTarget)
            .clip(containerShape)
            .background(OemSurface)
            .oemSurfaceBorder(containerShape, OemBorder)
            .padding(OemSpacing.xs)
            .selectableGroup(),
        horizontalArrangement = Arrangement.spacedBy(OemSpacing.xs),
    ) {
        options.forEachIndexed { index, label ->
            OemSegmentItem(
                label = label,
                selected = selectedIndex == index,
                onClick = { onOptionSelected(index) },
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun OemSegmentItem(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = OemVisuals.chipShape
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = modifier
            .height(OemSpacing.minTouchTarget - OemSpacing.sm)
            .clip(shape)
            .then(
                if (selected) Modifier.background(OemPrimary) else Modifier,
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = if (selected) OemOnPrimary else OemOnSurfaceVariant,
        )
    }
}

@Composable
fun CustomSegmentedButtonRow(
    options: List<String>,
    selectedIndex: Int,
    onOptionSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.padding(vertical = OemSpacing.sm),
        horizontalArrangement = Arrangement.spacedBy(OemSpacing.sm),
    ) {
        CustomSegmentedButton(
            options = options,
            selectedIndex = selectedIndex,
            onOptionSelected = onOptionSelected,
            modifier = Modifier.weight(1f),
        )
    }
}
