package com.test.design.presentation.ivi.dashboard

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.test.design.presentation.ivi.IviExpressiveTheme
import com.test.design.presentation.ivi.climate.ClimateControlScreen
import com.test.design.presentation.ivi.climate.ClimateViewModel
import com.test.design.presentation.ivi.dashboard.components.DashboardWidgetCard
import com.test.design.presentation.ivi.dashboard.model.DashboardWidget
import com.test.design.presentation.ivi.media.MediaPlayerScreen
import com.test.design.presentation.ivi.media.MediaViewModel
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
) {
    val dashboardState by dashboardViewModel.state.collectAsStateWithLifecycle()
    val climateState by climateViewModel.state.collectAsStateWithLifecycle()
    val mediaState by mediaViewModel.state.collectAsStateWithLifecycle()

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
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(CarDesignTokens.ContentPadding),
        horizontalArrangement = Arrangement.spacedBy(CarDesignTokens.SectionSpacing),
    ) {
        Column(
            modifier = Modifier
                .weight(0.55f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(CarDesignTokens.SectionSpacing),
        ) {
            Text(
                text = "Your cockpit",
                style = MaterialTheme.typography.displayMedium,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Text(
                text = "Tap a widget to expand with a container transform",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            DashboardWidgetCard(
                widget = DashboardWidget.Media,
                subtitle = widgetSubtitle(DashboardWidget.Media),
                onClick = { onEvent(DashboardEvent.WidgetTapped(DashboardWidget.Media)) },
                animatedVisibilityScope = animatedVisibilityScope,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1.2f),
            )
        }
        Column(
            modifier = Modifier
                .weight(0.45f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(CarDesignTokens.SectionSpacing),
        ) {
            DashboardWidgetCard(
                widget = DashboardWidget.Climate,
                subtitle = widgetSubtitle(DashboardWidget.Climate),
                onClick = { onEvent(DashboardEvent.WidgetTapped(DashboardWidget.Climate)) },
                animatedVisibilityScope = animatedVisibilityScope,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .padding(top = 8.dp),
                contentAlignment = Alignment.CenterStart,
            ) {
                Text(
                    text = "Landscape · 1920×720 optimized",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.outline,
                )
            }
        }
    }
}
