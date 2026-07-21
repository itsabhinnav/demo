package com.test.design.presentation.ivi.dashboard

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * Process-wide visibility for the in-app floating system bars
 * ([com.test.design.presentation.ivi.dashboard.components.FloatingTopSystemBar] /
 * [com.test.design.presentation.ivi.dashboard.components.FloatingBottomSystemBar]).
 *
 * Hidden by default. Toggle via adb — see [FloatingSystemBarsReceiver].
 */
object FloatingSystemBarsVisibility {
    private val _visible = MutableStateFlow(false)
    val visible: StateFlow<Boolean> = _visible.asStateFlow()

    fun show() {
        _visible.value = true
    }

    fun hide() {
        _visible.value = false
    }

    fun toggle() {
        _visible.value = !_visible.value
    }

    fun setVisible(visible: Boolean) {
        _visible.value = visible
    }
}
