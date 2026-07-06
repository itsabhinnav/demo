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
import com.test.design.component.theme.OemBackground
import com.test.design.component.theme.OemGray
import com.test.design.component.theme.OemGrayDark
import com.test.design.component.theme.OemGrayLight
import com.test.design.component.theme.OemOnSurfaceVariant
import com.test.design.component.theme.OemSpacing
import com.test.design.component.theme.OemSurface
import com.test.design.component.theme.OemSurfaceElevated
import com.test.design.component.theme.OemSurfaceVariant
import com.test.design.component.theme.OemWhite
import com.test.design.presentation.demos.shared.DemoScaffold

@Composable
fun DesignSystemDemo(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    DemoScaffold(
        title = "Design System",
        onBack = onBack,
        modifier = modifier,
        yellowContent = {},
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
        subtitle = "Monochrome tokens for AAOS dark theme",
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = OemSpacing.md),
        horizontalArrangement = Arrangement.spacedBy(OemSpacing.md),
    ) {
        CustomColorSwatch("Black", "#000000", OemBackground, "Base", Modifier.weight(1f))
        CustomColorSwatch("White", "#FFFFFF", OemWhite, "Primary", Modifier.weight(1f))
        CustomColorSwatch("Gray", "#6B6B6B", OemGray, "Muted", Modifier.weight(1f))
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = OemSpacing.md),
        horizontalArrangement = Arrangement.spacedBy(OemSpacing.md),
    ) {
        CustomColorSwatch("Surface", "#141414", OemSurface, "Cards", Modifier.weight(1f))
        CustomColorSwatch("Elevated", "#1C1C1C", OemSurfaceElevated, "Raised", Modifier.weight(1f))
        CustomColorSwatch("Variant", "#2A2A2A", OemSurfaceVariant, "Zones", Modifier.weight(1f))
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = OemSpacing.md),
        horizontalArrangement = Arrangement.spacedBy(OemSpacing.md),
    ) {
        CustomColorSwatch("Gray Dark", "#3A3A3A", OemGrayDark, "Border", Modifier.weight(1f))
        CustomColorSwatch("Gray Light", "#9E9E9E", OemGrayLight, "Caption", Modifier.weight(1f))
    }
}

@Composable
private fun TypographySection() {
    CustomSectionHeader(title = "Typography", subtitle = "Clean sans-serif scale for in-vehicle legibility")
    CustomTypographySample("Display Large", "Oem Design Playground")
    CustomTypographySample("Headline Medium", "Feature demonstrations")
    CustomTypographySample("Title Large", "Section headers and cards")
    CustomTypographySample("Body Large", "Primary content for driver-readable text.")
    CustomTypographySample("Label Medium", "Captions and metadata")
}

@Composable
private fun SpacingSection() {
    CustomSectionHeader(title = "Spacing", subtitle = "4dp grid with 76dp AAOS touch targets")
    CustomSpacingSample("XS — 4dp", OemSpacing.xs)
    CustomSpacingSample("SM — 8dp", OemSpacing.sm)
    CustomSpacingSample("MD — 16dp", OemSpacing.md)
    CustomSpacingSample("LG — 24dp", OemSpacing.lg)
    CustomSpacingSample("Touch target — 76dp", OemSpacing.minTouchTarget)
    CustomSpacingSample("Driving target — 84dp", OemSpacing.drivingTouchTarget)
    CustomSpacingSample("Restricted target — 88dp", OemSpacing.restrictedTouchTarget)
}

@Composable
private fun ShapesSection() {
    CustomSectionHeader(title = "Shapes", subtitle = "Square corners from OemShapes")
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = OemSpacing.md),
        horizontalArrangement = Arrangement.spacedBy(OemSpacing.lg),
    ) {
        ShapeSample("Small", MaterialTheme.shapes.small)
        ShapeSample("Medium", MaterialTheme.shapes.medium)
        ShapeSample("Large", MaterialTheme.shapes.large)
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
                .size(OemSpacing.xl * 2)
                .background(OemSurfaceVariant, shape),
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = OemOnSurfaceVariant,
            modifier = Modifier.padding(top = OemSpacing.sm),
        )
    }
}
