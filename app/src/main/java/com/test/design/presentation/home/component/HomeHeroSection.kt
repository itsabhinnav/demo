package com.test.design.presentation.home.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.test.design.component.components.CustomChip
import com.test.design.component.theme.OemOnSurface
import com.test.design.component.theme.OemOnSurfaceVariant
import com.test.design.component.theme.OemRed
import com.test.design.component.theme.OemSpacing
import com.test.design.component.theme.OemVisuals
import com.test.design.domain.model.DemoCategory

@Composable
fun HomeCategoryChips(
    categories: List<DemoCategory>,
    selectedCategory: DemoCategory,
    onCategorySelected: (DemoCategory) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(OemSpacing.sm),
    ) {
        items(categories, key = { it.name }) { category ->
            CustomChip(
                label = category.label,
                selected = category == selectedCategory,
                onClick = { onCategorySelected(category) },
            )
        }
    }
}

@Composable
fun HomeHeroSection(
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = OemSpacing.md),
    ) {
        Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .width(OemVisuals.accentBarWidth)
                    .height(OemSpacing.xl)
                    .background(OemRed),
            )
            Text(
                text = title,
                style = MaterialTheme.typography.displayLarge,
                color = OemOnSurface,
                modifier = Modifier.padding(start = OemSpacing.sm),
            )
        }
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyLarge,
            color = OemOnSurfaceVariant,
            modifier = Modifier.padding(
                top = OemSpacing.sm,
                start = OemVisuals.accentBarWidth + OemSpacing.sm,
            ),
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = OemSpacing.md)
                .height(2.dp)
                .background(OemRed.copy(alpha = 0.3f)),
        )
    }
}
