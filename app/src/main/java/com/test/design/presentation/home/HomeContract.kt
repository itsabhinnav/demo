package com.test.design.presentation.home

import com.test.design.domain.model.DemoCategory
import com.test.design.domain.model.FeatureDemo

data class HomeState(
    val title: String = "Oem Design Playground",
    val subtitle: String = "Explore AAOS UI components, tokens, and adaptive layouts",
    val features: List<FeatureDemo> = emptyList(),
    val filteredFeatures: List<FeatureDemo> = emptyList(),
    val categories: List<DemoCategory> = emptyList(),
    val selectedCategory: DemoCategory = DemoCategory.All,
    val searchQuery: String = "",
    val isLoading: Boolean = true,
)

sealed interface HomeIntent {
    data object Load : HomeIntent
    data class FeatureSelected(val featureId: String) : HomeIntent
    data class CategorySelected(val category: DemoCategory) : HomeIntent
    data class SearchQueryChanged(val query: String) : HomeIntent
}

sealed interface HomeEffect {
    data class NavigateToDemo(val featureId: String) : HomeEffect
}
