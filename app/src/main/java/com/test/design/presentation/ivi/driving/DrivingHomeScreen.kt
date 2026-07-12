package com.test.design.presentation.ivi.driving

import androidx.activity.compose.BackHandler
import androidx.activity.compose.PredictiveBackHandler
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
import com.test.design.presentation.ivi.climate.ClimateControlScreen
import com.test.design.presentation.ivi.climate.ClimateViewModel
import com.test.design.presentation.ivi.dashboard.DashboardEvent
import com.test.design.presentation.ivi.dashboard.DashboardViewModel
import com.test.design.presentation.ivi.dashboard.components.DrivingDashboardLayout
import com.test.design.presentation.ivi.dashboard.model.DashboardWidget
import com.test.design.presentation.ivi.media.MediaPlayerScreen
import com.test.design.presentation.ivi.media.MediaViewModel
import com.test.design.presentation.ivi.navigation.NavigationScreen
import com.test.design.presentation.ivi.navigation.NavigationViewModel
import com.test.design.presentation.ivi.vehicle.VehicleScreen
import com.test.design.presentation.ivi.vehicle.VehicleViewModel
import com.test.design.presentation.material.CustomizedMaterialComponentsScreen
import com.test.design.presentation.material.MaterialComponentsScreen
import com.test.design.presentation.settings.SettingsScreen

/**
 * Tesla-like map-first driving home — start destination.
 * Opens the original widget-list dashboard via [onOpenWidgetDashboard].
 */
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun DrivingHomeScreen(
    onOpenWidgetDashboard: () -> Unit,
    modifier: Modifier = Modifier,
    dashboardViewModel: DashboardViewModel = viewModel(),
    climateViewModel: ClimateViewModel = viewModel(),
    mediaViewModel: MediaViewModel = viewModel(),
    navigationViewModel: NavigationViewModel = viewModel(),
    vehicleViewModel: VehicleViewModel = viewModel(),
) {
    val dashboardState by dashboardViewModel.state.collectAsStateWithLifecycle()
    val climateState by climateViewModel.state.collectAsStateWithLifecycle()
    val mediaState by mediaViewModel.state.collectAsStateWithLifecycle()
    val navigationState by navigationViewModel.state.collectAsStateWithLifecycle()
    val vehicleState by vehicleViewModel.state.collectAsStateWithLifecycle()

    IviExpressiveTheme {
        val collapseWidget = { dashboardViewModel.onEvent(DashboardEvent.CollapseWidget) }
        BackHandler(enabled = dashboardState.expandedWidget != null, onBack = collapseWidget)
        PredictiveBackHandler(enabled = dashboardState.expandedWidget != null) { progress ->
            try {
                progress.collect { }
                collapseWidget()
            } catch (_: kotlinx.coroutines.CancellationException) {
                // Gesture cancelled — keep expanded state.
            }
        }

        SharedTransitionLayout(modifier = modifier.fillMaxSize()) {
            AnimatedContent(
                targetState = dashboardState.expandedWidget,
                modifier = Modifier.fillMaxSize(),
                transitionSpec = {
                    EnterTransition.None togetherWith ExitTransition.None
                },
                label = "driving_home_container_transform",
            ) { expandedWidget ->
                when (expandedWidget) {
                    null -> DrivingDashboardLayout(
                        vehicleState = vehicleState,
                        navigationState = navigationState,
                        mediaState = mediaState,
                        climateState = climateState,
                        climateTemperature = climateViewModel.activeTemperature(),
                        onEvent = dashboardViewModel::onEvent,
                        onMediaEvent = mediaViewModel::onEvent,
                        onClimateEvent = climateViewModel::onEvent,
                        onOpenWidgetDashboard = onOpenWidgetDashboard,
                        animatedVisibilityScope = this@AnimatedContent,
                        modifier = Modifier.fillMaxSize(),
                    )
                    DashboardWidget.Climate -> ClimateControlScreen(
                        uiState = climateState,
                        activeTemperature = climateViewModel.activeTemperature(),
                        onEvent = climateViewModel::onEvent,
                        onBack = collapseWidget,
                        animatedVisibilityScope = this@AnimatedContent,
                    )
                    DashboardWidget.Media -> MediaPlayerScreen(
                        uiState = mediaState,
                        onEvent = mediaViewModel::onEvent,
                        onBack = collapseWidget,
                        animatedVisibilityScope = this@AnimatedContent,
                    )
                    DashboardWidget.Navigation -> NavigationScreen(
                        uiState = navigationState,
                        onEvent = navigationViewModel::onEvent,
                        onBack = collapseWidget,
                        animatedVisibilityScope = this@AnimatedContent,
                    )
                    DashboardWidget.Vehicle -> VehicleScreen(
                        uiState = vehicleState,
                        onEvent = vehicleViewModel::onEvent,
                        onBack = collapseWidget,
                        animatedVisibilityScope = this@AnimatedContent,
                    )
                    DashboardWidget.MaterialComponents -> MaterialComponentsScreen(
                        onBack = collapseWidget,
                    )
                    DashboardWidget.CustomizedMaterial -> CustomizedMaterialComponentsScreen(
                        onBack = collapseWidget,
                    )
                    DashboardWidget.Settings -> SettingsScreen(
                        onBack = collapseWidget,
                        animatedVisibilityScope = this@AnimatedContent,
                    )
                }
            }
        }
    }
}
