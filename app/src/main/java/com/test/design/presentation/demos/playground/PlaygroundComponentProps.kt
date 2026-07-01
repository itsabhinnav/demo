package com.test.design.presentation.demos.playground

import com.test.design.component.components.ButtonStyle
import com.test.design.component.components.StatusLevel

enum class PlaygroundPropertyType {
    Text,
    Boolean,
    Enum,
    Float,
    Int,
}

data class PlaygroundPropertyDefinition(
    val key: String,
    val label: String,
    val type: PlaygroundPropertyType,
    val defaultValue: String,
    val enumOptions: List<String> = emptyList(),
    val floatRange: ClosedFloatingPointRange<Float>? = null,
)

object PlaygroundComponentProps {

    fun schemaFor(componentId: String): List<PlaygroundPropertyDefinition> =
        schemas[baseComponentId(componentId)].orEmpty()

    fun defaultProps(componentId: String): Map<String, String> =
        schemaFor(componentId).associate { it.key to it.defaultValue }

    fun mergeWithDefaults(componentId: String, props: Map<String, String>): Map<String, String> {
        val defaults = defaultProps(componentId)
        return defaults + props.filterKeys { key -> defaults.containsKey(key) }
    }

    fun boolean(props: Map<String, String>, key: String, default: Boolean = false): Boolean =
        props[key]?.toBooleanStrictOrNull() ?: default

    fun float(props: Map<String, String>, key: String, default: Float): Float =
        props[key]?.toFloatOrNull() ?: default

    fun int(props: Map<String, String>, key: String, default: Int): Int =
        props[key]?.toIntOrNull() ?: default

    fun string(props: Map<String, String>, key: String, default: String): String =
        props[key]?.takeIf { it.isNotEmpty() } ?: default

    fun optionsList(props: Map<String, String>, key: String, default: List<String>): List<String> =
        props[key]
            ?.split(",")
            ?.map { it.trim() }
            ?.filter { it.isNotEmpty() }
            ?.takeIf { it.isNotEmpty() }
            ?: default

    fun statusLevel(props: Map<String, String>, key: String = "statusLevel"): StatusLevel =
        runCatching { StatusLevel.valueOf(string(props, key, StatusLevel.Normal.name)) }
            .getOrDefault(StatusLevel.Normal)

    fun buttonStyle(componentId: String): ButtonStyle = when (componentId) {
        "button-tonal" -> ButtonStyle.Tonal
        "button-secondary" -> ButtonStyle.Secondary
        "button-text" -> ButtonStyle.Text
        "button-destructive" -> ButtonStyle.Destructive
        else -> ButtonStyle.Primary
    }

    private fun baseComponentId(componentId: String): String = when {
        PlaygroundCatalog.isTextComponent(componentId) -> "text"
        else -> componentId
    }

