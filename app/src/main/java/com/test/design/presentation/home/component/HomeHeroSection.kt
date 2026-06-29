package com.test.design.presentation.home.component

import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.test.design.component.components.CustomChip
import com.test.design.component.components.CustomSectionHeader
import com.test.design.component.theme.OemSpacing
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
        horizontalArrangement = Arrangement.spacedBy(OemSpacing.sm),
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
        CustomSectionHeader(title = title, subtitle = subtitle)
    }
}
