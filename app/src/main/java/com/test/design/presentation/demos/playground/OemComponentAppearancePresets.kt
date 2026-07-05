package com.test.design.presentation.demos.playground

/**
 * Per-component OEM appearance defaults that exercise every playground property
 * so previews look distinctly different from Material 3 stock styling.
 */
object OemComponentAppearancePresets {

    private val base = PlaygroundAppearance.schema.associate { it.key to it.defaultValue }

    fun forComponent(componentId: String): Map<String, String> {
        val resolvedId = when {
            PlaygroundCatalog.isTextComponent(componentId) -> "text"
            else -> componentId
        }
        return base + (presets[resolvedId] ?: categoryFallback(resolvedId))
    }

    private fun categoryFallback(componentId: String): Map<String, String> {
        val category = PlaygroundCatalog.findById(componentId)?.category
        return when (category) {
            "Actions" -> actionsPreset()
            "Selection" -> selectionPreset()
            "Input" -> inputPreset()
            "Display" -> displayPreset()
            "Feedback" -> feedbackPreset()
            "Navigation" -> navigationPreset()
            "Text" -> textPreset()
            else -> emptyMap()
        }
    }

    private fun actionsPreset() = mapOf(
        "bgColor" to PlaygroundColorToken.Variant.name,
        "borderColor" to PlaygroundColorToken.Border.name,
        "cornerRadiusDp" to "12",
        "paddingDp" to "8",
        "marginDp" to "4",
        "borderWidthDp" to "1",
        "typography" to PlaygroundTypographyToken.LabelLarge.name,
    )

    private fun selectionPreset() = mapOf(
        "bgColor" to PlaygroundColorToken.Surface.name,
        "borderColor" to PlaygroundColorToken.Border.name,
        "cornerRadiusDp" to "14",
        "paddingDp" to "12",
        "marginDp" to "4",
        "borderWidthDp" to "1",
        "typography" to PlaygroundTypographyToken.BodyLarge.name,
    )

    private fun inputPreset() = mapOf(
        "bgColor" to PlaygroundColorToken.Variant.name,
        "borderColor" to PlaygroundColorToken.Border.name,
        "cornerRadiusDp" to "14",
        "paddingDp" to "12",
        "marginDp" to "4",
        "borderWidthDp" to "1",
        "typography" to PlaygroundTypographyToken.BodyLarge.name,
    )

    private fun displayPreset() = mapOf(
        "bgColor" to PlaygroundColorToken.Elevated.name,
        "borderColor" to PlaygroundColorToken.Border.name,
        "cornerRadiusDp" to "16",
        "paddingDp" to "12",
        "marginDp" to "4",
        "borderWidthDp" to "1",
        "typography" to PlaygroundTypographyToken.TitleMedium.name,
    )

    private fun feedbackPreset() = mapOf(
        "bgColor" to PlaygroundColorToken.Elevated.name,
        "borderColor" to PlaygroundColorToken.Gray.name,
        "cornerRadiusDp" to "16",
        "paddingDp" to "16",
        "marginDp" to "4",
        "borderWidthDp" to "1",
        "typography" to PlaygroundTypographyToken.BodyLarge.name,
    )

    private fun navigationPreset() = mapOf(
        "bgColor" to PlaygroundColorToken.Surface.name,
        "borderColor" to PlaygroundColorToken.Border.name,
        "cornerRadiusDp" to "14",
        "paddingDp" to "8",
        "marginDp" to "4",
        "borderWidthDp" to "1",
        "typography" to PlaygroundTypographyToken.LabelLarge.name,
    )

    private fun textPreset() = mapOf(
        "textColor" to PlaygroundColorToken.Default.name,
        "cornerRadiusDp" to "8",
        "paddingDp" to "8",
        "marginDp" to "4",
        "borderWidthDp" to "0",
        "fontScale" to "1",
        "opacity" to "1",
    )

