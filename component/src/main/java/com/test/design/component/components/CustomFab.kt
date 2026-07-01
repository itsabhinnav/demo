package com.test.design.component.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import com.test.design.component.motion.oemInteractiveMotion
import com.test.design.component.theme.OemOnPrimary
import com.test.design.component.theme.OemPrimary
import com.test.design.component.theme.OemSpacing
import com.test.design.component.theme.OemVisuals

enum class FabSize { Standard, Large }

@Composable
fun CustomFab(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: FabSize = FabSize.Standard,
) {
    val fabSize = when (size) {
        FabSize.Standard -> OemSpacing.minTouchTarget
        FabSize.Large -> OemSpacing.minTouchTarget + OemSpacing.md
    }
    val shape = OemVisuals.fabShape
    val interactionSource = remember { MutableInteractionSource() }

    Box(
        modifier = modifier
            .size(fabSize)
            .oemInteractiveMotion(interactionSource)
            .clip(shape)
            .background(OemPrimary)
            .oemTouchTarget()
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = OemOnPrimary,
            modifier = Modifier.size(OemSpacing.lg),
        )
    }
}

@Composable
fun CustomExtendedFab(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = OemVisuals.fabShape
    val interactionSource = remember { MutableInteractionSource() }

    Row(
        modifier = modifier
            .oemInteractiveMotion(interactionSource)
            .clip(shape)
            .background(OemPrimary)
            .oemTouchTarget()
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick,
            )
            .padding(horizontal = OemSpacing.lg, vertical = OemSpacing.md),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(icon, contentDescription = null, tint = OemOnPrimary)
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            color = OemOnPrimary,
            modifier = Modifier.padding(start = OemSpacing.sm),
        )
    }
}
