package com.test.design.component.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.test.design.component.core.RestrictedComponentPolicy
import com.test.design.component.core.currentDrivingUxState
import com.test.design.component.core.oemDrivingTouchTarget
import com.test.design.component.preview.AutomotivePreviews
import com.test.design.component.theme.OemBorder
import com.test.design.component.theme.OemError
import com.test.design.component.theme.OemOnPrimary
import com.test.design.component.theme.OemOnSurface
import com.test.design.component.theme.OemOnSurfaceVariant
import com.test.design.component.theme.OemPrimary
import com.test.design.component.theme.OemSpacing
import com.test.design.component.theme.OemSurfaceVariant
import com.test.design.component.theme.OemTheme
import com.test.design.component.theme.OemVisuals

enum class ButtonStyle {
    Primary,
    Secondary,
    Tonal,
    Text,
    Destructive,
}

@Composable
fun CustomButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: ButtonStyle = ButtonStyle.Primary,
    enabled: Boolean = true,
) {
    val drivingState = currentDrivingUxState()
    val styleAllowed = when (style) {
        ButtonStyle.Primary -> true
        ButtonStyle.Destructive -> RestrictedComponentPolicy.allowsDestructiveActions(drivingState)
        ButtonStyle.Secondary, ButtonStyle.Tonal, ButtonStyle.Text ->
            RestrictedComponentPolicy.allowsSecondaryActions(drivingState)
    }
    val isEnabled = enabled && styleAllowed
    val shape = OemVisuals.buttonShape
    val interactionSource = remember { MutableInteractionSource() }
    val sizedModifier = modifier
        .oemDrivingTouchTarget()
        .clip(shape)
        .then(
            if (isEnabled) {
                Modifier.clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = onClick,
                )
            } else {
                Modifier
            },
        )

    val (backgroundModifier, contentColor) = when (style) {
        ButtonStyle.Primary -> Modifier.background(OemPrimary) to OemOnPrimary
        ButtonStyle.Secondary -> Modifier
            .background(Color.Transparent)
            .border(1.dp, OemBorder, shape) to OemOnSurface
        ButtonStyle.Tonal -> Modifier
            .background(OemSurfaceVariant)
            .border(1.dp, OemBorder, shape) to OemOnSurface
        ButtonStyle.Text -> Modifier.background(Color.Transparent) to OemOnSurface
        ButtonStyle.Destructive -> Modifier
            .background(OemSurfaceVariant)
            .border(1.dp, OemError, shape) to OemOnSurfaceVariant
    }

    Box(
        modifier = sizedModifier.then(backgroundModifier),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            color = if (isEnabled) contentColor else contentColor.copy(alpha = 0.4f),
            modifier = Modifier.padding(horizontal = OemSpacing.lg, vertical = OemSpacing.sm),
        )
    }
}

@AutomotivePreviews
@Composable
private fun CustomButtonPreview() {
    OemTheme {
        CustomButton(text = "Primary", onClick = {}, modifier = Modifier.padding(OemSpacing.md))
    }
}

@Preview(showBackground = true)
@Composable
private fun CustomButtonStylesPreview() {
    OemTheme {
        CustomButton(text = "Secondary", onClick = {}, style = ButtonStyle.Secondary)
    }
}
