package com.test.design.presentation.ivi.dashboard.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.test.design.presentation.ivi.dashboard.model.DashboardWidget
import com.test.design.theme.CarDesignTokens

/**
 * Horizontally scrollable widget rail; each card fills the available height and
 * roughly one quarter of the viewport width so four widgets are visible at once.
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
                    .fillParentMaxHeight()
                    .fillParentMaxWidth(0.23f),
            )
        }
    }
}
