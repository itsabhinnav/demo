package com.test.design.presentation.demos.playground

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.test.design.component.components.CustomSectionHeader
import com.test.design.component.components.CustomSlider
import com.test.design.component.components.CustomSwitch
import com.test.design.component.components.CustomTextField
import com.test.design.component.theme.OemBackground
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
    val componentSchema = PlaygroundComponentProps.componentSchemaFor(componentId)
    val appearanceSchema = PlaygroundAppearance.schema
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
                    text = "Typography preset",
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
        } else if (componentSchema.isNotEmpty()) {
            componentSchema.forEach { definition ->
                PropertyControl(
                    definition = definition,
                    mergedProps = mergedProps,
                    onPropChange = onPropChange,
                )
            }
        }

        CustomSectionHeader(
            title = "Appearance",
            subtitle = "Color, spacing, font, and shape",
        )
        appearanceSchema.forEach { definition ->
            PropertyControl(
                definition = definition,
                mergedProps = mergedProps,
                onPropChange = onPropChange,
            )
        }
    }
}

@Composable
private fun PropertyControl(
    definition: PlaygroundPropertyDefinition,
    mergedProps: Map<String, String>,
    onPropChange: (String, String) -> Unit,
) {
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
                                label = formatEnumLabel(option),
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
        PlaygroundPropertyType.Color -> {
            Text(
                text = definition.label,
                style = MaterialTheme.typography.labelLarge,
                color = OemOnSurfaceVariant,
            )
            Column(verticalArrangement = Arrangement.spacedBy(OemSpacing.xs)) {
                definition.enumOptions.chunked(3).forEach { rowOptions ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(OemSpacing.xs),
                    ) {
                        rowOptions.forEach { option ->
                            ColorTokenSwatch(
                                token = runCatching { PlaygroundColorToken.valueOf(option) }
                                    .getOrDefault(PlaygroundColorToken.Default),
                                selected = mergedProps[definition.key] == option,
                                onClick = { onPropChange(definition.key, option) },
                                modifier = Modifier.weight(1f),
                            )
                        }
                        repeat(3 - rowOptions.size) {
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
                text = formatPropertyValue(definition, value),
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
                text = formatPropertyValue(definition, value.toFloat()),
                style = MaterialTheme.typography.bodySmall,
                color = OemOnSurfaceVariant,
            )
        }
    }
}

@Composable
private fun ColorTokenSwatch(
    token: PlaygroundColorToken,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = OemVisuals.chipShape
    val swatchColor = token.color ?: OemBackground
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(OemSpacing.xs),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(40.dp)
                .clip(shape)
                .background(swatchColor)
                .oemSurfaceBorder(
                    shape,
                    if (selected) MaterialTheme.colorScheme.onSurface else OemBorder,
                )
                .clickable(onClick = onClick),
        )
        Text(
            text = token.label,
            style = MaterialTheme.typography.labelSmall,
            color = if (selected) MaterialTheme.colorScheme.onSurface else OemOnSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
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

private fun formatEnumLabel(option: String): String =
    runCatching { PlaygroundTypographyToken.valueOf(option).label }
        .getOrElse { option.replace(Regex("([a-z])([A-Z])"), "$1 $2") }

private fun formatPropertyValue(definition: PlaygroundPropertyDefinition, value: Float): String =
    when (definition.key) {
        "progress", "opacity" -> {
            if (definition.key == "opacity") {
                "${(value * 100).roundToInt()}%"
            } else {
                "${(value * 100).roundToInt()}%"
            }
        }
        "fontScale" -> String.format("%.2fx", value)
        "cornerRadiusDp", "paddingDp", "marginDp", "borderWidthDp" -> "${value.roundToInt()}dp"
        else -> value.toString()
    }
