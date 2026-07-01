package com.test.design.presentation.demos.playground

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.test.design.component.components.ButtonStyle
import com.test.design.component.components.CustomButton
import com.test.design.component.components.CustomSectionHeader
import com.test.design.component.components.CustomTopBar
import com.test.design.component.theme.OemBackground
import com.test.design.component.theme.OemOnSurfaceVariant
import com.test.design.component.theme.OemSpacing
import com.test.design.component.theme.OemSurface
import com.test.design.component.theme.OemVisuals
import androidx.compose.ui.draw.clip

@Composable
fun ComponentDetailEditor(
    componentId: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val presetStore = remember { ComponentPresetStore(context) }
    val definition = remember(componentId) { PlaygroundCatalog.findById(componentId) }

    var props by remember(componentId) {
        mutableStateOf(PlaygroundComponentProps.defaultProps(componentId))
    }
    var textContent by remember(componentId) {
        mutableStateOf(
            if (PlaygroundCatalog.isTextComponent(componentId)) {
                PlaygroundCatalog.defaultTextContent(componentId)
            } else {
                null
            },
        )
    }
    var saveStatus by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(componentId) {
        presetStore.load(componentId)?.let { snapshot ->
            props = PlaygroundComponentProps.mergeWithDefaults(componentId, snapshot.props)
            snapshot.textContent?.let { textContent = it }
        }
    }

    fun savePreset() {
        presetStore.save(
            ComponentPresetSnapshot(
                componentId = componentId,
                props = props,
                textContent = textContent,
            ),
        )
        saveStatus = "Saved"
    }

    Column(modifier = modifier.fillMaxSize()) {
        CustomTopBar(
            title = definition?.name ?: "Component",
            showBack = true,
            onBackClick = onBack,
            tabs = {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(OemSpacing.sm),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    saveStatus?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodySmall,
                            color = OemOnSurfaceVariant,
                        )
                    }
                    CustomButton(
                        text = "Save",
                        onClick = ::savePreset,
                        style = ButtonStyle.Primary,
                    )
                }
            },
        )

        Row(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(OemSpacing.md),
            horizontalArrangement = Arrangement.spacedBy(OemSpacing.md),
        ) {
            Surface(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize(),
                color = OemSurface,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(OemSpacing.lg),
                    verticalArrangement = Arrangement.spacedBy(OemSpacing.md),
                ) {
                    CustomSectionHeader(
                        title = "Preview",
                        subtitle = definition?.category,
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .clip(OemVisuals.cardShape)
                            .background(OemBackground)
                            .padding(OemSpacing.lg),
                        contentAlignment = Alignment.Center,
                    ) {
                        PlaygroundComponentRenderer(
                            componentId = componentId,
                            textContent = textContent,
                            props = props,
                        )
                    }
                }
            }

            Surface(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize(),
                color = OemSurface,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(OemSpacing.lg),
                    verticalArrangement = Arrangement.spacedBy(OemSpacing.md),
                ) {
                    ComponentPropertyEditor(
                        componentId = componentId,
                        props = props,
                        textContent = textContent,
                        onPropChange = { key, value -> props = props + (key to value) },
                        onTextContentChange = { textContent = it },
                        onTextStyleChange = null,
                    )

                    CustomButton(
                        text = "Reset to defaults",
                        onClick = {
                            props = PlaygroundComponentProps.defaultProps(componentId)
                            textContent = if (PlaygroundCatalog.isTextComponent(componentId)) {
                                PlaygroundCatalog.defaultTextContent(componentId)
                            } else {
                                null
                            }
                            presetStore.clear(componentId)
                            saveStatus = "Reset"
                        },
                        style = ButtonStyle.Secondary,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }
    }
}
