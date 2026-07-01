package com.test.design.presentation.demos.flow

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.test.design.component.components.ButtonStyle
import com.test.design.component.components.CustomButton
import com.test.design.component.components.CustomChip
import com.test.design.component.components.CustomSectionHeader
import com.test.design.component.theme.OemOnSurfaceVariant
import com.test.design.component.theme.OemSpacing
import com.test.design.core.export.DesignExportHelper
import com.test.design.presentation.demos.playground.PlaygroundComponentRenderer
import com.test.design.presentation.demos.shared.DemoScaffold
import com.test.design.presentation.demos.shared.DemoTipsPanel

@Composable
fun FlowBuilderDemo(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val store = remember { FlowDesignStore(context) }
    var flow by remember { mutableStateOf(FlowDesignStore.defaultFlow()) }
    var currentScreenIndex by remember { mutableIntStateOf(0) }

    LaunchedEffect(Unit) {
        store.load()?.let { flow = it }
    }

    val currentScreen = flow.screens.getOrNull(currentScreenIndex) ?: flow.screens.first()

    DemoScaffold(
        title = "Flow Builder",
        onBack = onBack,
        modifier = modifier,
        yellowContent = {
            DemoTipsPanel(
                tips = listOf(
                    "Step through screens to preview multi-step flows with real navigation",
                    "Export JSON for engineering handoff or share via deep link",
                    "Global Driving State applies to every screen in the flow",
                ),
            )
        },
    ) {
        CustomSectionHeader(
            title = flow.title,
            subtitle = "Screen ${currentScreenIndex + 1} of ${flow.screens.size} · ${currentScreen.title}",
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = OemSpacing.md),
            horizontalArrangement = Arrangement.spacedBy(OemSpacing.sm),
        ) {
            flow.screens.forEachIndexed { index, screen ->
                CustomChip(
                    label = screen.title,
                    selected = index == currentScreenIndex,
                    onClick = { currentScreenIndex = index },
                )
            }
        }
        Column(
            modifier = Modifier.padding(vertical = OemSpacing.md),
            verticalArrangement = Arrangement.spacedBy(OemSpacing.md),
        ) {
            currentScreen.componentIds.forEach { componentId ->
                PlaygroundComponentRenderer(componentId = componentId)
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = OemSpacing.md),
            horizontalArrangement = Arrangement.spacedBy(OemSpacing.sm),
        ) {
            CustomButton(
                text = "Previous",
                onClick = { currentScreenIndex = (currentScreenIndex - 1).coerceAtLeast(0) },
                style = ButtonStyle.Secondary,
                enabled = currentScreenIndex > 0,
            )
            CustomButton(
                text = "Next",
                onClick = {
                    currentScreenIndex = (currentScreenIndex + 1).coerceAtMost(flow.screens.lastIndex)
                },
                style = ButtonStyle.Primary,
                enabled = currentScreenIndex < flow.screens.lastIndex,
            )
            CustomButton(
                text = "Save",
                onClick = { store.save(flow) },
                style = ButtonStyle.Tonal,
            )
            CustomButton(
                text = "Export",
                onClick = {
                    DesignExportHelper.shareJson(
                        context = context,
                        fileName = "flow-design.json",
                        json = store.exportJson(flow),
                        chooserTitle = "Export flow design",
                    )
                },
                style = ButtonStyle.Secondary,
            )
        }
        Text(
            text = "Deep link: ${DesignExportHelper.buildDeepLink("flow-builder")}",
            style = MaterialTheme.typography.bodySmall,
            color = OemOnSurfaceVariant,
            modifier = Modifier.padding(top = OemSpacing.sm),
        )
    }
}
