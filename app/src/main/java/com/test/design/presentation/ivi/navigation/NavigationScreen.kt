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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.test.design.presentation.ivi.common.DetailSurfaceCard
import com.test.design.presentation.ivi.common.WidgetScreenHeader
import com.test.design.presentation.ivi.dashboard.model.DashboardWidget
import com.test.design.presentation.ivi.navigation.components.FavoriteDestinationsRow
import com.test.design.presentation.ivi.navigation.components.RouteMapPlaceholder
import com.test.design.presentation.ivi.navigation.components.RouteStepsList
import com.test.design.presentation.ivi.navigation.components.TurnInstructionCard
import com.test.design.theme.CarDesignTokens
import com.test.design.theme.WidgetCardShape

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.NavigationScreen(
    uiState: NavigationUiState,
    onEvent: (NavigationEvent) -> Unit,
    onBack: () -> Unit,
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier,
) {
    val motionSpec = MaterialTheme.motionScheme.defaultEffectsSpec<Float>()

    Box(
        modifier = modifier
            .fillMaxSize()
            .sharedBounds(
                rememberSharedContentState(key = DashboardWidget.Navigation.sharedElementKey),
                animatedVisibilityScope = animatedVisibilityScope,
                resizeMode = SharedTransitionScope.ResizeMode.RemeasureToBounds,
                clipInOverlayDuringTransition = OverlayClip(WidgetCardShape),
            )
            .background(MaterialTheme.colorScheme.background)
            .padding(CarDesignTokens.ContentPadding),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(CarDesignTokens.SectionSpacing),
        ) {
            WidgetScreenHeader(
                title = "Navigation",
                onBack = onBack,
                trailingContent = {
                    FilterChip(
                        selected = uiState.showRouteDetails,
                        onClick = { onEvent(NavigationEvent.ToggleRouteDetails) },
                        label = { Text("Steps", style = MaterialTheme.typography.labelLarge) },
                    )
                },
            )

            TurnInstructionCard(
                instruction = uiState.currentInstruction,
                maneuverIcon = uiState.maneuverIcon,
                distanceRemaining = uiState.distanceRemaining,
                etaMinutes = uiState.etaMinutes,
                onClick = { onEvent(NavigationEvent.NextManeuver) },
            )

            RouteMapPlaceholder(destination = uiState.destination)

            FavoriteDestinationsRow(
                favorites = uiState.favorites,
                selectedId = uiState.selectedFavoriteId,
                onSelected = { onEvent(NavigationEvent.SelectFavorite(it)) },
            )

            DetailSurfaceCard(modifier = Modifier.fillMaxWidth()) {
                Text("Arrival ${uiState.arrivalTime}", style = MaterialTheme.typography.titleMedium)
                Text(
                    text = "${uiState.distanceRemaining} remaining to ${uiState.destination}",
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
                    RouteStepsList(steps = uiState.routeSteps)
                }
            }
        }
    }
}
