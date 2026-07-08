package com.test.design.presentation.ivi.dashboard

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import com.test.design.presentation.ivi.dashboard.components.DashboardWidgetCard
import com.test.design.presentation.ivi.dashboard.components.DashboardWidgetGrid
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
        SharedTransitionLayout(modifier = modifier.fillMaxSize()) {
            Scaffold(
                topBar = {
                    if (dashboardState.expandedWidget == null) {
                        TopAppBar(
                            title = {
                                Column {
                                    Text(
                                        text = dashboardState.greeting,
                                        style = MaterialTheme.typography.titleLarge,
                                    )
                                    Text(
                                        text = "Material 3 Expressive · Motion",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    )
                                }
                            },
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
                val motionSpec = MaterialTheme.motionScheme.defaultEffectsSpec<Float>()
                AnimatedContent(
                    targetState = dashboardState.expandedWidget,
                    modifier = Modifier
                        .padding(padding)
                        .fillMaxSize(),
                    transitionSpec = {
                        fadeIn(animationSpec = motionSpec) togetherWith fadeOut(animationSpec = motionSpec)
                    },
                    label = "dashboard_container_transform",
                ) { expandedWidget ->
                    when (expandedWidget) {
                        null -> DashboardHubContent(
                            state = dashboardState,
                            onEvent = dashboardViewModel::onEvent,
                            widgetSubtitle = dashboardViewModel::widgetSubtitle,
                            animatedVisibilityScope = this@AnimatedContent,
                        )
                        DashboardWidget.Climate -> ClimateControlScreen(
                            uiState = climateState,
                            activeTemperature = climateViewModel.activeTemperature(),
                            onEvent = climateViewModel::onEvent,
                            onBack = { dashboardViewModel.onEvent(DashboardEvent.CollapseWidget) },
                            animatedVisibilityScope = this@AnimatedContent,
                        )
                        DashboardWidget.Media -> MediaPlayerScreen(
                            uiState = mediaState,
                            onEvent = mediaViewModel::onEvent,
                            onBack = { dashboardViewModel.onEvent(DashboardEvent.CollapseWidget) },
                            animatedVisibilityScope = this@AnimatedContent,
                        )
                        DashboardWidget.Navigation -> NavigationScreen(
                            uiState = navigationState,
                            onEvent = navigationViewModel::onEvent,
                            onBack = { dashboardViewModel.onEvent(DashboardEvent.CollapseWidget) },
                            animatedVisibilityScope = this@AnimatedContent,
                        )
                        DashboardWidget.Vehicle -> VehicleScreen(
                            uiState = vehicleState,
                            onEvent = vehicleViewModel::onEvent,
                            onBack = { dashboardViewModel.onEvent(DashboardEvent.CollapseWidget) },
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
private fun SharedTransitionScope.DashboardHubContent(
    state: DashboardUiState,
    onEvent: (DashboardEvent) -> Unit,
    widgetSubtitle: (DashboardWidget) -> String,
    animatedVisibilityScope: AnimatedVisibilityScope,
) {
    DashboardWidgetGrid(
        widgets = state.widgets,
        modifier = Modifier
            .fillMaxSize()
            .padding(CarDesignTokens.ContentPadding),
        widgetContent = { widget, widgetModifier ->
            DashboardWidgetCard(
                widget = widget,
                subtitle = widgetSubtitle(widget),
                onClick = { onEvent(DashboardEvent.WidgetTapped(widget)) },
                animatedVisibilityScope = animatedVisibilityScope,
                modifier = widgetModifier,
            )
        },
    )
}
