package com.test.design.presentation.demos.playground

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextOverflow
import com.test.design.component.components.ButtonStyle
import com.test.design.component.components.CustomAssistChip
import com.test.design.component.components.CustomBadge
import com.test.design.component.components.CustomButton
import com.test.design.component.components.CustomCard
import com.test.design.component.components.CustomCheckbox
import com.test.design.component.components.CustomChip
import com.test.design.component.components.CustomCircularProgress
import com.test.design.component.components.CustomDialog
import com.test.design.component.components.CustomEmptyState
import com.test.design.component.components.CustomExtendedFab
import com.test.design.component.components.CustomFab
import com.test.design.component.components.CustomIconButton
import com.test.design.component.components.CustomImage
import com.test.design.component.components.CustomInputChip
import com.test.design.component.components.CustomLinearProgress
import com.test.design.component.components.CustomListTile
import com.test.design.component.components.CustomMetricCard
import com.test.design.component.components.CustomRadioButton
import com.test.design.component.components.CustomSearchBar
import com.test.design.component.components.CustomSegmentedButtonRow
import com.test.design.component.components.CustomSlider
import com.test.design.component.components.CustomSnackbarMessage
import com.test.design.component.components.CustomStatusIndicator
import com.test.design.component.components.CustomSuggestionChip
import com.test.design.component.components.CustomSwitch
import com.test.design.component.components.CustomTabs
import com.test.design.component.components.CustomTextField
import com.test.design.component.components.FabSize
import com.test.design.component.components.IconButtonStyle
import com.test.design.component.components.StatusLevel
import com.test.design.component.theme.OemSpacing
import com.test.design.component.tokens.DesignTokens

data class PlaygroundComponentDefinition(
    val id: String,
    val name: String,
    val category: String,
    val icon: ImageVector,
)

data class PlacedComponent(
    val instanceId: Int,
    val componentId: String,
    /** Horizontal position as a fraction of canvas width (0–1). */
    val xFraction: Float = 0.05f,
    /** Vertical position as a fraction of canvas height (0–1). */
    val yFraction: Float = 0.05f,
    /** Null = wrap content; otherwise fraction of canvas width (0.1–1.0). */
    val widthFraction: Float? = null,
    /** Null = wrap content; otherwise fraction of canvas height (0.08–1.0). */
    val heightFraction: Float? = null,
    /** Outer spacing around the component (dp). */
    val marginDp: Float = 0f,
    /** Inner spacing around the component content (dp). */
    val paddingDp: Float = 0f,
    /** Editable copy for text typography components. */
    val textContent: String? = null,
    /** Component-specific customizable properties. */
    val props: Map<String, String> = emptyMap(),
)

enum class PlaygroundTextStyle(
    val id: String,
    val label: String,
    val sample: String,
) {
    Display("text-display", "Display", "287 km"),
    HeadlineLarge("text-headline-lg", "Headline Large", "Climate control"),
    HeadlineMedium("text-headline-md", "Headline Medium", "Navigation"),
    TitleLarge("text-title-lg", "Title Large", "Vehicle settings"),
    TitleMedium("text-title-md", "Title Medium", "Battery status"),
    BodyLarge("text-body-lg", "Body Large", "Range updated for current drive mode."),
    BodyMedium("text-body-md", "Body Medium", "Estimated arrival in 12 minutes."),
    LabelLarge("text-label-lg", "Label Large", "DRIVE MODE"),
    LabelMedium("text-label-md", "Label Medium", "Eco · Sport · Snow"),
    ;

    companion object {
        val entriesList = entries.toList()

        fun fromComponentId(id: String): PlaygroundTextStyle? =
            entries.find { it.id == id }
    }
}

object PlaygroundCatalog {
    val categories: List<String> = DesignTokens.componentCategories + "Text"

