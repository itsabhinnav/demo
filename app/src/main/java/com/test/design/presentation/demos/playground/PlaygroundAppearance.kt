package com.test.design.presentation.demos.playground

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import com.test.design.component.theme.OemBackground
import com.test.design.component.theme.OemBorder
import com.test.design.component.theme.OemGrayDark
import com.test.design.component.theme.OemOnSurface
import com.test.design.component.theme.OemPrimary
import com.test.design.component.theme.OemSurface
import com.test.design.component.theme.OemSurfaceElevated
import com.test.design.component.theme.OemSurfaceVariant
import com.test.design.component.theme.OemWhite

enum class PlaygroundColorToken(val label: String, val color: Color?) {
    Default("Default", null),
    Surface("Surface", OemSurface),
    Elevated("Elevated", OemSurfaceElevated),
    Variant("Variant", OemSurfaceVariant),
    Gray("Gray", OemGrayDark),
    White("White", OemWhite),
    Primary("Primary", OemPrimary),
    Background("Background", OemBackground),
    Border("Border", OemBorder),
}

enum class PlaygroundTypographyToken(val label: String) {
    Default("Default"),
    Display("Display"),
    HeadlineLarge("Headline Large"),
    HeadlineMedium("Headline Medium"),
    TitleLarge("Title Large"),
    TitleMedium("Title Medium"),
    BodyLarge("Body Large"),
    BodyMedium("Body Medium"),
    LabelLarge("Label Large"),
    LabelMedium("Label Medium"),
}

object PlaygroundAppearance {

    val colorTokens: List<PlaygroundColorToken> = PlaygroundColorToken.entries
    val typographyTokens: List<PlaygroundTypographyToken> = PlaygroundTypographyToken.entries

    val schema: List<PlaygroundPropertyDefinition> = listOf(
        PlaygroundPropertyDefinition(
            key = "bgColor",
            label = "Background",
            type = PlaygroundPropertyType.Color,
            defaultValue = PlaygroundColorToken.Default.name,
            enumOptions = colorTokens.map { it.name },
        ),
        PlaygroundPropertyDefinition(
            key = "textColor",
            label = "Text color",
            type = PlaygroundPropertyType.Color,
            defaultValue = PlaygroundColorToken.Default.name,
            enumOptions = colorTokens.map { it.name },
        ),
        PlaygroundPropertyDefinition(
            key = "borderColor",
            label = "Border color",
            type = PlaygroundPropertyType.Color,
            defaultValue = PlaygroundColorToken.Default.name,
            enumOptions = colorTokens.map { it.name },
        ),
        PlaygroundPropertyDefinition(
            key = "cornerRadiusDp",
            label = "Corner radius",
            type = PlaygroundPropertyType.Float,
            defaultValue = "12",
            floatRange = 0f..32f,
        ),
        PlaygroundPropertyDefinition(
            key = "paddingDp",
            label = "Padding",
            type = PlaygroundPropertyType.Float,
            defaultValue = "0",
            floatRange = 0f..48f,
        ),
        PlaygroundPropertyDefinition(
            key = "marginDp",
            label = "Margin",
            type = PlaygroundPropertyType.Float,
            defaultValue = "0",
            floatRange = 0f..48f,
        ),
        PlaygroundPropertyDefinition(
            key = "borderWidthDp",
            label = "Border width",
            type = PlaygroundPropertyType.Float,
            defaultValue = "0",
            floatRange = 0f..8f,
        ),
        PlaygroundPropertyDefinition(
            key = "fontScale",
            label = "Font scale",
            type = PlaygroundPropertyType.Float,
            defaultValue = "1",
            floatRange = 0.5f..2f,
        ),
        PlaygroundPropertyDefinition(
            key = "typography",
            label = "Font style",
            type = PlaygroundPropertyType.Enum,
            defaultValue = PlaygroundTypographyToken.Default.name,
            enumOptions = typographyTokens.map { it.name },
        ),
        PlaygroundPropertyDefinition(
            key = "opacity",
            label = "Opacity",
            type = PlaygroundPropertyType.Float,
            defaultValue = "1",
            floatRange = 0.1f..1f,
        ),
    )

