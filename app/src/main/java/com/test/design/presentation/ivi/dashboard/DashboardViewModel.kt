package com.test.design.presentation.ivi.dashboard

import com.test.design.core.mvi.MviViewModel
import com.test.design.presentation.ivi.dashboard.model.DashboardWidget

class DashboardViewModel : MviViewModel<DashboardUiState, DashboardEvent>(DashboardUiState()) {

    override fun onEvent(event: DashboardEvent) {
        when (event) {
            is DashboardEvent.WidgetTapped -> setState { copy(expandedWidget = event.widget) }
            DashboardEvent.CollapseWidget -> setState { copy(expandedWidget = null) }
        }
    }

    fun widgetSubtitle(widget: DashboardWidget): String = widget.subtitle
}