    val components: List<PlaygroundComponentDefinition> = listOf(
        PlaygroundComponentDefinition("button-primary", "Primary Button", "Actions", Icons.Default.Add),
        PlaygroundComponentDefinition("button-tonal", "Tonal Button", "Actions", Icons.Default.Add),
        PlaygroundComponentDefinition("button-secondary", "Secondary Button", "Actions", Icons.Default.Add),
        PlaygroundComponentDefinition("icon-button", "Icon Button", "Actions", Icons.Default.Settings),
        PlaygroundComponentDefinition("fab", "FAB", "Actions", Icons.Default.Add),
        PlaygroundComponentDefinition("extended-fab", "Extended FAB", "Actions", Icons.Default.Navigation),
        PlaygroundComponentDefinition("filter-chip", "Filter Chip", "Selection", Icons.Default.Settings),
        PlaygroundComponentDefinition("assist-chip", "Assist Chip", "Selection", Icons.Default.Add),
        PlaygroundComponentDefinition("suggestion-chip", "Suggestion Chip", "Selection", Icons.Default.Navigation),
        PlaygroundComponentDefinition("input-chip", "Input Chip", "Selection", Icons.Default.Settings),
        PlaygroundComponentDefinition("switch", "Switch", "Selection", Icons.Default.Settings),
        PlaygroundComponentDefinition("checkbox", "Checkbox", "Selection", Icons.Default.Settings),
        PlaygroundComponentDefinition("radio", "Radio Button", "Selection", Icons.Default.Settings),
        PlaygroundComponentDefinition("segmented-button", "Segmented Button", "Selection", Icons.Default.Settings),
        PlaygroundComponentDefinition("text-field", "Text Field", "Input", Icons.Default.Search),
        PlaygroundComponentDefinition("search-bar", "Search Bar", "Input", Icons.Default.Search),
        PlaygroundComponentDefinition("slider", "Slider", "Input", Icons.Default.Settings),
        PlaygroundComponentDefinition("card", "Card", "Display", Icons.Default.Settings),
        PlaygroundComponentDefinition("metric-card", "Metric Card", "Display", Icons.Default.DirectionsCar),
        PlaygroundComponentDefinition("list-tile", "List Tile", "Display", Icons.Default.Navigation),
        PlaygroundComponentDefinition("image", "Image", "Display", Icons.Default.DirectionsCar),
        PlaygroundComponentDefinition("tabs", "Tabs", "Navigation", Icons.Default.Navigation),
        PlaygroundComponentDefinition("status-indicator", "Status Indicator", "Feedback", Icons.Default.Notifications),
        PlaygroundComponentDefinition("linear-progress", "Linear Progress", "Feedback", Icons.Default.Settings),
        PlaygroundComponentDefinition("circular-progress", "Circular Progress", "Feedback", Icons.Default.Settings),
        PlaygroundComponentDefinition("snackbar", "Snackbar", "Feedback", Icons.Default.Notifications),
        PlaygroundComponentDefinition("empty-state", "Empty State", "Feedback", Icons.Default.Search),
        PlaygroundComponentDefinition("dialog-trigger", "Dialog", "Feedback", Icons.Default.Notifications),
    ) + PlaygroundTextStyle.entriesList.map { style ->
        PlaygroundComponentDefinition(
            id = style.id,
            name = style.label,
            category = "Text",
            icon = Icons.Default.TextFields,
        )
    }

    fun isTextComponent(componentId: String): Boolean =
        componentId.startsWith("text-")

    fun defaultTextContent(componentId: String): String =
        PlaygroundTextStyle.fromComponentId(componentId)?.sample ?: "Text"

    fun findById(id: String): PlaygroundComponentDefinition? = components.find { it.id == id }

    fun byCategory(category: String): List<PlaygroundComponentDefinition> =
        components.filter { it.category == category }
}

