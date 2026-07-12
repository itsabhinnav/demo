package com.test.design.presentation.ivi.dashboard.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.test.design.presentation.ivi.dashboard.model.DashboardWidget
import com.test.design.theme.CarDesignTokens
import com.test.design.theme.WindowBreakpoints
import com.test.design.theme.rememberWindowLayoutInfo

/**
 * Adaptive widget rail:
 * - Landscape / wide: horizontal list
 * - Portrait / compact: vertical adaptive grid
 */
@Composable
fun DashboardWidgetGrid(
    widgets: List<DashboardWidget>,
    widgetContent: @Composable (DashboardWidget, Modifier) -> Unit,
    modifier: Modifier = Modifier,
) {
    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val layout = rememberWindowLayoutInfo()
        if (layout.useSideBySide) {
            val visibleSlots = when {
                maxWidth >= 1600.dp -> 4
                maxWidth >= 1100.dp -> 3
                else -> 2
            }
            val fraction = 1f / visibleSlots
            LazyRow(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(CarDesignTokens.SectionSpacing),
            ) {
                items(widgets, key = { it.sharedElementKey }) { widget ->
                    widgetContent(
                        widget,
                        Modifier
                            .fillParentMaxHeight()
                            .fillParentMaxWidth(fraction * 0.92f),
                    )
                }
            }
        } else {
            val cardHeight = (maxHeight * 0.42f).coerceIn(180.dp, 320.dp)
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = WindowBreakpoints.WidgetMinWidth),
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(CarDesignTokens.SectionSpacing),
                verticalArrangement = Arrangement.spacedBy(CarDesignTokens.SectionSpacing),
            ) {
                items(widgets, key = { it.sharedElementKey }) { widget ->
                    widgetContent(
                        widget,
                        Modifier
                            .fillMaxWidth()
                            .height(cardHeight),
                    )
                }
            }
        }
    }
}
