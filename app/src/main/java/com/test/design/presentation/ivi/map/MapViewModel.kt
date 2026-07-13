package com.test.design.presentation.ivi.map

import android.content.Intent
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MapViewModel : ViewModel() {

    private val _config = MutableStateFlow(MapLaunchConfig.default())
    val config: StateFlow<MapLaunchConfig> = _config.asStateFlow()

    fun applyIntent(intent: Intent?) {
        _config.value = MapIntents.parseLaunchConfig(intent)
    }
}
