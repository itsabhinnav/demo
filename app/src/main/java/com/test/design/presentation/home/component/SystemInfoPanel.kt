package com.test.design.presentation.home.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.test.design.component.components.CustomSectionHeader
import com.test.design.component.components.CustomStatRow
import com.test.design.component.theme.OemSpacing
import com.test.design.presentation.home.model.SystemInfoUiState

@Composable
fun SystemInfoPanel(
    state: SystemInfoUiState,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        CustomSectionHeader(title = "System Info")
        Spacer(modifier = Modifier.height(OemSpacing.sm))
        CustomStatRow(label = "Display", value = state.displayLabel)
        CustomStatRow(label = "Width", value = state.widthLabel)
        CustomStatRow(label = "Height", value = state.heightLabel)
        CustomStatRow(label = "Layout", value = state.layoutLabel)
        CustomStatRow(label = "Blue Zone", value = state.blueZoneLabel)
    }
}