    private val presets: Map<String, Map<String, String>> = mapOf(
        "button-primary" to mapOf(
            "bgColor" to PlaygroundColorToken.Variant.name,
            "borderColor" to PlaygroundColorToken.Border.name,
            "cornerRadiusDp" to "12",
            "paddingDp" to "8",
            "marginDp" to "4",
            "borderWidthDp" to "1",
            "typography" to PlaygroundTypographyToken.LabelLarge.name,
        ),
        "button-tonal" to mapOf(
            "bgColor" to PlaygroundColorToken.Surface.name,
            "borderColor" to PlaygroundColorToken.Gray.name,
            "cornerRadiusDp" to "12",
            "paddingDp" to "8",
            "marginDp" to "4",
            "borderWidthDp" to "1",
            "typography" to PlaygroundTypographyToken.LabelLarge.name,
        ),
        "button-secondary" to mapOf(
            "bgColor" to PlaygroundColorToken.Background.name,
            "borderColor" to PlaygroundColorToken.Primary.name,
            "cornerRadiusDp" to "12",
            "paddingDp" to "8",
            "marginDp" to "4",
            "borderWidthDp" to "2",
            "typography" to PlaygroundTypographyToken.LabelLarge.name,
        ),
        "icon-button" to mapOf(
            "bgColor" to PlaygroundColorToken.Variant.name,
            "borderColor" to PlaygroundColorToken.Border.name,
            "cornerRadiusDp" to "12",
            "paddingDp" to "8",
            "marginDp" to "4",
            "borderWidthDp" to "1",
        ),
        "fab" to mapOf(
            "bgColor" to PlaygroundColorToken.Gray.name,
            "borderColor" to PlaygroundColorToken.Primary.name,
            "cornerRadiusDp" to "16",
            "paddingDp" to "4",
            "marginDp" to "8",
            "borderWidthDp" to "1",
        ),
        "extended-fab" to mapOf(
            "bgColor" to PlaygroundColorToken.Variant.name,
            "borderColor" to PlaygroundColorToken.Border.name,
            "cornerRadiusDp" to "16",
            "paddingDp" to "8",
            "marginDp" to "4",
            "borderWidthDp" to "1",
            "typography" to PlaygroundTypographyToken.LabelLarge.name,
        ),
        "filter-chip" to mapOf(
            "bgColor" to PlaygroundColorToken.Surface.name,
            "borderColor" to PlaygroundColorToken.Border.name,
            "cornerRadiusDp" to "12",
            "paddingDp" to "4",
            "marginDp" to "2",
            "borderWidthDp" to "1",
            "typography" to PlaygroundTypographyToken.LabelLarge.name,
        ),
        "assist-chip" to mapOf(
            "bgColor" to PlaygroundColorToken.Elevated.name,
            "borderColor" to PlaygroundColorToken.Border.name,
            "cornerRadiusDp" to "12",
            "paddingDp" to "4",
            "marginDp" to "2",
            "borderWidthDp" to "1",
        ),
        "suggestion-chip" to mapOf(
            "bgColor" to PlaygroundColorToken.Variant.name,
            "borderColor" to PlaygroundColorToken.Gray.name,
            "cornerRadiusDp" to "12",
            "paddingDp" to "4",
            "marginDp" to "2",
            "borderWidthDp" to "1",
        ),
        "input-chip" to mapOf(
            "bgColor" to PlaygroundColorToken.Surface.name,
            "borderColor" to PlaygroundColorToken.Primary.name,
            "cornerRadiusDp" to "12",
            "paddingDp" to "4",
            "marginDp" to "2",
            "borderWidthDp" to "1",
        ),
        "switch" to mapOf(
            "bgColor" to PlaygroundColorToken.Surface.name,
            "borderColor" to PlaygroundColorToken.Border.name,
            "cornerRadiusDp" to "14",
            "paddingDp" to "12",
            "marginDp" to "4",
            "borderWidthDp" to "1",
            "typography" to PlaygroundTypographyToken.BodyLarge.name,
        ),
        "checkbox" to mapOf(
            "bgColor" to PlaygroundColorToken.Surface.name,
            "borderColor" to PlaygroundColorToken.Border.name,
            "cornerRadiusDp" to "14",
            "paddingDp" to "12",
            "marginDp" to "4",
            "borderWidthDp" to "1",
        ),
        "radio" to mapOf(
            "bgColor" to PlaygroundColorToken.Surface.name,
            "borderColor" to PlaygroundColorToken.Border.name,
            "cornerRadiusDp" to "14",
            "paddingDp" to "12",
            "marginDp" to "4",
            "borderWidthDp" to "1",
        ),
        "segmented-button" to mapOf(
            "bgColor" to PlaygroundColorToken.Variant.name,
            "borderColor" to PlaygroundColorToken.Border.name,
            "cornerRadiusDp" to "14",
            "paddingDp" to "8",
            "marginDp" to "4",
            "borderWidthDp" to "1",
        ),
        "text-field" to mapOf(
            "bgColor" to PlaygroundColorToken.Variant.name,
            "borderColor" to PlaygroundColorToken.Border.name,
            "cornerRadiusDp" to "14",
            "paddingDp" to "12",
            "marginDp" to "4",
            "borderWidthDp" to "1",
            "typography" to PlaygroundTypographyToken.BodyLarge.name,
        ),
        "search-bar" to mapOf(
            "bgColor" to PlaygroundColorToken.Elevated.name,
            "borderColor" to PlaygroundColorToken.Gray.name,
            "cornerRadiusDp" to "14",
            "paddingDp" to "12",
            "marginDp" to "4",
            "borderWidthDp" to "1",
            "typography" to PlaygroundTypographyToken.BodyLarge.name,
        ),
        "slider" to mapOf(
            "bgColor" to PlaygroundColorToken.Surface.name,
            "borderColor" to PlaygroundColorToken.Border.name,
            "cornerRadiusDp" to "14",
            "paddingDp" to "16",
            "marginDp" to "4",
            "borderWidthDp" to "1",
            "typography" to PlaygroundTypographyToken.TitleMedium.name,
        ),
        "card" to mapOf(
            "bgColor" to PlaygroundColorToken.Elevated.name,
            "borderColor" to PlaygroundColorToken.Border.name,
            "cornerRadiusDp" to "18",
            "paddingDp" to "12",
            "marginDp" to "4",
            "borderWidthDp" to "1",
            "typography" to PlaygroundTypographyToken.TitleMedium.name,
        ),
        "metric-card" to mapOf(
            "bgColor" to PlaygroundColorToken.Elevated.name,
            "borderColor" to PlaygroundColorToken.Gray.name,
            "cornerRadiusDp" to "18",
            "paddingDp" to "16",
            "marginDp" to "4",
            "borderWidthDp" to "1",
            "typography" to PlaygroundTypographyToken.Display.name,
            "fontScale" to "0.85",
        ),
        "list-tile" to mapOf(
            "bgColor" to PlaygroundColorToken.Elevated.name,
            "borderColor" to PlaygroundColorToken.Border.name,
            "cornerRadiusDp" to "16",
            "paddingDp" to "8",
            "marginDp" to "4",
            "borderWidthDp" to "1",
            "typography" to PlaygroundTypographyToken.TitleMedium.name,
        ),
        "image" to mapOf(
            "bgColor" to PlaygroundColorToken.Variant.name,
            "borderColor" to PlaygroundColorToken.Border.name,
            "cornerRadiusDp" to "12",
            "paddingDp" to "8",
            "marginDp" to "4",
            "borderWidthDp" to "1",
        ),
        "tabs" to mapOf(
            "bgColor" to PlaygroundColorToken.Surface.name,
            "borderColor" to PlaygroundColorToken.Border.name,
            "cornerRadiusDp" to "14",
            "paddingDp" to "8",
            "marginDp" to "4",
            "borderWidthDp" to "1",
            "typography" to PlaygroundTypographyToken.LabelLarge.name,
        ),
        "status-indicator" to mapOf(
            "bgColor" to PlaygroundColorToken.Variant.name,
            "borderColor" to PlaygroundColorToken.Border.name,
            "cornerRadiusDp" to "10",
            "paddingDp" to "12",
            "marginDp" to "4",
            "borderWidthDp" to "1",
            "typography" to PlaygroundTypographyToken.BodyMedium.name,
        ),
        "linear-progress" to mapOf(
            "bgColor" to PlaygroundColorToken.Surface.name,
            "borderColor" to PlaygroundColorToken.Border.name,
            "cornerRadiusDp" to "12",
            "paddingDp" to "16",
            "marginDp" to "4",
            "borderWidthDp" to "1",
            "typography" to PlaygroundTypographyToken.LabelMedium.name,
        ),
        "circular-progress" to mapOf(
            "bgColor" to PlaygroundColorToken.Variant.name,
            "borderColor" to PlaygroundColorToken.Border.name,
            "cornerRadiusDp" to "16",
            "paddingDp" to "16",
            "marginDp" to "4",
            "borderWidthDp" to "1",
            "typography" to PlaygroundTypographyToken.BodyMedium.name,
        ),
        "snackbar" to mapOf(
            "bgColor" to PlaygroundColorToken.Elevated.name,
            "borderColor" to PlaygroundColorToken.Primary.name,
            "cornerRadiusDp" to "14",
            "paddingDp" to "8",
            "marginDp" to "4",
            "borderWidthDp" to "1",
            "typography" to PlaygroundTypographyToken.BodyLarge.name,
        ),
        "empty-state" to mapOf(
            "bgColor" to PlaygroundColorToken.Surface.name,
            "borderColor" to PlaygroundColorToken.Gray.name,
            "cornerRadiusDp" to "18",
            "paddingDp" to "24",
            "marginDp" to "4",
            "borderWidthDp" to "1",
            "typography" to PlaygroundTypographyToken.HeadlineMedium.name,
            "opacity" to "0.95",
        ),
        "dialog-trigger" to mapOf(
            "bgColor" to PlaygroundColorToken.Variant.name,
            "borderColor" to PlaygroundColorToken.Border.name,
            "cornerRadiusDp" to "16",
            "paddingDp" to "16",
            "marginDp" to "4",
            "borderWidthDp" to "1",
            "typography" to PlaygroundTypographyToken.BodyLarge.name,
        ),
        "text" to mapOf(
            "textColor" to PlaygroundColorToken.Default.name,
            "cornerRadiusDp" to "8",
            "paddingDp" to "8",
            "marginDp" to "4",
            "borderWidthDp" to "0",
            "fontScale" to "1",
            "opacity" to "1",
        ),
    )
}
