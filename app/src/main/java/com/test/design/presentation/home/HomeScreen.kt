package com.test.design.presentation.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import com.test.design.component.components.CustomFeatureCard
import com.test.design.component.components.CustomTopBar
import com.test.design.component.theme.OemSpacing
import com.test.design.domain.model.FeatureDemo
import com.test.design.presentation.home.component.HomeBrandingPanel
import com.test.design.presentation.home.component.HomeCategoryChips
import com.test.design.presentation.home.component.HomeHeroSection
import com.test.design.presentation.home.component.SystemInfoPanel
import com.test.design.presentation.home.mapper.mapFeatureIcon
import com.test.design.presentation.home.mapper.mapToSystemInfoUiState
import com.test.design.presentation.shared.GlobalDrivingUxPanel
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
                CustomTopBar(title = "Oem AAOS")
                HomeCategoryChips(
                    categories = state.categories,
                    selectedCategory = state.selectedCategory,
                    onCategorySelected = onCategorySelected,
                    modifier = Modifier.padding(horizontal = OemSpacing.md),
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
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = OemSpacing.md),
        verticalArrangement = Arrangement.spacedBy(OemSpacing.md),
    ) {
        item {
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
        GlobalDrivingUxPanel(modifier = Modifier.padding(bottom = OemSpacing.lg))
        HomeBrandingPanel(
            demoCount = state.features.size,
            selectedCategory = state.selectedCategory,
        )
        SystemInfoPanel(
            state = systemInfo,
            modifier = Modifier.padding(top = OemSpacing.lg),
        )
    }
}
