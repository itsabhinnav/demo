package com.test.design.component.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.window.Dialog
import com.test.design.component.core.RestrictedComponentPolicy
import com.test.design.component.core.currentDrivingUxState
import com.test.design.component.theme.OemOnSurface
import com.test.design.component.theme.OemOnSurfaceVariant
import com.test.design.component.theme.OemSpacing
import com.test.design.component.theme.OemSurfaceElevated
import com.test.design.component.theme.OemVisuals
import com.test.design.component.theme.oemSurfaceBorder

@Composable
fun CustomDialog(
    title: String,
    message: String,
    confirmText: String,
    dismissText: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val drivingState = currentDrivingUxState()
    if (!RestrictedComponentPolicy.allowsDialogs(drivingState)) return

    Dialog(onDismissRequest = onDismiss) {
        val shape = OemVisuals.cardShape
        Column(
            modifier = modifier
                .fillMaxWidth()
                .clip(shape)
                .background(OemSurfaceElevated)
                .oemSurfaceBorder(shape)
                .padding(OemSpacing.lg),
            verticalArrangement = Arrangement.spacedBy(OemSpacing.md),
        ) {
            Text(text = title, style = MaterialTheme.typography.headlineMedium, color = OemOnSurface)
            Text(text = message, style = MaterialTheme.typography.bodyLarge, color = OemOnSurfaceVariant)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
            ) {
                CustomButton(
                    text = dismissText,
                    onClick = onDismiss,
                    style = ButtonStyle.Secondary,
                    modifier = Modifier.padding(end = OemSpacing.sm),
                )
                CustomButton(
                    text = confirmText,
                    onClick = onConfirm,
                    style = ButtonStyle.Primary,
                )
            }
        }
    }
}

@Composable
fun CustomSnackbarMessage(
    message: String,
    modifier: Modifier = Modifier,
    actionLabel: String? = null,
    onAction: () -> Unit = {},
) {
    val shape = OemVisuals.cardShape
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(OemSurfaceElevated)
            .oemSurfaceBorder(shape)
            .padding(horizontal = OemSpacing.md, vertical = OemSpacing.sm),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge,
            color = OemOnSurface,
            modifier = Modifier.weight(1f),
        )
        if (actionLabel != null) {
            CustomButton(
                text = actionLabel,
                onClick = onAction,
                style = ButtonStyle.Text,
            )
        }
    }
}
