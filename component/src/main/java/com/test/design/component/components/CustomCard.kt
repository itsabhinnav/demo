package com.test.design.component.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.test.design.component.theme.OemBackground
import com.test.design.component.theme.OemBorder
import com.test.design.component.theme.OemSpacing
import com.test.design.component.theme.OemSurfaceElevated
import com.test.design.component.theme.OemVisuals
import com.test.design.component.theme.oemCardSurface
import com.test.design.component.theme.oemSurfaceBorder

enum class CardStyle {
    Elevated,
    Filled,
    Outlined,
}

@Composable
fun CustomCard(
    modifier: Modifier = Modifier,
    style: CardStyle = CardStyle.Filled,
    onClick: (() -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    val shape = OemVisuals.cardShape
    val interactionSource = remember { MutableInteractionSource() }
    val surfaceModifier = when (style) {
        CardStyle.Elevated -> Modifier
            .clip(shape)
            .background(OemSurfaceElevated)
            .oemSurfaceBorder(shape, OemBorder)
        CardStyle.Filled -> Modifier.oemCardSurface(shape)
        CardStyle.Outlined -> Modifier
            .clip(shape)
            .background(OemBackground)
            .oemSurfaceBorder(shape, OemBorder)
    }

    val clickableModifier = if (onClick != null) {
        Modifier.clickable(
            interactionSource = interactionSource,
            indication = null,
            onClick = onClick,
        )
    } else {
        Modifier
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .then(clickableModifier)
            .then(surfaceModifier),
    ) {
        Column(
            Modifier.padding(OemSpacing.md),
            content = content,
        )
    }
}
