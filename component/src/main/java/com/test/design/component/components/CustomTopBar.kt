package com.test.design.component.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import com.test.design.component.preview.AutomotivePreviews
import com.test.design.component.theme.OemBorder
import com.test.design.component.theme.OemOnSurface
import com.test.design.component.theme.OemSpacing
import com.test.design.component.theme.OemSurface
import com.test.design.component.theme.OemTheme
import com.test.design.component.theme.OemVisuals
import com.test.design.component.theme.oemSurfaceBorder

@Composable
fun CustomTopBar(
    title: String,
    modifier: Modifier = Modifier,
    showBack: Boolean = false,
    onBackClick: (() -> Unit)? = null,
    tabs: @Composable (() -> Unit)? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(OemSpacing.topBarHeight)
            .background(OemSurface)
            .padding(horizontal = OemSpacing.md),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (showBack && onBackClick != null) {
            OemTopBarIconButton(
                icon = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                onClick = onBackClick,
            )
        }

        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            color = OemOnSurface,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )

        tabs?.invoke()
    }
}

@Composable
private fun OemTopBarIconButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
) {
    val shape = OemVisuals.chipShape
    val interactionSource = remember { MutableInteractionSource() }
    Box(
        modifier = Modifier
            .size(OemSpacing.minTouchTarget)
            .clip(shape)
            .background(OemSurface)
            .oemSurfaceBorder(shape, OemBorder)
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
            tint = OemOnSurface,
            modifier = Modifier.size(OemSpacing.lg),
        )
    }
}

@AutomotivePreviews
@Composable
private fun CustomTopBarPreview() {
    OemTheme {
        CustomTopBar(title = "Oem AAOS")
    }
}

@AutomotivePreviews
@Composable
private fun CustomTopBarWithBackPreview() {
    OemTheme {
        CustomTopBar(
            title = "Demo Screen",
            showBack = true,
            onBackClick = {},
        )
    }
}
