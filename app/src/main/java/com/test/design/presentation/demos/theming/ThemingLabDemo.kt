package com.test.design.presentation.demos.theming

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.test.design.component.components.CustomCard
import com.test.design.component.components.CustomSectionHeader
import com.test.design.component.components.CustomSlider
import com.test.design.component.theme.OemOnSurfaceVariant
import com.test.design.component.theme.OemSpacing
import com.test.design.presentation.demos.shared.DemoScaffold
import com.test.design.presentation.demos.shared.DemoTipsPanel

@Composable
fun ThemingLabDemo(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var spacingScale by remember { mutableFloatStateOf(1f) }
    var typeScale by remember { mutableFloatStateOf(1f) }

    DemoScaffold(
        title = "Theming Lab",
        onBack = onBack,
        modifier = modifier,
        yellowContent = {
            DemoTipsPanel(
                tips = listOf(
                    "Preview token changes before updating OemSpacing or OemTypography",
                    "Production theme uses fixed tokens — this lab simulates what-if scenarios",
                    "Share screenshots with engineering when proposing token updates",
                ),
            )
        },
    ) {
        CustomSectionHeader(
            title = "Spacing Scale",
            subtitle = "Multiplier applied to preview padding and gaps (${"%.1f".format(spacingScale)}×)",
        )
        CustomSlider(
            value = spacingScale,
            onValueChange = { spacingScale = it },
            valueRange = 0.75f..1.5f,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = OemSpacing.sm),
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = OemSpacing.md),
            horizontalArrangement = Arrangement.spacedBy((OemSpacing.md.value * spacingScale).dp),
        ) {
            SpacingPreviewBlock("xs", OemSpacing.xs, spacingScale, Modifier.weight(1f))
            SpacingPreviewBlock("sm", OemSpacing.sm, spacingScale, Modifier.weight(1f))
            SpacingPreviewBlock("md", OemSpacing.md, spacingScale, Modifier.weight(1f))
            SpacingPreviewBlock("lg", OemSpacing.lg, spacingScale, Modifier.weight(1f))
        }

        CustomSectionHeader(
            title = "Typography Scale",
            subtitle = "Preview legibility at ${"%.0f".format(typeScale * 100)}% of baseline",
        )
        CustomSlider(
            value = typeScale,
            onValueChange = { typeScale = it },
            valueRange = 0.85f..1.25f,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = OemSpacing.sm),
        )
        CustomCard(modifier = Modifier.padding(vertical = OemSpacing.md)) {
            Column(verticalArrangement = Arrangement.spacedBy((OemSpacing.sm.value * spacingScale).dp)) {
                ScaledTypePreview("Headline", "Climate control", typeScale, MaterialTheme.typography.headlineMedium.fontSize)
                ScaledTypePreview("Body", "Cabin temperature 21°C", typeScale, MaterialTheme.typography.bodyLarge.fontSize)
                ScaledTypePreview("Label", "AUTO MODE", typeScale, MaterialTheme.typography.labelLarge.fontSize)
            }
        }
        Text(
            text = "Minimum AAOS body text: 20sp · Caption: 16sp",
            style = MaterialTheme.typography.bodySmall,
            color = OemOnSurfaceVariant,
        )
    }
}

@Composable
private fun ScaledTypePreview(
    label: String,
    sample: String,
    scale: Float,
    baseSize: TextUnit,
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = label, style = MaterialTheme.typography.labelMedium, color = OemOnSurfaceVariant)
        Text(
            text = sample,
            fontSize = (baseSize.value * scale).sp,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(top = OemSpacing.xs),
        )
    }
}

@Composable
private fun SpacingPreviewBlock(
    label: String,
    baseSpacing: androidx.compose.ui.unit.Dp,
    scale: Float,
    modifier: Modifier = Modifier,
) {
    val scaled = (baseSpacing.value * scale).dp
    CustomCard(modifier = modifier) {
        Column {
            Text(text = label, style = MaterialTheme.typography.labelLarge)
            Text(
                text = "${scaled.value.toInt()}dp",
                style = MaterialTheme.typography.bodyMedium,
                color = OemOnSurfaceVariant,
                modifier = Modifier.padding(top = OemSpacing.xs),
            )
        }
    }
}
