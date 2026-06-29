package com.test.design.presentation.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun HomeRoute(
    onNavigateToDemo: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = viewModel(factory = HomeViewModelFactory()),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.onIntent(HomeIntent.Load)
    }

    LaunchedEffect(viewModel) {
        viewModel.effect.collect { effect ->
            handleHomeEffect(effect, onNavigateToDemo)
        }
    }

    HomeScreen(
        state = state,
        onFeatureClick = { feature ->
            viewModel.onIntent(HomeIntent.FeatureSelected(feature.id))
        },
        onCategorySelected = { category ->
            viewModel.onIntent(HomeIntent.CategorySelected(category))
        },
        modifier = modifier,
    )
}

private fun handleHomeEffect(
    effect: HomeEffect,
    onNavigateToDemo: (String) -> Unit,
) {
    when (effect) {
        is HomeEffect.NavigateToDemo -> onNavigateToDemo(effect.featureId)
    }
}
