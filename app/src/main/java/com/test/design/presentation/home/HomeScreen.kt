package com.test.design.presentation.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.test.design.domain.model.FeatureDemo
import com.test.design.template.AutomotiveDashboardTemplate

@Composable
fun HomeScreen(
    state: HomeState,
    onFeatureClick: (FeatureDemo) -> Unit,
    onCategorySelected: (com.test.design.domain.model.DemoCategory) -> Unit,
    onSearchQueryChanged: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    AutomotiveDashboardTemplate(
        modifier = modifier,
        blueZone = {},
        greenZone = {
            Box(modifier = Modifier.fillMaxSize())
        },
        yellowZone = {
            Box(modifier = Modifier.fillMaxSize())
        },
        showBlueZone = false,
    )
}
