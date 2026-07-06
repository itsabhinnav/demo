package com.test.design.presentation.demos.flow

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.test.design.component.components.ButtonStyle
import com.test.design.component.components.CustomButton
import com.test.design.component.components.CustomChip
import com.test.design.component.components.CustomSectionHeader
import com.test.design.component.components.CustomTextField
import com.test.design.component.theme.OemOnSurfaceVariant
import com.test.design.component.theme.OemSpacing
import com.test.design.core.export.DesignExportHelper
import com.test.design.presentation.demos.playground.PlaygroundCatalog
import com.test.design.presentation.demos.playground.PlaygroundComponentRenderer
import com.test.design.presentation.demos.shared.DemoScaffold

@Composable
fun FlowBuilderDemo(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val viewModel: FlowBuilderViewModel = viewModel(
        factory = FlowBuilderViewModelFactory(FlowDesignStore(context)),
    )
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    val currentScreen = uiState.flow.screens[uiState.currentScreenIndex]

    DemoScaffold(
        title = "Flow Builder",
        onBack = onBack,
        modifier = modifier,
        yellowContent = {},
    ) {
        CustomTextField(
            value = uiState.flow.title,
            onValueChange = viewModel::updateFlowTitle,
            label = "Flow title",
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = OemSpacing.md),
        )
        CustomSectionHeader(
            title = uiState.flow.title,
            subtitle = buildString {
                append("Screen ${uiState.currentScreenIndex + 1} of ${uiState.flow.screens.size}")
                append(" · ")
                append(currentScreen.title)
                uiState.lastSavedAtMillis?.let { append(" · Saved") }
            },
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(vertical = OemSpacing.sm),
            horizontalArrangement = Arrangement.spacedBy(OemSpacing.sm),
        ) {
            uiState.flow.screens.forEachIndexed { index, screen ->
                CustomChip(
                    label = screen.title,
                    selected = index == uiState.currentScreenIndex,
                    onClick = { viewModel.selectScreen(index) },
                )
            }
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = OemSpacing.md),
            horizontalArrangement = Arrangement.spacedBy(OemSpacing.sm),
        ) {
            CustomButton(text = "Add screen", onClick = viewModel::addScreen, style = ButtonStyle.Tonal)
            CustomButton(
                text = "Remove screen",
                onClick = viewModel::removeCurrentScreen,
                style = ButtonStyle.Secondary,
                enabled = uiState.flow.screens.size > 1,
            )
        }

        CustomSectionHeader(title = "Preview", subtitle = "Live components for the selected screen")
        Column(
            modifier = Modifier.padding(vertical = OemSpacing.sm),
            verticalArrangement = Arrangement.spacedBy(OemSpacing.md),
        ) {
            if (currentScreen.componentIds.isEmpty()) {
                Text(
                    text = "Add a component from the catalog below.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = OemOnSurfaceVariant,
                )
            } else {
                currentScreen.componentIds.forEach { componentId ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                    ) {
                        PlaygroundComponentRenderer(
                            componentId = componentId,
                            modifier = Modifier.weight(1f),
                        )
                        CustomButton(
                            text = "Remove",
                            onClick = { viewModel.removeComponentFromCurrentScreen(componentId) },
                            style = ButtonStyle.Text,
                        )
                    }
                }
            }
        }

        CustomSectionHeader(title = "Component catalog", subtitle = "Tap to add to the current screen")
        PlaygroundCatalog.categories.forEach { category ->
            Text(
                text = category,
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(top = OemSpacing.sm, bottom = OemSpacing.xs),
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(OemSpacing.sm),
            ) {
                PlaygroundCatalog.byCategory(category).take(8).forEach { definition ->
                    CustomChip(
                        label = definition.name,
                        selected = definition.id in currentScreen.componentIds,
                        onClick = { viewModel.addComponentToCurrentScreen(definition.id) },
                    )
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = OemSpacing.lg),
            horizontalArrangement = Arrangement.spacedBy(OemSpacing.sm),
        ) {
            CustomButton(
                text = "Previous",
                onClick = viewModel::goToPreviousScreen,
                style = ButtonStyle.Secondary,
                enabled = uiState.currentScreenIndex > 0,
            )
            CustomButton(
                text = "Next",
                onClick = viewModel::goToNextScreen,
                style = ButtonStyle.Primary,
                enabled = uiState.currentScreenIndex < uiState.flow.screens.lastIndex,
            )
            CustomButton(
                text = if (uiState.isSaving) "Saving…" else "Save",
                onClick = viewModel::saveNow,
                style = ButtonStyle.Tonal,
            )
            CustomButton(
                text = "Export",
                onClick = { viewModel.export(context) },
                style = ButtonStyle.Secondary,
            )
        }
        Text(
            text = "Deep link: ${DesignExportHelper.buildDeepLink("flow-builder")}",
            style = MaterialTheme.typography.bodySmall,
            color = OemOnSurfaceVariant,
        )
    }
}
