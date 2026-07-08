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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Surface
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.test.design.presentation.ivi.IviExpressiveTheme
import com.test.design.presentation.ivi.climate.ClimateControlScreen
import com.test.design.presentation.ivi.climate.ClimateViewModel
import com.test.design.presentation.ivi.common.DetailSurfaceCard
import com.test.design.presentation.ivi.common.WidgetScreenHeader
import com.test.design.presentation.ivi.dashboard.components.DashboardWidgetCard
import com.test.design.presentation.ivi.dashboard.components.DashboardWidgetGrid
import com.test.design.presentation.ivi.dashboard.model.DashboardWidget
import com.test.design.presentation.ivi.dashboard.widgetContentSharedElement
import com.test.design.presentation.ivi.dashboard.widgetControlsSharedElement
import com.test.design.presentation.ivi.media.MediaPlayerScreen
import com.test.design.presentation.ivi.media.MediaViewModel
import com.test.design.presentation.ivi.navigation.NavigationScreen
import com.test.design.presentation.ivi.navigation.NavigationViewModel
import com.test.design.presentation.ivi.vehicle.VehicleScreen
import com.test.design.presentation.ivi.vehicle.VehicleViewModel
import com.test.design.theme.CarDesignTokens
import com.test.design.theme.ExpressiveShapes
import com.test.design.theme.carListItemHeight
import com.test.design.theme.carTopAppBarColors
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
                containerColor = Color.Transparent,
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
                            colors = carTopAppBarColors(),
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
                            mediaState = mediaState,
                            climateState = climateState,
                            climateTemperature = climateViewModel.activeTemperature(),
                            navigationState = navigationState,
                            vehicleState = vehicleState,
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
                        else -> DummyWidgetDetailScreen(
                            widget = expandedWidget,
                            onBack = collapseWidget,
                            animatedVisibilityScope = this@AnimatedContent,
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun SharedTransitionScope.DummyWidgetDetailScreen(
    widget: DashboardWidget,
    onBack: () -> Unit,
    animatedVisibilityScope: AnimatedVisibilityScope,
) {
    DetailSurfaceCard(
        modifier = Modifier
            .fillMaxSize()
            .padding(CarDesignTokens.ContentPadding),
    ) {
        WidgetScreenHeader(
            widget = widget,
            onBack = onBack,
            animatedVisibilityScope = animatedVisibilityScope,
            modifier = Modifier.fillMaxWidth(),
        )
        Text(
            text = widget.subtitle,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = widgetContentSharedElement(
                widget = widget,
                animatedVisibilityScope = animatedVisibilityScope,
                modifier = Modifier.fillMaxWidth(),
            ),
        )
        Surface(
            shape = ExpressiveShapes.small,
            color = MaterialTheme.colorScheme.surfaceContainerHigh,
            modifier = widgetControlsSharedElement(
                widget = widget,
                animatedVisibilityScope = animatedVisibilityScope,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(CarDesignTokens.MinTouchTarget),
            ),
        ) {
            Box(contentAlignment = androidx.compose.ui.Alignment.Center) {
                Text(
                    text = "Open ${widget.title}",
                    style = MaterialTheme.typography.labelLarge,
                )
            }
        }
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
        ) {
            items(8) { index ->
                ListItem(
                    modifier = Modifier.carListItemHeight(),
                    headlineContent = { Text("${widget.title} demo item ${index + 1}") },
                    supportingContent = { Text("Dummy content for motion and scrolling preview") },
                )
                HorizontalDivider()
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
    mediaState: com.test.design.presentation.ivi.media.MediaUiState,
    climateState: com.test.design.presentation.ivi.climate.ClimateUiState,
    climateTemperature: Int,
    navigationState: com.test.design.presentation.ivi.navigation.NavigationUiState,
    vehicleState: com.test.design.presentation.ivi.vehicle.VehicleUiState,
) {
    DashboardWidgetGrid(
        widgets = state.widgets,
        modifier = Modifier
            .fillMaxSize()
            .padding(CarDesignTokens.ContentPadding),
        widgetContent = { widget, widgetModifier ->
            DashboardWidgetCard(
                widget = widget,
                onClick = { onEvent(DashboardEvent.WidgetTapped(widget)) },
                animatedVisibilityScope = animatedVisibilityScope,
                modifier = widgetModifier,
                morphExpanded = when (widget) {
                    DashboardWidget.Climate -> climateState.isAcEnabled
                    DashboardWidget.Media -> mediaState.isPlaying
                    DashboardWidget.Vehicle -> vehicleState.driveMode == com.test.design.presentation.ivi.vehicle.DriveMode.Sport ||
                        vehicleState.isCharging
                    else -> false
                },
                mediaState = mediaState,
                climateState = climateState,
                climateTemperature = climateTemperature,
                navigationState = navigationState,
                vehicleState = vehicleState,
            )
        },
    )
}
