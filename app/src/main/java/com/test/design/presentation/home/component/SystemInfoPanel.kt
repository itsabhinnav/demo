package com.test.design.presentation.home.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.test.design.component.theme.NissanSpacing
import com.test.design.presentation.home.model.SystemInfoUiState

@Composable
fun SystemInfoPanel(
    state: SystemInfoUiState,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(
            text = "System Info",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Spacer(modifier = Modifier.height(NissanSpacing.md))
        InfoRow(label = "Display", value = state.displayLabel)
        InfoRow(label = "Width", value = state.widthLabel)
        InfoRow(label = "Height", value = state.heightLabel)
        InfoRow(label = "Layout", value = state.layoutLabel)
        InfoRow(label = "Blue Zone", value = state.blueZoneLabel)
    }
}

@Composable
private fun InfoRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.height(NissanSpacing.listItemHeight)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}
