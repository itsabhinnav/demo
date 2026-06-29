package com.test.design.component.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import com.test.design.component.core.RestrictedComponentPolicy
import com.test.design.component.core.currentDrivingUxState
import com.test.design.component.core.oemDrivingTouchTarget
import com.test.design.component.theme.OemOnSurfaceVariant

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

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            .oemDrivingTouchTarget(),
        label = { Text(label, style = MaterialTheme.typography.bodyMedium) },
        placeholder = {
            Text(
                if (!keyboardAllowed && blockedMessage != null) blockedMessage else placeholder,
                style = MaterialTheme.typography.bodyMedium,
                color = if (!keyboardAllowed) OemOnSurfaceVariant else MaterialTheme.colorScheme.onSurfaceVariant,
            )
        },
        enabled = fieldEnabled,
        singleLine = singleLine,
        shape = MaterialTheme.shapes.medium,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
            focusedLabelColor = MaterialTheme.colorScheme.primary,
            cursorColor = MaterialTheme.colorScheme.primary,
        ),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        keyboardActions = KeyboardActions(onDone = { onDone() }),
    )
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

    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier
            .fillMaxWidth()
            .oemDrivingTouchTarget(),
        placeholder = {
            Text(
                if (!keyboardAllowed && blockedMessage != null) blockedMessage else placeholder,
                style = MaterialTheme.typography.bodyLarge,
            )
        },
        enabled = keyboardAllowed,
        leadingIcon = {
            Icon(Icons.Default.Search, contentDescription = "Search", tint = MaterialTheme.colorScheme.onSurfaceVariant)
        },
        singleLine = true,
        shape = MaterialTheme.shapes.medium,
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
        ),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = { onSearch(query) }),
    )
}
