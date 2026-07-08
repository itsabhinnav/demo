package com.test.design.presentation.ivi.dashboard

import androidx.activity.compose.BackHandler
import androidx.activity.compose.PredictiveBackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.test.design.presentation.ivi.IviExpressiveTheme
import com.test.design.presentation.ivi.climate.ClimateControlScreen
import com.test.design.presentation.ivi.climate.ClimateViewModel
import com.test.design.presentation.ivi.dashboard.components.ClimateWidgetPreview
import com.test.design.presentation.ivi.dashboard.components.DashboardWidgetCard
import com.test.design.presentation.ivi.dashboard.components.DashboardWidgetGrid
import com.test.design.presentation.ivi.dashboard.components.MediaWidgetPreview
import com.test.design.presentation.ivi.dashboard.components.NavigationWidgetPreview
import com.test.design.presentation.ivi.dashboard.components.VehicleWidgetPreview
import com.test.design.presentation.ivi.dashboard.model.DashboardWidget
import com.test.design.presentation.ivi.media.MediaPlayerScreen
import com.test.design.presentation.ivi.media.MediaViewModel
import com.test.design.presentation.ivi.navigation.NavigationScreen
import com.test.design.presentation.ivi.navigation.NavigationViewModel
import com.test.design.presentation.ivi.vehicle.VehicleScreen
import com.test.design.presentation.ivi.vehicle.VehicleViewModel
import com.test.design.theme.CarDesignTokens
import com.test.design.theme.carTouchTarget

