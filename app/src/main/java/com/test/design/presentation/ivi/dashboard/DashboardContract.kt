package com.test.design.presentation.ivi.dashboard

import com.test.design.presentation.ivi.dashboard.model.DashboardWidget

data class DashboardUiState(
    val widgets: List<DashboardWidget> = DashboardWidget.entries,
    val expandedWidget: DashboardWidget? = null,
    val greeting: String = "Good drive",
)

sealed interface DashboardEvent {
    data class WidgetTapped(val widget: DashboardWidget) : DashboardEvent
    data object CollapseWidget : DashboardEvent
}
