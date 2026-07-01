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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.test.design.component.components.CustomSectionHeader
import com.test.design.component.core.DrivingUxState
import com.test.design.component.theme.OemBorder
import com.test.design.component.theme.OemOnSurfaceVariant
import com.test.design.component.theme.OemSpacing
import com.test.design.component.theme.OemSurfaceElevated
import com.test.design.component.theme.OemTheme
import com.test.design.component.theme.OemVisuals
import com.test.design.presentation.demos.playground.PlaygroundComponentRenderer
import com.test.design.presentation.demos.shared.DemoScaffold
import com.test.design.presentation.demos.shared.DemoTipsPanel

private data class MatrixRow(
    val label: String,
    val componentId: String,
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
    MatrixRow("Dialog", "dialog-trigger"),
)

@Composable
fun ComponentStateMatrixDemo(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    DemoScaffold(
        title = "Component State Matrix",
        onBack = onBack,
        modifier = modifier,
        yellowContent = {
            DemoTipsPanel(
                tips = listOf(
                    "Use the global Driving State toggle to preview app-wide UXR",
                    "Each column renders the same component under a different UXR level",
                    "Compare touch targets, hidden actions, and blocked inputs side by side",
                ),
            )
        },
    ) {
        CustomSectionHeader(
            title = "UXR State Matrix",
            subtitle = "Scroll horizontally to compare Parked, Driving, and Restricted behavior",
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(vertical = OemSpacing.md),
            horizontalArrangement = Arrangement.spacedBy(OemSpacing.sm),
        ) {
            MatrixHeaderCell("Component", isHeader = true)
            DrivingUxState.entries.forEach { state ->
                MatrixHeaderCell(state.name, isHeader = true)
            }
        }
        matrixRows.forEach { row ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(bottom = OemSpacing.sm),
                horizontalArrangement = Arrangement.spacedBy(OemSpacing.sm),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                MatrixHeaderCell(row.label)
                DrivingUxState.entries.forEach { state ->
                    MatrixPreviewCell(componentId = row.componentId, state = state)
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
    componentId: String,
    state: DrivingUxState,
) {
    OemTheme(drivingUxState = state) {
        Box(
            modifier = Modifier
                .width(180.dp)
                .height(120.dp)
                .clip(OemVisuals.cardShape)
                .background(OemSurfaceElevated)
                .border(1.dp, OemBorder, OemVisuals.cardShape)
                .padding(OemSpacing.sm),
            contentAlignment = Alignment.Center,
        ) {
            PlaygroundComponentRenderer(componentId = componentId)
        }
    }
}
