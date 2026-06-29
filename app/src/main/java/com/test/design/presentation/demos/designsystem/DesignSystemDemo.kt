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
import com.test.design.component.theme.OemCarAccent
import com.test.design.component.theme.OemBackground
import com.test.design.component.theme.OemOnSurfaceVariant
import com.test.design.component.theme.OemRed
import com.test.design.component.theme.OemRedDark
import com.test.design.component.theme.OemSpacing
import com.test.design.component.theme.OemSurface
import com.test.design.component.theme.OemSurfaceVariant
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
                    "76dp touch targets per Google Design for Driving",
                    "20sp body / 16sp caption minimum while moving",
                    "Use Oem Red sparingly as accent",
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
        subtitle = "Oem tokens for AAOS dark theme",
    )
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = OemSpacing.md),
        horizontalArrangement = Arrangement.spacedBy(OemSpacing.md),
    ) {
        CustomColorSwatch("Oem Red", "#C3002F", OemRed, "Primary", Modifier.weight(1f))
        CustomColorSwatch("Red Dark", "#9A0025", OemRedDark, "Container", Modifier.weight(1f))
        CustomColorSwatch("Car Accent", "#8AB4F8", OemCarAccent, "Info", Modifier.weight(1f))
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = OemSpacing.md),
        horizontalArrangement = Arrangement.spacedBy(OemSpacing.md),
    ) {
        CustomColorSwatch("Background", "#0D0D0D", OemBackground, "Base", Modifier.weight(1f))
        CustomColorSwatch("Surface", "#1A1A1A", OemSurface, "Cards", Modifier.weight(1f))
        CustomColorSwatch("Surface Var", "#2D2D2D", OemSurfaceVariant, "Zones", Modifier.weight(1f))
    }
}

@Composable
private fun TypographySection() {
    CustomSectionHeader(title = "Typography", subtitle = "AAOS glanceable scale — 20sp body, 16sp caption minimum")
    CustomTypographySample("Display Large", "Oem Design Playground")
    CustomTypographySample("Headline Medium", "Feature demonstrations")
    CustomTypographySample("Title Large", "Section headers and cards")
    CustomTypographySample("Body Large", "Primary content for driver-readable text at 20sp.")
    CustomTypographySample("Label Medium", "CAPTIONS AND METADATA AT 16SP")
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
    CustomSectionHeader(title = "Shapes", subtitle = "Corner radii from OemShapes")
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = OemSpacing.md),
        horizontalArrangement = Arrangement.spacedBy(OemSpacing.lg),
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
