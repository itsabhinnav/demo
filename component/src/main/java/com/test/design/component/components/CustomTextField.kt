package com.test.design.component.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.test.design.component.core.RestrictedComponentPolicy
import com.test.design.component.core.currentDrivingUxState
import com.test.design.component.core.oemDrivingTouchTarget
import com.test.design.component.theme.OemBorder
import com.test.design.component.theme.OemOnSurface
import com.test.design.component.theme.OemOnSurfaceVariant
import com.test.design.component.theme.OemOutline
import com.test.design.component.theme.OemPrimary
import com.test.design.component.theme.OemSpacing
import com.test.design.component.theme.OemSurfaceVariant
import com.test.design.component.theme.OemVisuals

@Composable
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    enabled: Boolean = true,
    singleLine: Boolean = true,
    onDone: () -> Unit = {},
) {
    val drivingState = currentDrivingUxState()
    val keyboardAllowed = RestrictedComponentPolicy.allowsKeyboardInput(drivingState)
    val blockedMessage = RestrictedComponentPolicy.keyboardBlockedMessage(drivingState)
    val fieldEnabled = enabled && keyboardAllowed
    val shape = OemVisuals.buttonShape
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val borderColor = when {
        !fieldEnabled -> OemBorder.copy(alpha = 0.4f)
        isFocused -> OemPrimary
        else -> OemOutline
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .oemDrivingTouchTarget(),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = if (fieldEnabled) OemOnSurfaceVariant else OemOnSurfaceVariant.copy(alpha = 0.4f),
            modifier = Modifier.padding(bottom = OemSpacing.xs),
        )
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .clip(shape)
                .background(if (fieldEnabled) OemSurfaceVariant else OemSurfaceVariant.copy(alpha = 0.5f))
                .border(1.dp, borderColor, shape)
                .padding(horizontal = OemSpacing.md, vertical = OemSpacing.md),
            enabled = fieldEnabled,
            singleLine = singleLine,
            textStyle = MaterialTheme.typography.bodyLarge.copy(color = OemOnSurface),
            cursorBrush = SolidColor(OemPrimary),
            interactionSource = interactionSource,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
            keyboardActions = KeyboardActions(onDone = { onDone() }),
            decorationBox = { innerTextField ->
                Box(contentAlignment = Alignment.CenterStart) {
                    if (value.isEmpty()) {
                        Text(
                            text = if (!keyboardAllowed && blockedMessage != null) blockedMessage else placeholder,
                            style = MaterialTheme.typography.bodyLarge,
                            color = if (!keyboardAllowed) OemOnSurfaceVariant else OemOnSurfaceVariant.copy(alpha = 0.7f),
                        )
                    }
                    innerTextField()
                }
            },
        )
    }
}

@Composable
fun CustomSearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    onSearch: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
) {
    val drivingState = currentDrivingUxState()
    val keyboardAllowed = RestrictedComponentPolicy.allowsKeyboardInput(drivingState)
    val blockedMessage = RestrictedComponentPolicy.keyboardBlockedMessage(drivingState)
    val shape = OemVisuals.buttonShape
    val interactionSource = remember { MutableInteractionSource() }
    val isFocused by interactionSource.collectIsFocusedAsState()
    val borderColor = when {
        !keyboardAllowed -> OemBorder.copy(alpha = 0.4f)
        isFocused -> OemPrimary
        else -> OemOutline
    }

    BasicTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier
            .fillMaxWidth()
            .oemDrivingTouchTarget()
            .clip(shape)
            .background(
                if (keyboardAllowed) OemSurfaceVariant else OemSurfaceVariant.copy(alpha = 0.5f),
            )
            .border(1.dp, borderColor, shape)
            .padding(horizontal = OemSpacing.md, vertical = OemSpacing.md),
        enabled = keyboardAllowed,
        singleLine = true,
        textStyle = MaterialTheme.typography.bodyLarge.copy(color = OemOnSurface),
        cursorBrush = SolidColor(OemPrimary),
        interactionSource = interactionSource,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = { onSearch(query) }),
        decorationBox = { innerTextField ->
            Box(contentAlignment = Alignment.CenterStart) {
                RowWithIcon(
                    placeholder = if (!keyboardAllowed && blockedMessage != null) blockedMessage else placeholder,
                    showPlaceholder = query.isEmpty(),
                    innerTextField = innerTextField,
                )
            }
        },
    )
}

@Composable
private fun RowWithIcon(
    placeholder: String,
    showPlaceholder: Boolean,
    innerTextField: @Composable () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(OemSpacing.sm),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Icon(
            Icons.Default.Search,
            contentDescription = "Search",
            tint = OemOnSurfaceVariant,
            modifier = Modifier.size(OemSpacing.lg),
        )
        Box(modifier = Modifier.weight(1f)) {
            if (showPlaceholder) {
                Text(
                    text = placeholder,
                    style = MaterialTheme.typography.bodyLarge,
                    color = OemOnSurfaceVariant.copy(alpha = 0.7f),
                )
            }
            innerTextField()
        }
    }
}
