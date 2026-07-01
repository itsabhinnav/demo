package com.test.design.component.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
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
import com.test.design.component.theme.OemOnSurface
import com.test.design.component.theme.OemOnSurfaceVariant
import com.test.design.component.theme.OemPrimary
import com.test.design.component.theme.OemSpacing
import com.test.design.component.theme.OemSurfaceVariant
import com.test.design.component.theme.OemVisuals
import com.test.design.component.motion.oemInteractiveMotion

@Composable
fun CustomChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val shape = OemVisuals.chipShape
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = modifier
            .defaultMinSize(minHeight = OemSpacing.minTouchTarget)
            .oemInteractiveMotion(interactionSource, enabled)
            .clip(shape)
            .then(
                when {
                    selected -> Modifier.background(OemPrimary)
                    else -> Modifier
                        .background(OemSurfaceVariant)
                        .border(1.dp, OemBorder, shape)
                },
            )
            .then(
                if (enabled) {
                    Modifier.clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        onClick = onClick,
                    )
                } else {
                    Modifier
                },
            ),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = when {
                !enabled -> OemOnSurfaceVariant.copy(alpha = 0.4f)
                selected -> OemOnPrimary
                else -> OemOnSurface
            },
            modifier = Modifier.padding(horizontal = OemSpacing.lg, vertical = OemSpacing.sm),
        )
    }
}