    private val schemas: Map<String, List<PlaygroundPropertyDefinition>> = mapOf(
        "button-primary" to buttonSchema("Primary"),
        "button-tonal" to buttonSchema("Tonal"),
        "button-secondary" to buttonSchema("Secondary"),
        "icon-button" to listOf(
            bool("enabled", "Enabled", true),
            int("badgeCount", "Badge count", 2, 0..99),
        ),
        "fab" to listOf(
            bool("enabled", "Enabled", true),
        ),
        "extended-fab" to listOf(
            text("label", "Label", "Navigate"),
            bool("enabled", "Enabled", true),
        ),
        "filter-chip" to chipSchema("Climate"),
        "assist-chip" to listOf(
            text("label", "Label", "Add stop"),
            bool("enabled", "Enabled", true),
        ),
        "suggestion-chip" to listOf(
            text("label", "Label", "Home"),
            bool("enabled", "Enabled", true),
        ),
        "input-chip" to chipSchema("Eco Mode"),
        "switch" to selectionSchema("Auto climate", checkedKey = "checked", defaultChecked = true),
        "checkbox" to selectionSchema("Heated seats", checkedKey = "checked"),
        "radio" to selectionSchema("Standard mode", checkedKey = "selected", defaultChecked = true),
        "segmented-button" to listOf(
            text("options", "Options (comma-separated)", "Off,Auto,Max"),
            int("selectedIndex", "Selected option", 1, 0..2),
        ),
        "text-field" to listOf(
            text("fieldLabel", "Label", "Destination"),
            text("placeholder", "Placeholder", "Enter address"),
            text("value", "Value", ""),
            bool("enabled", "Enabled", true),
        ),
        "search-bar" to listOf(
            text("placeholder", "Placeholder", "Search destinations"),
            text("query", "Query", ""),
        ),
        "slider" to listOf(
            text("label", "Label", "Temperature °C"),
            floatProp("value", "Value", 22f, 16f..30f),
            floatProp("valueMin", "Minimum", 16f, 0f..50f),
            floatProp("valueMax", "Maximum", 30f, 0f..50f),
        ),
        "card" to listOf(
            text("title", "Title", "Climate"),
            text("subtitle", "Subtitle", "22°C"),
        ),
        "metric-card" to listOf(
            text("label", "Label", "Range"),
            text("value", "Value", "287"),
            text("unit", "Unit", "km"),
        ),
        "list-tile" to listOf(
            text("title", "Title", "Navigation"),
            text("subtitle", "Subtitle", "Home — 12 min"),
            bool("showChevron", "Show chevron", true),
        ),
        "image" to listOf(
            text("contentDescription", "Content description", "Vehicle"),
        ),
        "tabs" to listOf(
            text("options", "Tab labels (comma-separated)", "Overview,Details,Settings"),
            int("selectedIndex", "Selected tab", 0, 0..5),
        ),
        "status-indicator" to listOf(
            text("label", "Label", "Systems OK"),
            enumProp("statusLevel", "Status level", StatusLevel.Normal.name, StatusLevel.entries.map { it.name }),
        ),
        "linear-progress" to listOf(
            text("label", "Label", "Battery charge"),
            floatProp("progress", "Progress", 0.65f, 0f..1f),
        ),
        "circular-progress" to listOf(
            text("label", "Label", "Syncing…"),
        ),
        "snackbar" to listOf(
            text("message", "Message", "Route updated"),
            text("actionLabel", "Action label", "Undo"),
        ),
        "empty-state" to listOf(
            text("title", "Title", "No results"),
            text("message", "Message", "Try a different search term"),
        ),
        "dialog-trigger" to listOf(
            text("triggerLabel", "Button label", "Show dialog"),
            text("title", "Dialog title", "Enable ProPILOT?"),
            text("message", "Dialog message", "Driver assistance will activate on supported roads."),
            text("confirmText", "Confirm text", "Enable"),
            text("dismissText", "Dismiss text", "Cancel"),
        ),
        "text" to listOf(
            text("textContent", "Text content", "Sample text"),
        ),
    )

    private fun buttonSchema(defaultLabel: String) = listOf(
        text("label", "Label", defaultLabel),
        bool("enabled", "Enabled", true),
    )

    private fun chipSchema(defaultLabel: String) = listOf(
        text("label", "Label", defaultLabel),
        bool("selected", "Selected", false),
        bool("enabled", "Enabled", true),
    )

    private fun selectionSchema(
        defaultLabel: String,
        checkedKey: String,
        defaultChecked: Boolean = false,
    ) = listOf(
        text("label", "Label", defaultLabel),
        bool(checkedKey, if (checkedKey == "selected") "Selected" else "Checked", defaultChecked),
        bool("enabled", "Enabled", true),
    )

    private fun text(key: String, label: String, default: String) =
        PlaygroundPropertyDefinition(key, label, PlaygroundPropertyType.Text, default)

    private fun bool(key: String, label: String, default: Boolean) =
        PlaygroundPropertyDefinition(key, label, PlaygroundPropertyType.Boolean, default.toString())

    private fun enumProp(key: String, label: String, default: String, options: List<String>) =
        PlaygroundPropertyDefinition(key, label, PlaygroundPropertyType.Enum, default, enumOptions = options)

    private fun floatProp(
        key: String,
        label: String,
        default: Float,
        range: ClosedFloatingPointRange<Float>,
    ) = PlaygroundPropertyDefinition(
        key = key,
        label = label,
        type = PlaygroundPropertyType.Float,
        defaultValue = default.toString(),
        floatRange = range,
    )

    private fun int(key: String, label: String, default: Int, range: IntRange) =
        PlaygroundPropertyDefinition(
            key = key,
            label = label,
            type = PlaygroundPropertyType.Int,
            defaultValue = default.toString(),
            floatRange = range.first.toFloat()..range.last.toFloat(),
        )
}
