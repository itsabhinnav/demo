package com.test.design.presentation.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import com.test.design.component.components.CustomFeatureCard
import com.test.design.component.components.CustomTopBar
import com.test.design.component.theme.NissanSpacing
import com.test.design.domain.model.FeatureDemo
import com.test.design.presentation.home.component.HomeBrandingPanel
import com.test.design.presentation.home.component.HomeCategoryChips
import com.test.design.presentation.home.component.HomeHeroSection
import com.test.design.presentation.home.component.SystemInfoPanel
import com.test.design.presentation.home.mapper.mapFeatureIcon
import com.test.design.presentation.home.mapper.mapToSystemInfoUiState
import com.test.design.template.AutomotiveDashboardTemplate
import com.test.design.template.LocalAutomotiveWindowInfo

@Composable
fun HomeScreen(
    state: HomeState,
    onFeatureClick: (FeatureDemo) -> Unit,
    onCategorySelected: (com.test.design.domain.model.DemoCategory) -> Unit,
    modifier: Modifier = Modifier,
) {
    AutomotiveDashboardTemplate(
        modifier = modifier,
        blueZone = {
            Column(modifier = Modifier.fillMaxSize()) {
                CustomTopBar(title = "Nissan AAOS")
                HomeCategoryChips(
                    categories = state.categories,
                    selectedCategory = state.selectedCategory,
                    onCategorySelected = onCategorySelected,
                    modifier = Modifier.padding(horizontal = NissanSpacing.md),
                )
            }
        },
        greenZone = {
            HomeContent(
                state = state,
                onFeatureClick = onFeatureClick,
            )
        },
        yellowZone = {
            HomeYellowPanel(state = state)
        },
    )
}

@Composable
private fun HomeContent(
    state: HomeState,
    onFeatureClick: (FeatureDemo) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = NissanSpacing.xl * 8),
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = NissanSpacing.md),
        horizontalArrangement = Arrangement.spacedBy(NissanSpacing.md),
        verticalArrangement = Arrangement.spacedBy(NissanSpacing.md),
    ) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            HomeHeroSection(
                title = state.title,
                subtitle = state.subtitle,
            )
        }
        items(state.filteredFeatures, key = { it.id }) { feature ->
            CustomFeatureCard(
                title = feature.title,
                description = feature.description,
                icon = mapFeatureIcon(feature),
                categoryLabel = feature.tagline,
                onClick = { onFeatureClick(feature) },
            )
        }
    }
}

@Composable
private fun HomeYellowPanel(state: HomeState) {
    val windowInfo = LocalAutomotiveWindowInfo.current
    val density = LocalDensity.current.density
    val systemInfo = mapToSystemInfoUiState(windowInfo, density)

    Column(modifier = Modifier.fillMaxSize()) {
        HomeBrandingPanel(
            demoCount = state.features.size,
            selectedCategory = state.selectedCategory,
        )
        SystemInfoPanel(
            state = systemInfo,
            modifier = Modifier.padding(top = NissanSpacing.lg),
        )
    }
}
