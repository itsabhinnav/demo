package com.test.design.presentation.home

import androidx.compose.runtime.Composable
import com.test.design.component.theme.OemTheme
import com.test.design.template.preview.AutomotivePreviews

@AutomotivePreviews
@Composable
private fun HomeScreenPreview() {
    OemTheme {
        HomeScreen(
            state = HomeState(isLoading = false),
            onFeatureClick = {},
            onCategorySelected = {},
            onSearchQueryChanged = {},
        )
    }
}
