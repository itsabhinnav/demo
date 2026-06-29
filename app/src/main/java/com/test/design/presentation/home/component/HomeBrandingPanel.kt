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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.test.design.component.components.CustomSectionHeader
import com.test.design.component.components.CustomStatRow
import com.test.design.component.theme.OemOnPrimary
import com.test.design.component.theme.OemOnSurface
import com.test.design.component.theme.OemOnSurfaceVariant
import com.test.design.component.theme.OemPrimary
import com.test.design.component.theme.OemSpacing
import com.test.design.component.theme.OemVisuals
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
                    color = OemOnSurface,
                )
                Text(
                    text = "AAOS Playground",
                    style = MaterialTheme.typography.bodyMedium,
                    color = OemOnSurfaceVariant,
                )
            }
        }
        Spacer(modifier = Modifier.height(OemSpacing.lg))
        CustomStatRow(label = "Demos", value = demoCount.toString())
        CustomStatRow(label = "Category", value = selectedCategory.label)
        CustomStatRow(label = "Modules", value = "3")
        Spacer(modifier = Modifier.height(OemSpacing.md))
        CustomSectionHeader(
            title = "Driver-safe UI",
            subtitle = "76dp touch targets, 4.5:1 contrast, monochrome theme for in-vehicle displays.",
        )
    }
}

@Composable
private fun OemBrandMark(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(OemSpacing.xl + OemSpacing.md)
            .clip(OemVisuals.iconContainerShape)
            .background(OemPrimary),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = "O",
            style = MaterialTheme.typography.headlineLarge,
            color = OemOnPrimary,
        )
    }
}
