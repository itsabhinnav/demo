package com.test.design.presentation.demos.matrix

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.test.design.component.components.CustomButton
import com.test.design.component.components.CustomSectionHeader
import com.test.design.component.components.ButtonStyle
import com.test.design.component.core.DrivingUxState
import com.test.design.component.theme.OemBorder
import com.test.design.component.theme.OemOnSurfaceVariant
import com.test.design.component.theme.OemSpacing
import com.test.design.component.theme.OemSurfaceElevated
import com.test.design.component.theme.OemTheme
import com.test.design.component.theme.OemVisuals
import com.test.design.presentation.demos.playground.PlaygroundComponentRenderer
import com.test.design.presentation.demos.shared.DemoScaffold

private data class MatrixRow(
    val label: String,
    val componentId: String,
    val staticPreview: Boolean = false,
)

private val matrixRows = listOf(
    MatrixRow("Primary", "button-primary"),
    MatrixRow("Secondary", "button-secondary"),
    MatrixRow("Text field", "text-field"),
    MatrixRow("Slider", "slider"),
    MatrixRow("Filter chip", "filter-chip"),
    MatrixRow("Input chip", "input-chip"),
    MatrixRow("Tabs", "tabs"),
    MatrixRow("List tile", "list-tile"),
    MatrixRow("Extended FAB", "extended-fab"),
    MatrixRow("Dialog", "dialog-trigger", staticPreview = true),
)

@Composable
fun ComponentStateMatrixDemo(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val horizontalScroll = rememberScrollState()

    DemoScaffold(
        title = "Component State Matrix",
        onBack = onBack,
        modifier = modifier,
        yellowContent = {},
    ) {
        CustomSectionHeader(
            title = "UXR State Matrix",
            subtitle = "Compare component behavior across all restriction levels",
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(horizontalScroll)
                .padding(vertical = OemSpacing.md),
            horizontalArrangement = Arrangement.spacedBy(OemSpacing.sm),
        ) {
            MatrixHeaderCell("Component", isHeader = true)
            DrivingUxState.entries.forEach { state ->
                MatrixHeaderCell(state.name, isHeader = true)
            }
        }
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(OemSpacing.sm),
        ) {
            items(matrixRows, key = { it.componentId }) { row ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(horizontalScroll),
                    horizontalArrangement = Arrangement.spacedBy(OemSpacing.sm),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    MatrixHeaderCell(row.label)
                    DrivingUxState.entries.forEach { state ->
                        MatrixPreviewCell(
                            label = row.label,
                            componentId = row.componentId,
                            state = state,
                            staticPreview = row.staticPreview,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MatrixHeaderCell(label: String, isHeader: Boolean = false) {
    Box(
        modifier = Modifier
            .width(180.dp)
            .height(if (isHeader) 48.dp else 120.dp)
            .clip(OemVisuals.chipShape)
            .background(if (isHeader) OemSurfaceElevated else OemSurfaceElevated.copy(alpha = 0.6f))
            .border(1.dp, OemBorder, OemVisuals.chipShape)
            .padding(OemSpacing.sm),
        contentAlignment = if (isHeader) Alignment.Center else Alignment.CenterStart,
    ) {
        Text(
            text = label,
            style = if (isHeader) MaterialTheme.typography.labelLarge else MaterialTheme.typography.bodyMedium,
            color = OemOnSurfaceVariant,
        )
    }
}

@Composable
private fun MatrixPreviewCell(
    label: String,
    componentId: String,
    state: DrivingUxState,
    staticPreview: Boolean,
) {
    OemTheme(drivingUxState = state) {
        Box(
            modifier = Modifier
                .width(180.dp)
                .height(120.dp)
                .clip(OemVisuals.cardShape)
                .background(OemSurfaceElevated)
                .border(1.dp, OemBorder, OemVisuals.cardShape)
                .padding(OemSpacing.sm)
                .semantics {
                    contentDescription = "$label in ${state.name} UXR"
                },
            contentAlignment = Alignment.Center,
        ) {
            if (staticPreview && componentId == "dialog-trigger") {
                CustomButton(
                    text = "Dialog preview",
                    onClick = {},
                    style = ButtonStyle.Secondary,
                    enabled = false,
                )
            } else {
                PlaygroundComponentRenderer(componentId = componentId)
            }
        }
    }
}