    val defaultProps: Map<String, String> = schema.associate { it.key to it.defaultValue }

    fun color(props: Map<String, String>, key: String, fallback: Color = Color.Unspecified): Color {
        val token = runCatching {
            PlaygroundColorToken.valueOf(PlaygroundComponentProps.string(props, key, PlaygroundColorToken.Default.name))
        }.getOrDefault(PlaygroundColorToken.Default)
        return token.color ?: fallback
    }

    fun optionalColor(props: Map<String, String>, key: String): Color? =
        color(props, key, fallback = Color.Unspecified).takeUnless { it == Color.Unspecified }

    @Composable
    fun textStyle(props: Map<String, String>, base: TextStyle): TextStyle {
        val scale = PlaygroundComponentProps.float(props, "fontScale", 1f).coerceIn(0.5f, 2f)
        val typography = runCatching {
            PlaygroundTypographyToken.valueOf(
                PlaygroundComponentProps.string(props, "typography", PlaygroundTypographyToken.Default.name),
            )
        }.getOrDefault(PlaygroundTypographyToken.Default)

        val resolved = resolveTypography(typography, base)
        return if (scale == 1f) resolved else resolved.copy(fontSize = resolved.fontSize * scale)
    }

    @Composable
    private fun resolveTypography(token: PlaygroundTypographyToken, base: TextStyle): TextStyle =
        when (token) {
            PlaygroundTypographyToken.Default -> base
            PlaygroundTypographyToken.Display -> MaterialTheme.typography.displayLarge
            PlaygroundTypographyToken.HeadlineLarge -> MaterialTheme.typography.headlineLarge
            PlaygroundTypographyToken.HeadlineMedium -> MaterialTheme.typography.headlineMedium
            PlaygroundTypographyToken.TitleLarge -> MaterialTheme.typography.titleLarge
            PlaygroundTypographyToken.TitleMedium -> MaterialTheme.typography.titleMedium
            PlaygroundTypographyToken.BodyLarge -> MaterialTheme.typography.bodyLarge
            PlaygroundTypographyToken.BodyMedium -> MaterialTheme.typography.bodyMedium
            PlaygroundTypographyToken.LabelLarge -> MaterialTheme.typography.labelLarge
            PlaygroundTypographyToken.LabelMedium -> MaterialTheme.typography.labelMedium
        }

    fun textColor(props: Map<String, String>): Color {
        val override = optionalColor(props, "textColor")
        return override ?: OemOnSurface
    }

    @Composable
    fun Box(
        props: Map<String, String>,
        modifier: Modifier = Modifier,
        content: @Composable () -> Unit,
    ) {
        val cornerRadius = PlaygroundComponentProps.float(props, "cornerRadiusDp", 12f).coerceIn(0f, 32f)
        val padding = PlaygroundComponentProps.float(props, "paddingDp", 0f).coerceIn(0f, 48f)
        val margin = PlaygroundComponentProps.float(props, "marginDp", 0f).coerceIn(0f, 48f)
        val borderWidth = PlaygroundComponentProps.float(props, "borderWidthDp", 0f).coerceIn(0f, 8f)
        val opacity = PlaygroundComponentProps.float(props, "opacity", 1f).coerceIn(0.1f, 1f)
        val shape: Shape = RoundedCornerShape(cornerRadius.dp)
        val background = optionalColor(props, "bgColor")
        val borderColor = optionalColor(props, "borderColor") ?: OemBorder
        val contentColor = optionalColor(props, "textColor")

        Box(
            modifier = modifier
                .padding(margin.dp)
                .alpha(opacity)
                .clip(shape)
                .then(if (background != null) Modifier.background(background) else Modifier)
                .then(
                    if (borderWidth > 0f) {
                        Modifier.border(borderWidth.dp, borderColor, shape)
                    } else {
                        Modifier
                    },
                )
                .padding(padding.dp),
        ) {
            if (contentColor != null) {
                CompositionLocalProvider(LocalContentColor provides contentColor) {
                    content()
                }
            } else {
                content()
            }
        }
    }
}
