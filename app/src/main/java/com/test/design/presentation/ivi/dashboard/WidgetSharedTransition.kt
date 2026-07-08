package com.test.design.presentation.ivi.dashboard

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.test.design.presentation.ivi.dashboard.model.DashboardWidget
import com.test.design.theme.WidgetCardShape

/** Per-widget shared-transition keys for [SharedTransitionLayout] container transforms. */
val DashboardWidget.containerKey: String
    get() = sharedElementKey

val DashboardWidget.iconKey: String
    get() = "${sharedElementKey}_icon"

val DashboardWidget.titleKey: String
    get() = "${sharedElementKey}_title"

val DashboardWidget.subtitleKey: String
    get() = "${sharedElementKey}_subtitle"

val DashboardWidget.previewKey: String
    get() = "${sharedElementKey}_preview"

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
): Modifier = modifier.sharedBounds(
    sharedContentState = rememberSharedContentState(key = widget.containerKey),
    animatedVisibilityScope = animatedVisibilityScope,
    resizeMode = SharedTransitionScope.ResizeMode.RemeasureToBounds,
    renderInOverlayDuringTransition = true,
    clipInOverlayDuringTransition = OverlayClip(WidgetCardShape),
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
fun SharedTransitionScope.widgetSubtitleSharedElement(
    widget: DashboardWidget,
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier,
): Modifier = modifier.sharedElement(
    sharedContentState = rememberSharedContentState(key = widget.subtitleKey),
    animatedVisibilityScope = animatedVisibilityScope,
    zIndexInOverlay = 2f,
)

/** Hero preview (album art, dial, map strip, gauge) shared between grid card and detail screen. */
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.widgetPreviewSharedElement(
    widget: DashboardWidget,
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier,
): Modifier = modifier.sharedElement(
    sharedContentState = rememberSharedContentState(key = widget.previewKey),
    animatedVisibilityScope = animatedVisibilityScope,
    renderInOverlayDuringTransition = true,
    zIndexInOverlay = 1f,
)
