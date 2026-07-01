package com.test.design.presentation.home

import com.test.design.core.mvi.MviViewModel
import com.test.design.domain.model.DemoCategory
import com.test.design.domain.model.FeatureDemo
import com.test.design.domain.repository.FeatureDemoRepository

class HomeViewModel(
    private val repository: FeatureDemoRepository,
) : MviViewModel<HomeIntent, HomeState, HomeEffect>(HomeState()) {

    override fun handleIntent(intent: HomeIntent) {
        when (intent) {
            is HomeIntent.Load -> loadHome()
            is HomeIntent.FeatureSelected -> onFeatureSelected(intent.featureId)
            is HomeIntent.CategorySelected -> onCategorySelected(intent.category)
            is HomeIntent.SearchQueryChanged -> onSearchQueryChanged(intent.query)
        }
    }

    private fun loadHome() {
        val features = repository.getAll()
        updateState {
            copy(
                features = features,
                filteredFeatures = applyFilters(features, selectedCategory, searchQuery),
                categories = repository.getCategories(),
                isLoading = false,
            )
        }
    }

    private fun onCategorySelected(category: DemoCategory) {
        updateState {
            copy(
                selectedCategory = category,
                filteredFeatures = applyFilters(features, category, searchQuery),
            )
        }
    }

    private fun onSearchQueryChanged(query: String) {
        updateState {
            copy(
                searchQuery = query,
                filteredFeatures = applyFilters(features, selectedCategory, query),
            )
        }
    }

    private fun onFeatureSelected(featureId: String) {
        emitEffect(HomeEffect.NavigateToDemo(featureId))
    }

    private fun applyFilters(
        features: List<FeatureDemo>,
        category: DemoCategory,
        query: String,
    ): List<FeatureDemo> {
        val byCategory = if (category == DemoCategory.All) {
            features
        } else {
            features.filter { it.category == category }
        }
        if (query.isBlank()) return byCategory
        return byCategory.filter { feature ->
            feature.title.contains(query, ignoreCase = true) ||
                feature.description.contains(query, ignoreCase = true) ||
                feature.tagline.contains(query, ignoreCase = true) ||
                feature.id.contains(query, ignoreCase = true)
        }
    }
}
