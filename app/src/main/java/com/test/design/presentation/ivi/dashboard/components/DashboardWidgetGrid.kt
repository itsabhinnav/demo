package com.test.design.presentation.ivi.dashboard.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
    Row(
        modifier = modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(CarDesignTokens.SectionSpacing),
    ) {
        widgets.forEach { widget ->
            widgetContent(
                widget,
                Modifier
                    .weight(widget.gridColumnSpan.toFloat())
                    .fillMaxHeight(),
            )
        }
    }
}
