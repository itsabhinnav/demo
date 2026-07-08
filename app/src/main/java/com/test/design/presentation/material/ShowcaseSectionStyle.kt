package com.test.design.presentation.material

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.test.design.theme.CarDesignTokens

data class ShowcaseSectionStyle(
    val containerColor: @Composable () -> Color,
    val titleColor: @Composable () -> Color,
    val descriptionColor: @Composable () -> Color,
    val shape: @Composable () -> Shape,
    val border: @Composable () -> BorderStroke?,
    val contentPadding: PaddingValues = PaddingValues(CarDesignTokens.SectionPadding),
) {
    companion object {
        val Default = ShowcaseSectionStyle(
            containerColor = { MaterialTheme.colorScheme.surfaceContainerLow },
            titleColor = { MaterialTheme.colorScheme.onSurface },
            descriptionColor = { MaterialTheme.colorScheme.onSurfaceVariant },
            shape = { MaterialTheme.shapes.large },
            border = { null },
        )

        val OemBranded = ShowcaseSectionStyle(
            containerColor = { MaterialTheme.colorScheme.surfaceContainer },
            titleColor = { MaterialTheme.colorScheme.onSurface },
            descriptionColor = { MaterialTheme.colorScheme.onSurfaceVariant },
            shape = { MaterialTheme.shapes.large },
            border = {
                BorderStroke(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outlineVariant,
                )
            },
        )
    }
}

val LocalShowcaseSectionStyle = compositionLocalOf { ShowcaseSectionStyle.Default }

@Composable
fun ComponentSection(
    title: String,
    description: String,
    content: @Composable () -> Unit,
) {
    val style = LocalShowcaseSectionStyle.current
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = style.shape(),
        color = style.containerColor(),
        border = style.border(),
    ) {
        Column(
            modifier = Modifier.padding(style.contentPadding),
            verticalArrangement = Arrangement.spacedBy(CarDesignTokens.TouchTargetSpacing),
        ) {
            Text(text = title, style = MaterialTheme.typography.titleMedium, color = style.titleColor())
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = style.descriptionColor(),
            )
            content()
        }
    }
}
