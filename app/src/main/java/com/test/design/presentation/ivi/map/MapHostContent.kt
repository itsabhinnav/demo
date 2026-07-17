package com.test.design.presentation.ivi.map

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.test.design.presentation.ivi.IviExpressiveTheme
import com.test.design.presentation.ivi.navigation.NavigationScreen
import com.test.design.presentation.ivi.navigation.NavigationViewModel

/**
 * Standalone map/navigation host for [MapActivity] — Scalable UI `map_panel` content.
 * SystemUI owns status/nav bars; do not pad for Compose fake chrome.
 */
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun MapHostContent(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    navigationViewModel: NavigationViewModel = viewModel(),
) {
    val navigationState by navigationViewModel.state.collectAsStateWithLifecycle()

    IviExpressiveTheme {
        SharedTransitionLayout(modifier = modifier.fillMaxSize()) {
            AnimatedVisibility(
                visible = true,
                modifier = Modifier.fillMaxSize(),
                enter = EnterTransition.None,
                exit = ExitTransition.None,
                label = "map_host",
            ) {
                Box(modifier = Modifier.fillMaxSize()) {
                    NavigationScreen(
                        uiState = navigationState,
                        onEvent = navigationViewModel::onEvent,
                        onBack = onBack,
                        animatedVisibilityScope = this@AnimatedVisibility,
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            }
        }
    }
}
