package com.test.design.presentation.home

import androidx.compose.runtime.Composable
import com.test.design.component.theme.OemTheme
import com.test.design.domain.model.DemoCategory
import com.test.design.domain.model.FeatureDemo
import com.test.design.template.preview.AutomotivePreviews

@AutomotivePreviews
@Composable
private fun HomeScreenPreview() {
    OemTheme {
        HomeScreen(
            state = HomeState(
                features = listOf(
                    FeatureDemo("1", "Design System", "Tokens", DemoCategory.DesignSystem, "OEM"),
                    FeatureDemo("2", "Components", "Gallery", DemoCategory.Components, "UI Kit"),
                ),
                filteredFeatures = listOf(
                    FeatureDemo("1", "Design System", "Tokens", DemoCategory.DesignSystem, "OEM"),
                    FeatureDemo("2", "Components", "Gallery", DemoCategory.Components, "UI Kit"),
                ),
                categories = DemoCategory.entries,
                isLoading = false,
            ),
            onFeatureClick = {},
            onCategorySelected = {},
        )
    }
}