@Composable
fun PlaygroundComponentRenderer(
    componentId: String,
    modifier: Modifier = Modifier,
    textContent: String? = null,
    props: Map<String, String> = emptyMap(),
) {
    val mergedProps = PlaygroundComponentProps.mergeWithDefaults(componentId, props)
    val propsAccessor = PlaygroundComponentProps

    PlaygroundTextStyle.fromComponentId(componentId)?.let { style ->
        PlaygroundTextRenderer(
            style = style,
            text = textContent ?: propsAccessor.string(mergedProps, "textContent", style.sample),
            modifier = modifier,
        )
        return
    }

    when (componentId) {
        "button-primary", "button-tonal", "button-secondary" -> CustomButton(
            text = propsAccessor.string(mergedProps, "label", "Button"),
            onClick = {},
            style = PlaygroundComponentProps.buttonStyle(componentId),
            enabled = propsAccessor.boolean(mergedProps, "enabled", true),
            modifier = modifier,
        )
        "icon-button" -> Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(OemSpacing.sm)) {
            CustomIconButton(
                Icons.Default.Settings,
                "Settings",
                {},
                style = IconButtonStyle.Standard,
                enabled = propsAccessor.boolean(mergedProps, "enabled", true),
            )
            val badgeCount = propsAccessor.int(mergedProps, "badgeCount", 2)
            if (badgeCount > 0) CustomBadge(count = badgeCount)
        }
        "fab" -> CustomFab(
            Icons.Default.Add,
            "Add",
            {},
            size = FabSize.Standard,
            modifier = modifier,
        )
        "extended-fab" -> CustomExtendedFab(
            propsAccessor.string(mergedProps, "label", "Navigate"),
            Icons.Default.Navigation,
            {},
            modifier = modifier,
        )
        "filter-chip" -> CustomChip(
            label = propsAccessor.string(mergedProps, "label", "Climate"),
            selected = propsAccessor.boolean(mergedProps, "selected", false),
            onClick = {},
            enabled = propsAccessor.boolean(mergedProps, "enabled", true),
            modifier = modifier,
        )
        "assist-chip" -> CustomAssistChip(
            propsAccessor.string(mergedProps, "label", "Add stop"),
            {},
            leadingIcon = Icons.Default.Add,
            modifier = modifier,
        )
        "suggestion-chip" -> CustomSuggestionChip(
            propsAccessor.string(mergedProps, "label", "Home"),
            {},
            modifier = modifier,
        )
        "input-chip" -> CustomInputChip(
            propsAccessor.string(mergedProps, "label", "Eco Mode"),
            propsAccessor.boolean(mergedProps, "selected", false),
            {},
            modifier = modifier,
        )
        "switch" -> CustomSwitch(
            propsAccessor.string(mergedProps, "label", "Auto climate"),
            propsAccessor.boolean(mergedProps, "checked", true),
            {},
            enabled = propsAccessor.boolean(mergedProps, "enabled", true),
            modifier = modifier,
        )
        "checkbox" -> CustomCheckbox(
            propsAccessor.string(mergedProps, "label", "Heated seats"),
            propsAccessor.boolean(mergedProps, "checked", false),
            {},
            enabled = propsAccessor.boolean(mergedProps, "enabled", true),
            modifier = modifier,
        )
        "radio" -> CustomRadioButton(
            propsAccessor.string(mergedProps, "label", "Standard mode"),
            propsAccessor.boolean(mergedProps, "selected", true),
            {},
            enabled = propsAccessor.boolean(mergedProps, "enabled", true),
            modifier = modifier,
        )
        "segmented-button" -> {
            val options = propsAccessor.optionsList(mergedProps, "options", listOf("Off", "Auto", "Max"))
            val selectedIndex = propsAccessor.int(mergedProps, "selectedIndex", 1)
                .coerceIn(0, (options.size - 1).coerceAtLeast(0))
            CustomSegmentedButtonRow(
                options = options,
                selectedIndex = selectedIndex,
                onOptionSelected = {},
                modifier = modifier,
            )
        }
        "text-field" -> CustomTextField(
            value = propsAccessor.string(mergedProps, "value", ""),
            onValueChange = {},
            label = propsAccessor.string(mergedProps, "fieldLabel", "Destination"),
            placeholder = propsAccessor.string(mergedProps, "placeholder", "Enter address"),
            enabled = propsAccessor.boolean(mergedProps, "enabled", true),
            modifier = modifier,
        )
        "search-bar" -> CustomSearchBar(
            query = propsAccessor.string(mergedProps, "query", ""),
            onQueryChange = {},
            onSearch = {},
            placeholder = propsAccessor.string(mergedProps, "placeholder", "Search destinations"),
            modifier = modifier,
        )
        "slider" -> {
            val min = propsAccessor.float(mergedProps, "valueMin", 16f)
            val max = propsAccessor.float(mergedProps, "valueMax", 30f)
            val range = if (min < max) min..max else 16f..30f
            CustomSlider(
                value = propsAccessor.float(mergedProps, "value", 22f).coerceIn(range),
                onValueChange = {},
                label = propsAccessor.string(mergedProps, "label", "Temperature °C"),
                valueRange = range,
                modifier = modifier,
            )
        }
        "card" -> CustomCard(modifier = modifier, onClick = {}) {
            Text(
                propsAccessor.string(mergedProps, "title", "Climate"),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                propsAccessor.string(mergedProps, "subtitle", "22°C"),
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
        "metric-card" -> CustomMetricCard(
            propsAccessor.string(mergedProps, "label", "Range"),
            propsAccessor.string(mergedProps, "value", "287"),
            propsAccessor.string(mergedProps, "unit", "km"),
            modifier = modifier,
        )
        "list-tile" -> CustomListTile(
            propsAccessor.string(mergedProps, "title", "Navigation"),
            subtitle = propsAccessor.string(mergedProps, "subtitle", "Home — 12 min"),
            leadingIcon = Icons.Default.Navigation,
            showChevron = propsAccessor.boolean(mergedProps, "showChevron", true),
            onClick = {},
            modifier = modifier,
        )
        "image" -> CustomImage(
            contentDescription = propsAccessor.string(mergedProps, "contentDescription", "Vehicle"),
            modifier = modifier,
            size = OemSpacing.xl * 2,
        )
        "tabs" -> {
            val tabs = propsAccessor.optionsList(mergedProps, "options", listOf("Overview", "Details", "Settings"))
            val selectedIndex = propsAccessor.int(mergedProps, "selectedIndex", 0)
                .coerceIn(0, (tabs.size - 1).coerceAtLeast(0))
            CustomTabs(
                tabs = tabs,
                selectedIndex = selectedIndex,
                onTabSelected = {},
                modifier = modifier,
            )
        }
        "status-indicator" -> CustomStatusIndicator(
            propsAccessor.string(mergedProps, "label", "Systems OK"),
            propsAccessor.statusLevel(mergedProps),
            modifier = modifier,
        )
        "linear-progress" -> CustomLinearProgress(
            progress = { propsAccessor.float(mergedProps, "progress", 0.65f).coerceIn(0f, 1f) },
            label = propsAccessor.string(mergedProps, "label", "Battery charge"),
            modifier = modifier.padding(vertical = OemSpacing.sm),
        )
        "circular-progress" -> CustomCircularProgress(
            label = propsAccessor.string(mergedProps, "label", "Syncing…"),
            modifier = modifier,
        )
        "snackbar" -> CustomSnackbarMessage(
            message = propsAccessor.string(mergedProps, "message", "Route updated"),
            actionLabel = propsAccessor.string(mergedProps, "actionLabel", "Undo"),
            modifier = modifier,
        )
        "empty-state" -> CustomEmptyState(
            icon = Icons.Default.Search,
            title = propsAccessor.string(mergedProps, "title", "No results"),
            message = propsAccessor.string(mergedProps, "message", "Try a different search term"),
            modifier = modifier,
        )
        "dialog-trigger" -> {
            var show by remember { mutableStateOf(false) }
            CustomButton(
                text = propsAccessor.string(mergedProps, "triggerLabel", "Show dialog"),
                onClick = { show = true },
                modifier = modifier,
            )
            if (show) {
                CustomDialog(
                    title = propsAccessor.string(mergedProps, "title", "Enable ProPILOT?"),
                    message = propsAccessor.string(
                        mergedProps,
                        "message",
                        "Driver assistance will activate on supported roads.",
                    ),
                    confirmText = propsAccessor.string(mergedProps, "confirmText", "Enable"),
                    dismissText = propsAccessor.string(mergedProps, "dismissText", "Cancel"),
                    onConfirm = { show = false },
                    onDismiss = { show = false },
                )
            }
        }
    }
}

@Composable
private fun PlaygroundTextRenderer(
    style: PlaygroundTextStyle,
    text: String,
    modifier: Modifier = Modifier,
) {
    val textStyle = when (style) {
        PlaygroundTextStyle.Display -> MaterialTheme.typography.displayLarge
        PlaygroundTextStyle.HeadlineLarge -> MaterialTheme.typography.headlineLarge
        PlaygroundTextStyle.HeadlineMedium -> MaterialTheme.typography.headlineMedium
        PlaygroundTextStyle.TitleLarge -> MaterialTheme.typography.titleLarge
        PlaygroundTextStyle.TitleMedium -> MaterialTheme.typography.titleMedium
        PlaygroundTextStyle.BodyLarge -> MaterialTheme.typography.bodyLarge
        PlaygroundTextStyle.BodyMedium -> MaterialTheme.typography.bodyMedium
        PlaygroundTextStyle.LabelLarge -> MaterialTheme.typography.labelLarge
        PlaygroundTextStyle.LabelMedium -> MaterialTheme.typography.labelMedium
    }
    Text(
        text = text,
        style = textStyle,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = modifier,
        maxLines = 5,
        overflow = TextOverflow.Ellipsis,
    )
}
