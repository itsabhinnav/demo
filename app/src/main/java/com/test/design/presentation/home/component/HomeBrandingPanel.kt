package com.test.design.presentation.home.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.test.design.component.theme.OemBorder
import com.test.design.component.theme.OemOnSurface
import com.test.design.component.theme.OemOnSurfaceVariant
import com.test.design.component.theme.OemRed
import com.test.design.component.theme.OemSpacing
import com.test.design.component.theme.OemSurfaceElevated
import com.test.design.component.theme.OemVisuals
import com.test.design.component.theme.oemSurfaceBorder
import com.test.design.domain.model.DemoCategory

@Composable
fun HomeBrandingPanel(
    demoCount: Int,
    selectedCategory: DemoCategory,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            OemBrandMark()
            Column(modifier = Modifier.padding(start = OemSpacing.md)) {
                Text(
                    text = "Oem",
                    style = MaterialTheme.typography.headlineLarge,
                    color = OemRed,
                )
                Text(
                    text = "AAOS Playground",
                    style = MaterialTheme.typography.labelMedium,
                    color = OemOnSurfaceVariant,
                )
            }
        }
        Spacer(modifier = Modifier.height(OemSpacing.lg))
        StatRow(label = "Demos", value = demoCount.toString())
        StatRow(label = "Category", value = selectedCategory.label)
        StatRow(label = "Modules", value = "3")
        Spacer(modifier = Modifier.height(OemSpacing.md))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(OemRed.copy(alpha = 0.25f)),
        )
        Spacer(modifier = Modifier.height(OemSpacing.md))
        Text(
            text = "DRIVER-SAFE UI",
            style = MaterialTheme.typography.labelMedium,
            color = OemRed,
        )
        Text(
            text = "48dp touch targets, 4.5:1 contrast, dark-first Oem theme for in-vehicle displays.",
            style = MaterialTheme.typography.bodyMedium,
            color = OemOnSurfaceVariant,
            modifier = Modifier.padding(top = OemSpacing.xs),
        )
    }
}

@Composable
private fun OemBrandMark(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(OemSpacing.xl + OemSpacing.md)
            .clip(OemVisuals.iconContainerShape)
            .background(OemVisuals.primaryGradient)
            .oemSurfaceBorder(OemVisuals.iconContainerShape, OemBorder),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "O",
            style = MaterialTheme.typography.headlineLarge,
            color = OemOnSurface,
        )
    }
}

@Composable
private fun StatRow(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = OemSpacing.xs)
            .clip(OemVisuals.cardShape)
            .background(OemSurfaceElevated)
            .oemSurfaceBorder(OemVisuals.cardShape, OemBorder)
            .padding(horizontal = OemSpacing.md, vertical = OemSpacing.sm),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .width(OemVisuals.accentBarWidth)
                .height(OemSpacing.lg)
                .background(OemRed),
        )
        Column(modifier = Modifier.padding(start = OemSpacing.sm)) {
            Text(
                text = label.uppercase(),
                style = MaterialTheme.typography.labelMedium,
                color = OemOnSurfaceVariant,
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                color = OemOnSurface,
            )
        }
    }
}
