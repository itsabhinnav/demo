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
        }
    }

    private fun loadHome() {
        val features = repository.getAll()
        updateState {
            copy(
                features = features,
                filteredFeatures = features,
                categories = repository.getCategories(),
                isLoading = false,
            )
        }
    }

    private fun onCategorySelected(category: DemoCategory) {
        val filtered = filterByCategory(state.value.features, category)
        updateState { copy(selectedCategory = category, filteredFeatures = filtered) }
    }

    private fun onFeatureSelected(featureId: String) {
        emitEffect(HomeEffect.NavigateToDemo(featureId))
    }

    private fun filterByCategory(features: List<FeatureDemo>, category: DemoCategory): List<FeatureDemo> {
        if (category == DemoCategory.All) return features
        return features.filter { it.category == category }
    }
}
