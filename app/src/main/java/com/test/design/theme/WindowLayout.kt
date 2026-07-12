package com.test.design.theme

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Breakpoints aligned with Material window size classes, tuned for AAOS
 * landscape hubs and phone/tablet portrait reflow.
 */
object WindowBreakpoints {
    val CompactMax = 600.dp
    val MediumMax = 840.dp
    /** Minimum width to keep multi-pane side-by-side layouts. */
    val SideBySideMin = 700.dp
    /** Minimum card width for adaptive dashboard grids. */
    val WidgetMinWidth = 280.dp
}

enum class WindowWidthClass {
    Compact,
    Medium,
    Expanded,
}

@Immutable
data class WindowLayoutInfo(
    val width: Dp,
    val height: Dp,
    val widthClass: WindowWidthClass,
) {
    val isLandscape: Boolean get() = width >= height
    val isPortrait: Boolean get() = height > width

    /** Prefer horizontal multi-pane when wide enough and landscape-ish. */
    val useSideBySide: Boolean
        get() = width >= WindowBreakpoints.SideBySideMin && width >= height * 0.85f

    /** Three-column vehicle cockpit needs more horizontal room. */
    val useTriplePane: Boolean
        get() = width >= WindowBreakpoints.MediumMax && isLandscape

    val orientationLabel: String
        get() = if (isLandscape) "Landscape" else "Portrait"

    val widthClassLabel: String
        get() = widthClass.name
}

fun windowLayoutInfo(width: Dp, height: Dp): WindowLayoutInfo {
    val widthClass = when {
        width < WindowBreakpoints.CompactMax -> WindowWidthClass.Compact
        width < WindowBreakpoints.MediumMax -> WindowWidthClass.Medium
        else -> WindowWidthClass.Expanded
    }
    return WindowLayoutInfo(width = width, height = height, widthClass = widthClass)
}

@Composable
fun BoxWithConstraintsScope.rememberWindowLayoutInfo(): WindowLayoutInfo =
    remember(maxWidth, maxHeight) { windowLayoutInfo(maxWidth, maxHeight) }

/**
 * Measures available space and provides [WindowLayoutInfo] to [content].
 */
@Composable
fun AdaptiveLayout(
    modifier: Modifier = Modifier,
    content: @Composable (WindowLayoutInfo) -> Unit,
) {
    BoxWithConstraints(modifier = modifier) {
        content(rememberWindowLayoutInfo())
    }
}

/**
 * Two-pane split that stacks vertically on narrow / portrait viewports.
 */
@Composable
fun AdaptiveSplit(
    layout: WindowLayoutInfo,
    modifier: Modifier = Modifier,
    spacing: Dp = CarDesignTokens.SectionSpacing,
    primaryWeight: Float = 0.5f,
    secondaryWeight: Float = 0.5f,
    fillHeight: Boolean = false,
    primary: @Composable (Modifier) -> Unit,
    secondary: @Composable (Modifier) -> Unit,
) {
    if (layout.useSideBySide) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .then(if (fillHeight) Modifier.fillMaxHeight() else Modifier),
            horizontalArrangement = Arrangement.spacedBy(spacing),
            verticalAlignment = Alignment.Top,
        ) {
            primary(
                Modifier
                    .weight(primaryWeight)
                    .then(if (fillHeight) Modifier.fillMaxHeight() else Modifier),
            )
            secondary(
                Modifier
                    .weight(secondaryWeight)
                    .then(if (fillHeight) Modifier.fillMaxHeight() else Modifier),
            )
        }
    } else {
        Column(
            modifier = modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(spacing),
        ) {
            primary(Modifier.fillMaxWidth())
            secondary(Modifier.fillMaxWidth())
        }
    }
}
