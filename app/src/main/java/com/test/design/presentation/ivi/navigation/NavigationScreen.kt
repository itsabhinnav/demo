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
import com.test.design.presentation.ivi.common.DetailSurfaceCard
import com.test.design.presentation.ivi.common.WidgetScreenHeader
import com.test.design.presentation.ivi.dashboard.model.DashboardWidget
import com.test.design.presentation.ivi.dashboard.widgetContainerTransform
import com.test.design.presentation.ivi.dashboard.widgetPreviewSharedElement
import com.test.design.presentation.ivi.navigation.components.DummyMapBackground
import com.test.design.presentation.ivi.navigation.components.FavoriteDestinationsRow
import com.test.design.presentation.ivi.navigation.components.RouteStepsList
import com.test.design.presentation.ivi.navigation.components.TurnInstructionCard
import com.test.design.theme.CarDesignTokens
import com.test.design.theme.ExpressiveShapes

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.NavigationScreen(
    uiState: NavigationUiState,
    onEvent: (NavigationEvent) -> Unit,
    onBack: () -> Unit,
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier,
    headerSubtitle: String? = null,
) {
    val motionSpec = MaterialTheme.motionScheme.defaultEffectsSpec<Float>()

    Box(
        modifier = widgetContainerTransform(
            widget = DashboardWidget.Navigation,
            animatedVisibilityScope = animatedVisibilityScope,
            modifier = modifier.fillMaxSize(),
        ),
    ) {
        DummyMapBackground(modifier = Modifier.fillMaxSize())

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.22f)
                .align(Alignment.TopCenter)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Black.copy(alpha = 0.45f), Color.Transparent),
                    ),
                ),
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(CarDesignTokens.ContentPadding),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Surface(
                shape = ExpressiveShapes.large,
                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.94f),
                shadowElevation = CarDesignTokens.TouchTargetSpacing,
            ) {
                WidgetScreenHeader(
                    widget = DashboardWidget.Navigation,
                    onBack = onBack,
                    animatedVisibilityScope = animatedVisibilityScope,
                    subtitle = headerSubtitle,
                    modifier = Modifier.padding(horizontal = CarDesignTokens.TouchTargetSpacing),
                    trailingContent = {
                        FilterChip(
                            selected = uiState.showRouteDetails,
                            onClick = { onEvent(NavigationEvent.ToggleRouteDetails) },
                            label = { Text("Steps", style = MaterialTheme.typography.labelLarge) },
                        )
                    },
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(CarDesignTokens.SectionSpacing),
                verticalAlignment = Alignment.Bottom,
            ) {
                Column(
                    modifier = Modifier.weight(0.55f),
                    verticalArrangement = Arrangement.spacedBy(CarDesignTokens.TouchTargetSpacing),
                ) {
                    TurnInstructionCard(
                        instruction = uiState.currentInstruction,
                        maneuverIcon = uiState.maneuverIcon,
                        distanceRemaining = uiState.distanceRemaining,
                        etaMinutes = uiState.etaMinutes,
                        onClick = { onEvent(NavigationEvent.NextManeuver) },
                        modifier = this@NavigationScreen.widgetPreviewSharedElement(
                            widget = DashboardWidget.Navigation,
                            animatedVisibilityScope = animatedVisibilityScope,
                            modifier = Modifier,
                        ),
                    )
                    Surface(
                        shape = ExpressiveShapes.large,
                        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.94f),
                    ) {
                        FavoriteDestinationsRow(
                            favorites = uiState.favorites,
                            selectedId = uiState.selectedFavoriteId,
                            onSelected = { onEvent(NavigationEvent.SelectFavorite(it)) },
                            modifier = Modifier.padding(CarDesignTokens.TouchTargetSpacing),
                        )
                    }
                }

                Column(
                    modifier = Modifier.weight(0.35f),
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
                                color = MaterialTheme.colorScheme.surface.copy(alpha = 0.94f),
                            ) {
                                RouteStepsList(
                                    steps = uiState.routeSteps,
                                    modifier = Modifier.padding(CarDesignTokens.TouchTargetSpacing),
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
