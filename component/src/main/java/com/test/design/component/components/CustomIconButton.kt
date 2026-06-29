package com.test.design.component.components

import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.test.design.component.core.oemTouchTarget

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
    val colors = when (style) {
        IconButtonStyle.Standard -> IconButtonDefaults.iconButtonColors()
        IconButtonStyle.Filled -> IconButtonDefaults.filledIconButtonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
        )
        IconButtonStyle.Tonal -> IconButtonDefaults.filledTonalIconButtonColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurface,
        )
    }

    when (style) {
        IconButtonStyle.Standard -> IconButton(
            onClick = onClick,
            modifier = modifier.oemTouchTarget(),
            enabled = enabled,
            colors = colors,
        ) {
            Icon(icon, contentDescription)
        }
        IconButtonStyle.Filled -> androidx.compose.material3.FilledIconButton(
            onClick = onClick,
            modifier = modifier.oemTouchTarget(),
            enabled = enabled,
            colors = colors,
        ) {
            Icon(icon, contentDescription)
        }
        IconButtonStyle.Tonal -> androidx.compose.material3.FilledTonalIconButton(
            onClick = onClick,
            modifier = modifier.oemTouchTarget(),
            enabled = enabled,
            colors = colors,
        ) {
            Icon(icon, contentDescription)
        }
    }
}
