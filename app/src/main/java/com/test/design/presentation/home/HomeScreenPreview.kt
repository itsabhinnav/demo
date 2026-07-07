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
                isLoading = false,
                features = previewFeatures,
                filteredFeatures = previewFeatures,
                categories = listOf(
                    DemoCategory.All,
                    DemoCategory.DesignSystem,
                    DemoCategory.Components,
                ),
            ),
            onFeatureClick = {},
            onCategorySelected = {},
            onSearchQueryChanged = {},
        )
    }
}

private val previewFeatures = listOf(
    FeatureDemo(
        id = "expressive-motion",
        title = "Expressive Components",
        description = "M3 MotionScheme springs on Jetpack components.",
        category = DemoCategory.DesignSystem,
        tagline = "Component spring physics",
    ),
    FeatureDemo(
        id = "motion-transition-patterns",
        title = "Transition Patterns",
        description = "Container transform, lateral, enter/exit, and skeleton loaders.",
        category = DemoCategory.DesignSystem,
        tagline = "Screen transitions",
    ),
)
