package com.test.design.presentation.demos.specs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.test.design.component.components.CustomCard
import com.test.design.component.components.CustomSectionHeader
import com.test.design.component.core.RestrictedComponentPolicy
import com.test.design.component.theme.OemOnSurfaceVariant
import com.test.design.component.theme.OemSpacing
import com.test.design.core.export.DesignExportHelper
import com.test.design.presentation.demos.playground.PlaygroundCatalog
import com.test.design.presentation.demos.shared.DemoScaffold

@Composable
fun ComponentSpecsDemo(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    DemoScaffold(
        title = "Component Spec Sheets",
        onBack = onBack,
        yellowContent = {},
    ) {
        CustomSectionHeader(
            title = "Component Catalog",
            subtitle = "${PlaygroundCatalog.components.size} components with props and UXR notes",
        )
        PlaygroundCatalog.categories.forEach { category ->
            CustomSectionHeader(title = category, subtitle = "Variants and restrictions")
            PlaygroundCatalog.byCategory(category).forEach { definition ->
                ComponentSpecCard(
                    name = definition.name,
                    componentId = definition.id,
                    category = definition.category,
                )
            }
        }
    }
}

@Composable
private fun ComponentSpecCard(
    name: String,
    componentId: String,
    category: String,
) {
    val restrictions = buildList {
        if (componentId.contains("text-field") || componentId.contains("search")) {
            add("Keyboard blocked while Driving")
        }
        if (componentId.contains("slider")) {
            add("Disabled while Driving")
        }
        if (componentId.contains("secondary") || componentId.contains("destructive")) {
            add("Hidden in Restricted UXR")
        }
        if (componentId.contains("extended-fab") || componentId.contains("input-chip")) {
            add("Parked only")
        }
        if (componentId.contains("dialog")) {
            add("Dialogs blocked while Driving")
        }
    }.ifEmpty {
        listOf(
            "Touch target scales: " +
                "${RestrictedComponentPolicy.touchTarget(com.test.design.component.core.DrivingUxState.Parked).value.toInt()}–88dp",
        )
    }

    CustomCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = OemSpacing.xs),
    ) {
        Column {
            Text(text = name, style = MaterialTheme.typography.titleMedium)
            Text(
                text = "ID: $componentId · Category: $category",
                style = MaterialTheme.typography.bodySmall,
                color = OemOnSurfaceVariant,
            )
            Text(
                text = "UXR: ${restrictions.joinToString(" · ")}",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = OemSpacing.xs),
            )
            Text(
                text = "Playground ID: $componentId",
                style = MaterialTheme.typography.labelMedium,
                color = OemOnSurfaceVariant,
                modifier = Modifier.padding(top = OemSpacing.xs),
            )
            Text(
                text = DesignExportHelper.buildDeepLink("components-gallery"),
                style = MaterialTheme.typography.bodySmall,
                color = OemOnSurfaceVariant,
            )
        }
    }
}
