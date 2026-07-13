package com.test.design.presentation.ivi.map

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.max
import com.test.design.theme.CarDesignTokens
import com.test.design.theme.WindowLayoutInfo
import com.test.design.theme.WindowWidthClass

/**
 * Overlay metrics for [MapActivity] / navigation chrome hosted in Scalable UI panels.
 *
 * Scalable UI publishes panel SafeBounds as standard window insets. Keep the map
 * full-bleed and apply [WindowInsets.safeDrawing][androidx.compose.foundation.layout.WindowInsets]
 * only to floating cards/controls (see [NavigationScreen]).
 */
data class MapOverlayMetrics(
    val contentPadding: PaddingValues,
    val cardSpacing: Dp,
    val cardMaxWidth: Dp,
    val compactCards: Boolean,
    val showSecondaryPane: Boolean,
    val showFavorites: Boolean,
    val showRouteStepsInline: Boolean,
)

@Composable
fun rememberMapOverlayMetrics(layout: WindowLayoutInfo): MapOverlayMetrics =
    remember(layout.width, layout.height, layout.widthClass, layout.useSideBySide) {
        mapOverlayMetricsForLayout(layout)
    }

internal fun mapOverlayMetricsForLayout(layout: WindowLayoutInfo): MapOverlayMetrics {
    val base = when (layout.widthClass) {
        WindowWidthClass.Compact -> CarDesignTokens.TouchTargetSpacing / 2
        WindowWidthClass.Medium -> CarDesignTokens.TouchTargetSpacing
        WindowWidthClass.Expanded -> CarDesignTokens.ContentPadding
    }
    val vertical = when {
        layout.height < 480.dp -> 8.dp
        layout.height < 720.dp -> 12.dp
        else -> base
    }
    val compactCards = layout.widthClass == WindowWidthClass.Compact || layout.height < 640.dp
    val showSecondary = layout.useSideBySide && layout.height >= 560.dp
    return MapOverlayMetrics(
        contentPadding = PaddingValues(
            start = max(base, 12.dp),
            top = max(vertical, 8.dp),
            end = max(base, 12.dp),
            bottom = max(vertical, 8.dp),
        ),
        cardSpacing = if (compactCards) 10.dp else CarDesignTokens.TouchTargetSpacing,
        cardMaxWidth = when (layout.widthClass) {
            WindowWidthClass.Compact -> layout.width
            WindowWidthClass.Medium -> 520.dp
            WindowWidthClass.Expanded -> 640.dp
        },
        compactCards = compactCards,
        showSecondaryPane = showSecondary,
        showFavorites = layout.height >= 520.dp,
        showRouteStepsInline = showSecondary || (!layout.useSideBySide && layout.height >= 700.dp),
    )
}
