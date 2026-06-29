package com.test.design.presentation.demos.designsystem

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import com.test.design.component.components.CustomColorSwatch
import com.test.design.component.components.CustomSectionHeader
import com.test.design.component.components.CustomSpacingSample
import com.test.design.component.components.CustomTypographySample
import com.test.design.component.theme.NissanCarAccent
import com.test.design.component.theme.NissanBackground
import com.test.design.component.theme.NissanOnSurfaceVariant
import com.test.design.component.theme.NissanRed
import com.test.design.component.theme.NissanRedDark
import com.test.design.component.theme.NissanSpacing
import com.test.design.component.theme.NissanSurface
import com.test.design.component.theme.NissanSurfaceVariant
import com.test.design.presentation.demos.shared.DemoScaffold
import com.test.design.presentation.demos.shared.DemoTipsPanel

@Composable
fun DesignSystemDemo(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    DemoScaffold(
        title = "Design System",
        onBack = onBack,
        modifier = modifier,
        yellowContent = {
            DemoTipsPanel(
                tips = listOf(
                    "Build from black for day/night driving",
                    "Maintain 4.5:1 contrast minimum",
                    "Use Nissan Red sparingly as accent",
                    "Typography scaled for in-car legibility",
                ),
            )
        },
    ) {
        ColorPaletteSection()
        TypographySection()
        SpacingSection()
        ShapesSection()
    }
}

@Composable
private fun ColorPaletteSection() {
    CustomSectionHeader(
        title = "Color Palette",
        subtitle = "Nissan OEM tokens for AAOS dark theme",
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = NissanSpacing.md),
        horizontalArrangement = Arrangement.spacedBy(NissanSpacing.md),
    ) {
        CustomColorSwatch("Nissan Red", "#C3002F", NissanRed, "Primary", Modifier.weight(1f))
        CustomColorSwatch("Red Dark", "#9A0025", NissanRedDark, "Container", Modifier.weight(1f))
        CustomColorSwatch("Car Accent", "#8AB4F8", NissanCarAccent, "Info", Modifier.weight(1f))
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = NissanSpacing.md),
        horizontalArrangement = Arrangement.spacedBy(NissanSpacing.md),
    ) {
        CustomColorSwatch("Background", "#0D0D0D", NissanBackground, "Base", Modifier.weight(1f))
        CustomColorSwatch("Surface", "#1A1A1A", NissanSurface, "Cards", Modifier.weight(1f))
        CustomColorSwatch("Surface Var", "#2D2D2D", NissanSurfaceVariant, "Zones", Modifier.weight(1f))
    }
}

@Composable
private fun TypographySection() {
    CustomSectionHeader(title = "Typography", subtitle = "Material3 type scale — automotive sizes")
    CustomTypographySample("Display Large", "Nissan Design Playground")
    CustomTypographySample("Headline Medium", "Feature demonstrations")
    CustomTypographySample("Title Large", "Section headers and cards")
    CustomTypographySample("Body Large", "Primary content for driver-readable text at 18sp.")
    CustomTypographySample("Label Medium", "CAPTIONS AND METADATA")
}

@Composable
private fun SpacingSection() {
    CustomSectionHeader(title = "Spacing", subtitle = "4dp base grid with 48dp touch targets")
    CustomSpacingSample("XS — 4dp", NissanSpacing.xs)
    CustomSpacingSample("SM — 8dp", NissanSpacing.sm)
    CustomSpacingSample("MD — 16dp", NissanSpacing.md)
    CustomSpacingSample("LG — 24dp", NissanSpacing.lg)
    CustomSpacingSample("Touch target — 48dp", NissanSpacing.minTouchTarget)
}

@Composable
private fun ShapesSection() {
    CustomSectionHeader(title = "Shapes", subtitle = "Corner radii from NissanShapes")
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = NissanSpacing.md),
        horizontalArrangement = Arrangement.spacedBy(NissanSpacing.lg),
    ) {
        ShapeSample("Small 8dp", MaterialTheme.shapes.small)
        ShapeSample("Medium 12dp", MaterialTheme.shapes.medium)
        ShapeSample("Large 16dp", MaterialTheme.shapes.large)
    }
}

@Composable
private fun ShapeSample(
    label: String,
    shape: Shape,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(NissanSpacing.xl * 2)
                .background(NissanSurfaceVariant, shape),
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = NissanOnSurfaceVariant,
            modifier = Modifier.padding(top = NissanSpacing.sm),
        )
    }
}
