package com.test.design.component.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.test.design.component.core.oemTouchTarget
import com.test.design.component.motion.oemInteractiveMotion
import com.test.design.component.theme.OemBorder
import com.test.design.component.theme.OemOnPrimary
import com.test.design.component.theme.OemOnSurface
import com.test.design.component.theme.OemPrimary
import com.test.design.component.theme.OemSurfaceVariant
import com.test.design.component.theme.OemVisuals

enum class IconButtonStyle { Standard, Filled, Tonal }

@Composable
fun CustomIconButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: IconButtonStyle = IconButtonStyle.Standard,
    enabled: Boolean = true,
) {
    val shape = OemVisuals.iconButtonShape
    val interactionSource = remember { MutableInteractionSource() }

    val backgroundModifier = when (style) {
        IconButtonStyle.Standard -> Modifier
            .background(Color.Transparent)
            .border(1.dp, OemBorder, shape)
        IconButtonStyle.Filled -> Modifier.background(OemPrimary)
        IconButtonStyle.Tonal -> Modifier
            .background(OemSurfaceVariant)
            .border(1.dp, OemBorder, shape)
    }

    val tint = when (style) {
        IconButtonStyle.Filled -> OemOnPrimary
        else -> OemOnSurface
    }

    Box(
        modifier = modifier
            .oemTouchTarget()
            .oemInteractiveMotion(interactionSource, enabled)
            .clip(shape)
            .then(backgroundModifier)
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
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = if (enabled) tint else tint.copy(alpha = 0.4f),
        )
    }
}