@OptIn(ExperimentalMaterial3Api::class, ExperimentalSharedTransitionApi::class)
@Composable
fun IviDemoScreen(
    onExit: () -> Unit,
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
            Scaffold(
                topBar = {
                    if (dashboardState.expandedWidget == null) {
                        TopAppBar(
                            title = {},
                            navigationIcon = {
                                IconButton(
                                    onClick = onExit,
                                    modifier = Modifier.carTouchTarget(),
                                ) {
                                    Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Exit IVI demo",
                                    )
                                }
                            },
                            colors = TopAppBarDefaults.topAppBarColors(
                                containerColor = MaterialTheme.colorScheme.surface,
                            ),
                        )
                    }
                },
            ) { padding ->
                AnimatedContent(
                    targetState = dashboardState.expandedWidget,
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize(),
                    transitionSpec = {
                        // Let sharedBounds drive the container transform; only cross-fade siblings.
                        EnterTransition.None togetherWith ExitTransition.None
                    },
                    label = "dashboard_container_transform",
                ) { expandedWidget ->
                    when (expandedWidget) {
                        null -> DashboardHubContent(
                            state = dashboardState,
                            onEvent = dashboardViewModel::onEvent,
                            animatedVisibilityScope = this@AnimatedContent,
                            climateMorphExpanded = climateState.isAcEnabled,
                            mediaMorphExpanded = mediaState.isPlaying,
                            vehicleMorphExpanded = vehicleState.driveMode == com.test.design.presentation.ivi.vehicle.DriveMode.Sport ||
                                vehicleState.isCharging,
                            mediaAlbum = mediaState.currentTrack.album,
                            mediaSubtitle = "Now playing · ${mediaState.currentTrack.title}",
                            climateTemperature = climateViewModel.activeTemperature(),
                            climateSubtitle = "${climateViewModel.activeTemperature()}°C · ${climateState.airflowMode.name} airflow",
                            navigationDestination = navigationState.destination,
                            navigationEtaMinutes = navigationState.etaMinutes,
                            navigationSubtitle = "${navigationState.destination} · ${navigationState.etaMinutes} min",
                            vehicleBatteryPercent = vehicleState.batteryPercent,
                            vehicleRangeMiles = vehicleState.rangeMiles,
                            vehicleSubtitle = "${vehicleState.batteryPercent}% charge · ${vehicleState.rangeMiles} mi",
                        )
                        DashboardWidget.Climate -> ClimateControlScreen(
                            uiState = climateState,
                            activeTemperature = climateViewModel.activeTemperature(),
                            onEvent = climateViewModel::onEvent,
                            onBack = collapseWidget,
                            animatedVisibilityScope = this@AnimatedContent,
                            headerSubtitle = "${climateViewModel.activeTemperature()}°C · ${climateState.airflowMode.name} airflow",
                        )
                        DashboardWidget.Media -> MediaPlayerScreen(
                            uiState = mediaState,
                            onEvent = mediaViewModel::onEvent,
                            onBack = collapseWidget,
                            animatedVisibilityScope = this@AnimatedContent,
                            headerSubtitle = "Now playing · ${mediaState.currentTrack.title}",
                        )
                        DashboardWidget.Navigation -> NavigationScreen(
                            uiState = navigationState,
                            onEvent = navigationViewModel::onEvent,
                            onBack = collapseWidget,
                            animatedVisibilityScope = this@AnimatedContent,
                            headerSubtitle = "${navigationState.destination} · ${navigationState.etaMinutes} min",
                        )
                        DashboardWidget.Vehicle -> VehicleScreen(
                            uiState = vehicleState,
                            onEvent = vehicleViewModel::onEvent,
                            onBack = collapseWidget,
                            animatedVisibilityScope = this@AnimatedContent,
                            headerSubtitle = "${vehicleState.batteryPercent}% charge · ${vehicleState.rangeMiles} mi",
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun SharedTransitionScope.DashboardHubContent(
    state: DashboardUiState,
    onEvent: (DashboardEvent) -> Unit,
    animatedVisibilityScope: AnimatedVisibilityScope,
    climateMorphExpanded: Boolean,
    mediaMorphExpanded: Boolean,
    vehicleMorphExpanded: Boolean,
    mediaAlbum: String,
    mediaSubtitle: String,
    climateTemperature: Int,
    climateSubtitle: String,
    navigationDestination: String,
    navigationEtaMinutes: Int,
    navigationSubtitle: String,
    vehicleBatteryPercent: Int,
    vehicleRangeMiles: Int,
    vehicleSubtitle: String,
) {
    DashboardWidgetGrid(
        widgets = state.widgets,
        modifier = Modifier
            .fillMaxSize()
            .padding(CarDesignTokens.ContentPadding),
        widgetContent = { widget, widgetModifier ->
            val subtitle = when (widget) {
                DashboardWidget.Media -> mediaSubtitle
                DashboardWidget.Climate -> climateSubtitle
                DashboardWidget.Navigation -> navigationSubtitle
                DashboardWidget.Vehicle -> vehicleSubtitle
            }
            DashboardWidgetCard(
                widget = widget,
                subtitle = subtitle,
                onClick = { onEvent(DashboardEvent.WidgetTapped(widget)) },
                animatedVisibilityScope = animatedVisibilityScope,
                modifier = widgetModifier,
                morphExpanded = when (widget) {
                    DashboardWidget.Climate -> climateMorphExpanded
                    DashboardWidget.Media -> mediaMorphExpanded
                    DashboardWidget.Vehicle -> vehicleMorphExpanded
                    else -> false
                },
                previewContent = {
                    when (widget) {
                        DashboardWidget.Media -> MediaWidgetPreview(
                            album = mediaAlbum,
                            animatedVisibilityScope = animatedVisibilityScope,
                            playing = mediaMorphExpanded,
                            modifier = Modifier.fillMaxSize(),
                        )
                        DashboardWidget.Climate -> ClimateWidgetPreview(
                            temperature = climateTemperature,
                            animatedVisibilityScope = animatedVisibilityScope,
                            acEnabled = climateMorphExpanded,
                            modifier = Modifier.fillMaxSize(),
                        )
                        DashboardWidget.Navigation -> NavigationWidgetPreview(
                            destination = navigationDestination,
                            etaMinutes = navigationEtaMinutes,
                            animatedVisibilityScope = animatedVisibilityScope,
                            modifier = Modifier.fillMaxSize(),
                        )
                        DashboardWidget.Vehicle -> VehicleWidgetPreview(
                            batteryPercent = vehicleBatteryPercent,
                            rangeMiles = vehicleRangeMiles,
                            animatedVisibilityScope = animatedVisibilityScope,
                            sportMode = vehicleMorphExpanded,
                            modifier = Modifier.fillMaxSize(),
                        )
                    }
                },
            )
        },
    )
}
