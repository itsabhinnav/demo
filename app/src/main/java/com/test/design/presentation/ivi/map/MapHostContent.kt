package com.test.design.presentation.ivi.map

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.test.design.presentation.ivi.IviExpressiveTheme
import com.test.design.presentation.ivi.navigation.NavigationScreen
import com.test.design.presentation.ivi.navigation.NavigationViewModel
import com.test.design.theme.CarDesignTokens
import com.test.design.theme.carTouchTarget

/**
 * Standalone map/navigation host for [MapActivity] — same UI as tapping
 * "Search maps" on the driving home screen.
 *
 * Back finishes Maps only (returns to the previous task / car launcher).
 * Optional Home opens Design ([MainActivity]) when the user explicitly asks.
 */
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun MapHostContent(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    showRoute: Boolean = true,
    onOpenDesign: (() -> Unit)? = null,
    navigationViewModel: NavigationViewModel = viewModel(),
) {
    val navigationState by navigationViewModel.state.collectAsStateWithLifecycle()

    BackHandler(onBack = onBack)

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
                    showRoute = showRoute,
                    trailingHeaderContent = {
                        if (onOpenDesign != null) {
                            IconButton(
                                onClick = onOpenDesign,
                                modifier = Modifier.carTouchTarget(),
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Home,
                                    contentDescription = "Open Design",
                                    modifier = Modifier.size(CarDesignTokens.PrimaryIcon),
                                    tint = MaterialTheme.colorScheme.onBackground,
                                )
                            }
                        }
                    },
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }
}
