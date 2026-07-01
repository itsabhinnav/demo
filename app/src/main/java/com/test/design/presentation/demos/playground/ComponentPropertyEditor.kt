package com.test.design.presentation.demos.playground

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import com.test.design.component.components.CustomSectionHeader
import com.test.design.component.components.CustomSlider
import com.test.design.component.components.CustomSwitch
import com.test.design.component.components.CustomTextField
import com.test.design.component.theme.OemBorder
import com.test.design.component.theme.OemOnSurfaceVariant
import com.test.design.component.theme.OemSpacing
import com.test.design.component.theme.OemSurface
import com.test.design.component.theme.OemSurfaceElevated
import com.test.design.component.theme.OemVisuals
import com.test.design.component.theme.oemSurfaceBorder
import kotlin.math.roundToInt

@Composable
fun ComponentPropertyEditor(
    componentId: String,
    props: Map<String, String>,
    textContent: String?,
    onPropChange: (String, String) -> Unit,
    onTextContentChange: ((String) -> Unit)? = null,
    onTextStyleChange: ((String) -> Unit)? = null,
    modifier: Modifier = Modifier,
    showHeader: Boolean = true,
) {
    val mergedProps = PlaygroundComponentProps.mergeWithDefaults(componentId, props)
    val schema = PlaygroundComponentProps.schemaFor(componentId)
    val isText = PlaygroundCatalog.isTextComponent(componentId)
    val textStyle = PlaygroundTextStyle.fromComponentId(componentId)

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(OemSpacing.md),
    ) {
        if (showHeader) {
            val definition = PlaygroundCatalog.findById(componentId)
            CustomSectionHeader(
                title = "Component",
                subtitle = definition?.name ?: componentId,
            )
        }

        if (isText && onTextContentChange != null) {
            CustomTextField(
                value = textContent.orEmpty(),
                onValueChange = onTextContentChange,
                label = "Text content",
                placeholder = "Enter label or copy",
                modifier = Modifier.fillMaxWidth(),
            )

            if (onTextStyleChange != null && textStyle != null) {
                Text(
                    text = "Typography style",
                    style = MaterialTheme.typography.labelLarge,
                    color = OemOnSurfaceVariant,
                )
                Column(verticalArrangement = Arrangement.spacedBy(OemSpacing.xs)) {
                    PlaygroundTextStyle.entriesList.chunked(2).forEach { rowStyles ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(OemSpacing.xs),
                        ) {
                            rowStyles.forEach { style ->
                                EnumChip(
                                    label = style.label,
                                    selected = textStyle == style,
                                    onClick = { onTextStyleChange(style.id) },
                                    modifier = Modifier.weight(1f),
                                )
                            }
                            if (rowStyles.size == 1) {
                                Box(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        } else if (schema.isNotEmpty()) {
            schema.forEach { definition ->
                when (definition.type) {
                    PlaygroundPropertyType.Text -> CustomTextField(
                        value = mergedProps[definition.key].orEmpty(),
                        onValueChange = { onPropChange(definition.key, it) },
                        label = definition.label,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    PlaygroundPropertyType.Boolean -> CustomSwitch(
                        label = definition.label,
                        checked = PlaygroundComponentProps.boolean(mergedProps, definition.key),
                        onCheckedChange = { onPropChange(definition.key, it.toString()) },
                    )
                    PlaygroundPropertyType.Enum -> {
                        Text(
                            text = definition.label,
                            style = MaterialTheme.typography.labelLarge,
                            color = OemOnSurfaceVariant,
                        )
                        Column(verticalArrangement = Arrangement.spacedBy(OemSpacing.xs)) {
                            definition.enumOptions.chunked(2).forEach { rowOptions ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(OemSpacing.xs),
                                ) {
                                    rowOptions.forEach { option ->
                                        EnumChip(
                                            label = option,
                                            selected = mergedProps[definition.key] == option,
                                            onClick = { onPropChange(definition.key, option) },
                                            modifier = Modifier.weight(1f),
                                        )
                                    }
                                    if (rowOptions.size == 1) {
                                        Box(modifier = Modifier.weight(1f))
                                    }
                                }
                            }
                        }
                    }
                    PlaygroundPropertyType.Float -> {
                        val range = definition.floatRange ?: 0f..1f
                        val value = PlaygroundComponentProps.float(mergedProps, definition.key, range.start)
                            .coerceIn(range)
                        CustomSlider(
                            value = value,
                            onValueChange = { onPropChange(definition.key, it.toString()) },
                            label = definition.label,
                            valueRange = range,
                        )
                        Text(
                            text = formatPropertyValue(definition, value.toString()),
                            style = MaterialTheme.typography.bodySmall,
                            color = OemOnSurfaceVariant,
                        )
                    }
                    PlaygroundPropertyType.Int -> {
                        val range = definition.floatRange ?: 0f..10f
                        val value = PlaygroundComponentProps.int(
                            mergedProps,
                            definition.key,
                            definition.defaultValue.toIntOrNull() ?: 0,
                        ).coerceIn(range.start.roundToInt(), range.endInclusive.roundToInt())
                        CustomSlider(
                            value = value.toFloat(),
                            onValueChange = { onPropChange(definition.key, it.roundToInt().toString()) },
                            label = definition.label,
                            valueRange = range,
                            steps = (range.endInclusive - range.start).roundToInt().coerceAtLeast(1) - 1,
                        )
                        Text(
                            text = formatPropertyValue(definition, value.toString()),
                            style = MaterialTheme.typography.bodySmall,
                            color = OemOnSurfaceVariant,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EnumChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = OemVisuals.chipShape
    Box(
        modifier = modifier
            .clip(shape)
            .background(if (selected) OemSurfaceElevated else OemSurface)
            .oemSurfaceBorder(
                shape,
                if (selected) MaterialTheme.colorScheme.onSurface else OemBorder,
            )
            .clickable(onClick = onClick)
            .padding(horizontal = OemSpacing.sm, vertical = OemSpacing.xs),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = if (selected) MaterialTheme.colorScheme.onSurface else OemOnSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

private fun formatPropertyValue(definition: PlaygroundPropertyDefinition, raw: String): String =
    when (definition.key) {
        "progress" -> "${(raw.toFloatOrNull()?.times(100) ?: 0f).roundToInt()}%"
        else -> raw
    }
