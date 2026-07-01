package com.test.design.presentation.demos.checklist

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.test.design.component.components.CustomCard
import com.test.design.component.components.CustomSectionHeader
import com.test.design.component.theme.OemOnSurfaceVariant
import com.test.design.component.theme.OemSpacing
import com.test.design.presentation.demos.shared.DemoScaffold
import com.test.design.presentation.demos.shared.DemoTipsPanel

private data class FigmaGapEntry(
    val component: String,
    val figmaLimitation: String,
    val androidBehavior: String,
)

private val figmaGaps = listOf(
    FigmaGapEntry(
        "CustomButton",
        "Static pressed/disabled frames",
        "Scale + alpha press motion; secondary/destructive hidden in Restricted UXR",
    ),
    FigmaGapEntry(
        "CustomTextField",
        "Editable in any prototype state",
        "Keyboard blocked while Driving; placeholder shows park-to-enter message",
    ),
    FigmaGapEntry(
        "CustomSlider",
        "Drag gesture in prototype only",
        "Fine controls disabled while Driving; zero animation duration",
    ),
    FigmaGapEntry(
        "CustomTabs",
        "Fixed tab count on artboard",
        "Tabs truncated to 3 (Driving) or 2 (Restricted) per CarUxRestrictions",
    ),
    FigmaGapEntry(
        "CustomList",
        "Scrollable layer mask",
        "List items capped at 4 (Driving) or 2 (Restricted)",
    ),
    FigmaGapEntry(
        "CustomDialog",
        "Overlay component",
        "Dialogs blocked entirely while Driving or Restricted",
    ),
    FigmaGapEntry(
        "CustomExtendedFab",
        "Always visible FAB variant",
        "Hidden in Restricted mode; touch target grows with UXR level",
    ),
    FigmaGapEntry(
        "Nav transitions",
        "Smart animate between frames",
        "Spring physics with OemMotionScheme; animations snap to 0ms while driving",
    ),
    FigmaGapEntry(
        "AutomotiveDashboardTemplate",
        "Manual zone rectangles",
        "Live zone weights adapt to 12.3\", 14.3\", and 15.3\" display profiles",
    ),
    FigmaGapEntry(
        "Rotary / knob input",
        "Not modeled in Figma",
        "Focus order and enlarged targets for non-touch hardware",
    ),
)

@Composable
fun FigmaChecklistDemo(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    DemoScaffold(
        title = "Figma vs Android",
        onBack = onBack,
        modifier = modifier,
        yellowContent = {
            DemoTipsPanel(
                tips = listOf(
                    "Use this checklist during design reviews",
                    "Validate every item here in Android before sign-off",
                    "Toggle global Driving State to confirm UXR-specific gaps",
                ),
            )
        },
    ) {
        CustomSectionHeader(
            title = "What Figma Cannot Show",
            subtitle = "Behaviors that require runtime Android validation",
        )
        figmaGaps.forEach { entry ->
            FigmaGapCard(entry)
        }
    }
}

@Composable
private fun FigmaGapCard(entry: FigmaGapEntry) {
    CustomCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = OemSpacing.sm),
    ) {
        Column {
            Text(
                text = entry.component,
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                text = "Figma: ${entry.figmaLimitation}",
                style = MaterialTheme.typography.bodyMedium,
                color = OemOnSurfaceVariant,
                modifier = Modifier.padding(top = OemSpacing.xs),
            )
            Text(
                text = "Android: ${entry.androidBehavior}",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = OemSpacing.xs),
            )
        }
    }
}
