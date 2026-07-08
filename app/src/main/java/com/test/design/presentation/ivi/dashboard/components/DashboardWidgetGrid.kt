package com.test.design.presentation.ivi.dashboard.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.test.design.presentation.ivi.dashboard.model.DashboardWidget
import com.test.design.theme.CarDesignTokens

/**
 * 4-column × 2-row grid; each widget spans 1×2 cells (one column, full height).
 */
@Composable
fun DashboardWidgetGrid(
    widgets: List<DashboardWidget>,
    widgetContent: @Composable (DashboardWidget, Modifier) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        modifier = modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(CarDesignTokens.SectionSpacing),
    ) {
        items(widgets, key = { it.sharedElementKey }) { widget ->
            widgetContent(
                widget,
                Modifier
                    .height(360.dp),
            )
        }
    }
}
