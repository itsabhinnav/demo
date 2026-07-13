package com.test.design.presentation.ivi.map

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.togetherWith
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
 * Standalone map/navigation host for [MapActivity] — same UI as tapping
 * "Search maps" on the driving home screen.
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
            AnimatedContent(
                targetState = Unit,
                modifier = Modifier.fillMaxSize(),
                transitionSpec = {
                    EnterTransition.None togetherWith ExitTransition.None
                },
                label = "map_host",
            ) {
                NavigationScreen(
                    uiState = navigationState,
                    onEvent = navigationViewModel::onEvent,
                    onBack = onBack,
                    animatedVisibilityScope = this@AnimatedContent,
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }
}
