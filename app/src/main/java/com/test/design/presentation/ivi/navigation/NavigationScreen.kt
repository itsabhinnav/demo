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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.test.design.presentation.common.ScreenBackground
import com.test.design.presentation.ivi.common.DetailSurfaceCard
import com.test.design.presentation.ivi.common.SimulatedBadge
import com.test.design.presentation.ivi.common.WidgetScreenHeader
import com.test.design.presentation.ivi.dashboard.model.DashboardWidget
import com.test.design.presentation.ivi.dashboard.widgetContentSharedElement
import com.test.design.presentation.ivi.dashboard.widgetControlsSharedElement
import com.test.design.presentation.ivi.dashboard.widgetContainerTransform
import com.test.design.presentation.ivi.map.rememberMapOverlayMetrics
import com.test.design.presentation.ivi.navigation.components.OsmMapBackground
import com.test.design.presentation.ivi.navigation.components.mapChromeLayer
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
) {
    val motionSpec = MaterialTheme.motionScheme.defaultEffectsSpec<Float>()

    Box(
        modifier = widgetContainerTransform(
            widget = DashboardWidget.Navigation,
            animatedVisibilityScope = animatedVisibilityScope,
            modifier = modifier.fillMaxSize(),
        ),
    ) {
        // Full-bleed OSM map layer — ignores Scalable UI / system safe areas.
        ScreenBackground(modifier = Modifier.fillMaxSize())
        OsmMapBackground(
            modifier = Modifier.fillMaxSize(),
            showRoute = true,
            interactive = true,
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.22f)
                .align(Alignment.TopCenter)
                .mapChromeLayer()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            MaterialTheme.colorScheme.background.copy(
                                alpha = CarBackgroundTokens.NavigationScrimAlpha,
                            ),
                            Color.Transparent,
                        ),
                    ),
                ),
        )

        AdaptiveLayout(
            modifier = Modifier
                .fillMaxSize()
                .mapChromeLayer()
                // Scalable UI SafeBounds + system bars → WindowInsets; pad overlays only.
                .windowInsetsPadding(WindowInsets.safeDrawing),
        ) { layout ->
            val overlay = rememberMapOverlayMetrics(layout)
            val scroll = rememberScrollState()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(overlay.contentPadding)
                    .verticalScroll(scroll),
                verticalArrangement = Arrangement.spacedBy(overlay.cardSpacing),
            ) {
                Surface(
                    shape = ExpressiveShapes.large,
                    color = navigationGlassPanelColor(),
                    shadowElevation = if (overlay.compactCards) 2.dp else CarDesignTokens.TouchTargetSpacing,
                    modifier = Modifier
                        .fillMaxWidth()
                        .widthIn(max = overlay.cardMaxWidth)
                        .align(Alignment.Start),
                ) {
                    WidgetScreenHeader(
                        widget = DashboardWidget.Navigation,
                        onBack = onBack,
                        animatedVisibilityScope = animatedVisibilityScope,
                        modifier = Modifier.padding(
                            horizontal = if (overlay.compactCards) 12.dp else CarDesignTokens.TouchTargetSpacing,
                        ),
                        trailingContent = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                            ) {
                                SimulatedBadge()
                                FilterChip(
                                    selected = uiState.showRouteDetails,
                                    onClick = { onEvent(NavigationEvent.ToggleRouteDetails) },
                                    label = {
                                        Text(
                                            "Steps",
                                            style = if (overlay.compactCards) {
                                                MaterialTheme.typography.labelMedium
                                            } else {
                                                MaterialTheme.typography.labelLarge
                                            },
                                        )
                                    },
                                )
                            }
                        },
                    )
                }

                if (overlay.showSecondaryPane) {
                    AdaptiveSplit(
                        layout = layout,
                        primaryWeight = 0.55f,
                        secondaryWeight = 0.35f,
                        modifier = Modifier.fillMaxWidth(),
                        spacing = overlay.cardSpacing,
                        primary = { paneModifier ->
                            NavigationPrimaryCards(
                                uiState = uiState,
                                onEvent = onEvent,
                                animatedVisibilityScope = animatedVisibilityScope,
                                overlayCompact = overlay.compactCards,
                                showFavorites = overlay.showFavorites,
                                cardSpacing = overlay.cardSpacing,
                                cardMaxWidth = overlay.cardMaxWidth,
                                modifier = paneModifier,
                            )
                        },
                        secondary = { paneModifier ->
                            NavigationSecondaryCards(
                                uiState = uiState,
                                showRouteSteps = overlay.showRouteStepsInline && uiState.showRouteDetails,
                                compact = overlay.compactCards,
                                cardSpacing = overlay.cardSpacing,
                                cardMaxWidth = overlay.cardMaxWidth,
                                motionSpec = motionSpec,
                                modifier = paneModifier,
                            )
                        },
                    )
                } else {
                    NavigationPrimaryCards(
                        uiState = uiState,
                        onEvent = onEvent,
                        animatedVisibilityScope = animatedVisibilityScope,
                        overlayCompact = overlay.compactCards,
                        showFavorites = overlay.showFavorites,
                        cardSpacing = overlay.cardSpacing,
                        cardMaxWidth = overlay.cardMaxWidth,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    if (overlay.showRouteStepsInline || uiState.showRouteDetails) {
                        NavigationSecondaryCards(
                            uiState = uiState,
                            showRouteSteps = uiState.showRouteDetails,
                            compact = overlay.compactCards,
                            cardSpacing = overlay.cardSpacing,
                            cardMaxWidth = overlay.cardMaxWidth,
                            motionSpec = motionSpec,
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
private fun SharedTransitionScope.NavigationPrimaryCards(
    uiState: NavigationUiState,
    onEvent: (NavigationEvent) -> Unit,
    animatedVisibilityScope: AnimatedVisibilityScope,
    overlayCompact: Boolean,
    showFavorites: Boolean,
    cardSpacing: Dp,
    cardMaxWidth: Dp,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.widthIn(max = cardMaxWidth),
        verticalArrangement = Arrangement.spacedBy(cardSpacing),
    ) {
        Text(
            text = "${uiState.destination} · ${uiState.etaMinutes} min",
            style = if (overlayCompact) {
                MaterialTheme.typography.titleLarge
            } else {
                MaterialTheme.typography.headlineSmall
            },
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
            compact = overlayCompact,
            modifier = widgetControlsSharedElement(
                widget = DashboardWidget.Navigation,
                animatedVisibilityScope = animatedVisibilityScope,
            ),
        )
        if (showFavorites) {
            Surface(
                shape = ExpressiveShapes.large,
                color = navigationGlassPanelColor(),
            ) {
                FavoriteDestinationsRow(
                    favorites = uiState.favorites,
                    selectedId = uiState.selectedFavoriteId,
                    onSelected = { onEvent(NavigationEvent.SelectFavorite(it)) },
                    compact = overlayCompact,
                    modifier = Modifier.padding(
                        if (overlayCompact) 12.dp else CarDesignTokens.TouchTargetSpacing,
                    ),
                )
            }
        }
    }
}

@Composable
private fun NavigationSecondaryCards(
    uiState: NavigationUiState,
    showRouteSteps: Boolean,
    compact: Boolean,
    cardSpacing: Dp,
    cardMaxWidth: Dp,
    motionSpec: FiniteAnimationSpec<Float>,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.widthIn(max = cardMaxWidth),
        verticalArrangement = Arrangement.spacedBy(cardSpacing),
    ) {
        DetailSurfaceCard(modifier = Modifier.fillMaxWidth()) {
            Text(
                "Arrival ${uiState.arrivalTime}",
                style = if (compact) MaterialTheme.typography.titleSmall else MaterialTheme.typography.titleMedium,
            )
            Text(
                text = "${uiState.distanceRemaining} to ${uiState.destination}",
                style = if (compact) MaterialTheme.typography.bodySmall else MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        AnimatedContent(
            targetState = showRouteSteps,
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
                        compact = compact,
                        modifier = Modifier.padding(
                            if (compact) 12.dp else CarDesignTokens.TouchTargetSpacing,
                        ),
                    )
                }
            }
        }
    }
}
