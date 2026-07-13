package com.test.design.presentation.ivi.navigation

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.test.design.presentation.common.ScreenBackground
import com.test.design.presentation.ivi.common.DetailSurfaceCard
import com.test.design.presentation.ivi.common.WidgetScreenHeader
import com.test.design.presentation.ivi.dashboard.model.DashboardWidget
import com.test.design.presentation.ivi.dashboard.widgetContentSharedElement
import com.test.design.presentation.ivi.dashboard.widgetControlsSharedElement
import com.test.design.presentation.ivi.dashboard.widgetContainerTransform
import com.test.design.presentation.ivi.navigation.components.OsmMapBackground
import com.test.design.presentation.ivi.navigation.components.FavoriteDestinationsRow
import com.test.design.presentation.ivi.navigation.components.RouteStepsList
import com.test.design.presentation.ivi.navigation.components.TurnInstructionCard
import com.test.design.theme.AdaptiveLayout
import com.test.design.theme.AdaptiveSplit
import com.test.design.theme.CarDesignTokens
import com.test.design.theme.CarBackgroundTokens
import com.test.design.theme.ExpressiveShapes
import com.test.design.theme.navigationGlassPanelColor

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.NavigationScreen(
    uiState: NavigationUiState,
    onEvent: (NavigationEvent) -> Unit,
    onBack: () -> Unit,
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier,
    showRoute: Boolean = true,
    trailingHeaderContent: @Composable () -> Unit = {},
) {
    val motionSpec = MaterialTheme.motionScheme.defaultEffectsSpec<Float>()

    Box(
        modifier = widgetContainerTransform(
            widget = DashboardWidget.Navigation,
            animatedVisibilityScope = animatedVisibilityScope,
            modifier = modifier.fillMaxSize(),
        ),
    ) {
        ScreenBackground(modifier = Modifier.fillMaxSize())
        OsmMapBackground(
            modifier = Modifier.fillMaxSize(),
            showRoute = showRoute,
            interactive = true,
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.22f)
                .align(Alignment.TopCenter)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            CarBackgroundTokens.CanvasGray.copy(alpha = CarBackgroundTokens.NavigationScrimAlpha),
                            Color.Transparent,
                        ),
                    ),
                ),
        )

        AdaptiveLayout(
            modifier = Modifier
                .fillMaxSize()
                .padding(CarDesignTokens.ContentPadding),
        ) { layout ->
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                Surface(
                    shape = ExpressiveShapes.large,
                    color = navigationGlassPanelColor(),
                    shadowElevation = CarDesignTokens.TouchTargetSpacing,
                ) {
                    WidgetScreenHeader(
                        widget = DashboardWidget.Navigation,
                        onBack = onBack,
                        animatedVisibilityScope = animatedVisibilityScope,
                        modifier = Modifier.padding(horizontal = CarDesignTokens.TouchTargetSpacing),
                        trailingContent = {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(CarDesignTokens.TouchTargetSpacing),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                trailingHeaderContent()
                                FilterChip(
                                    selected = uiState.showRouteDetails,
                                    onClick = { onEvent(NavigationEvent.ToggleRouteDetails) },
                                    label = { Text("Steps", style = MaterialTheme.typography.labelLarge) },
                                )
                            }
                        },
                    )
                }

                AdaptiveSplit(
                    layout = layout,
                    primaryWeight = 0.55f,
                    secondaryWeight = 0.35f,
                    modifier = Modifier.fillMaxWidth(),
                    primary = { paneModifier ->
                        Column(
                            modifier = paneModifier,
                            verticalArrangement = Arrangement.spacedBy(CarDesignTokens.TouchTargetSpacing),
                        ) {
                            Text(
                                text = "${uiState.destination} · ${uiState.etaMinutes} min",
                                style = MaterialTheme.typography.headlineSmall,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = widgetContentSharedElement(
                                    widget = DashboardWidget.Navigation,
                                    animatedVisibilityScope = animatedVisibilityScope,
                                    modifier = Modifier.fillMaxWidth(),
                                ),
                            )
                            TurnInstructionCard(
                                instruction = uiState.currentInstruction,
                                maneuverIcon = uiState.maneuverIcon,
                                distanceRemaining = uiState.distanceRemaining,
                                etaMinutes = uiState.etaMinutes,
                                onClick = { onEvent(NavigationEvent.NextManeuver) },
                                modifier = widgetControlsSharedElement(
                                    widget = DashboardWidget.Navigation,
                                    animatedVisibilityScope = animatedVisibilityScope,
                                ),
                            )
                            Surface(
                                shape = ExpressiveShapes.large,
                                color = navigationGlassPanelColor(),
                            ) {
                                FavoriteDestinationsRow(
                                    favorites = uiState.favorites,
                                    selectedId = uiState.selectedFavoriteId,
                                    onSelected = { onEvent(NavigationEvent.SelectFavorite(it)) },
                                    modifier = Modifier.padding(CarDesignTokens.TouchTargetSpacing),
                                )
                            }
                        }
                    },
                    secondary = { paneModifier ->
                        Column(
                            modifier = paneModifier,
                            verticalArrangement = Arrangement.spacedBy(CarDesignTokens.TouchTargetSpacing),
                        ) {
                            DetailSurfaceCard(modifier = Modifier.fillMaxWidth()) {
                                Text("Arrival ${uiState.arrivalTime}", style = MaterialTheme.typography.titleMedium)
                                Text(
                                    text = "${uiState.distanceRemaining} to ${uiState.destination}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                            AnimatedContent(
                                targetState = uiState.showRouteDetails,
                                transitionSpec = {
                                    fadeIn(animationSpec = motionSpec) togetherWith fadeOut(animationSpec = motionSpec)
                                },
                                label = "route_details",
                            ) { showDetails ->
                                if (showDetails) {
                                    Surface(
                                        shape = ExpressiveShapes.large,
                                        color = navigationGlassPanelColor(),
                                    ) {
                                        RouteStepsList(
                                            steps = uiState.routeSteps,
                                            modifier = Modifier.padding(CarDesignTokens.TouchTargetSpacing),
                                        )
                                    }
                                }
                            }
                        }
                    },
                )
            }
        }
    }
}
