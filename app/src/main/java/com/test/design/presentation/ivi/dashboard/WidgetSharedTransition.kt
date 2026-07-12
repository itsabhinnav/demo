package com.test.design.presentation.ivi.dashboard

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import com.test.design.presentation.ivi.dashboard.model.DashboardWidget
import com.test.design.theme.WidgetCardShape

/** Per-widget shared-transition keys for [SharedTransitionLayout] container transforms. */
val DashboardWidget.containerKey: String
    get() = sharedElementKey

val DashboardWidget.iconKey: String
    get() = "${sharedElementKey}_icon"

val DashboardWidget.titleKey: String
    get() = "${sharedElementKey}_title"

val DashboardWidget.contentKey: String
    get() = "${sharedElementKey}_content"

val DashboardWidget.controlsKey: String
    get() = "${sharedElementKey}_controls"

/**
 * Morphs a dashboard widget card into its full-screen detail surface (container transform).
 * Apply to the outer colored bounds on both the grid card and the matching detail screen.
 */
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.widgetContainerTransform(
    widget: DashboardWidget,
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier,
    clipShape: Shape = WidgetCardShape,
): Modifier = modifier.sharedBounds(
    sharedContentState = rememberSharedContentState(key = widget.containerKey),
    animatedVisibilityScope = animatedVisibilityScope,
    resizeMode = SharedTransitionScope.ResizeMode.RemeasureToBounds,
    renderInOverlayDuringTransition = true,
    clipInOverlayDuringTransition = OverlayClip(clipShape),
    boundsTransform = { _, _ ->
        spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMediumLow,
        )
    },
    zIndexInOverlay = 1f,
)

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.widgetIconSharedElement(
    widget: DashboardWidget,
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier,
): Modifier = modifier.sharedElement(
    sharedContentState = rememberSharedContentState(key = widget.iconKey),
    animatedVisibilityScope = animatedVisibilityScope,
    zIndexInOverlay = 2f,
)

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.widgetTitleSharedElement(
    widget: DashboardWidget,
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier,
): Modifier = modifier.sharedElement(
    sharedContentState = rememberSharedContentState(key = widget.titleKey),
    animatedVisibilityScope = animatedVisibilityScope,
    zIndexInOverlay = 2f,
)

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.widgetContentSharedElement(
    widget: DashboardWidget,
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier,
): Modifier = modifier.sharedElement(
    sharedContentState = rememberSharedContentState(key = widget.contentKey),
    animatedVisibilityScope = animatedVisibilityScope,
    zIndexInOverlay = 2f,
)

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.widgetControlsSharedElement(
    widget: DashboardWidget,
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier,
): Modifier = modifier.sharedElement(
    sharedContentState = rememberSharedContentState(key = widget.controlsKey),
    animatedVisibilityScope = animatedVisibilityScope,
    zIndexInOverlay = 2f,
)
