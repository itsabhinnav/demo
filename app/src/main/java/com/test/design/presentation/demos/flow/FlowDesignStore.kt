package com.test.design.presentation.demos.flow

import android.content.Context

data class FlowScreenDefinition(
    val id: String,
    val title: String,
    val componentIds: List<String>,
)

data class FlowDesignSnapshot(
    val title: String,
    val screens: List<FlowScreenDefinition>,
    val schemaVersion: Int = FlowDesignStore.CURRENT_SCHEMA_VERSION,
)

class FlowDesignStore(context: Context) {

    private val prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun load(): FlowDesignSnapshot? {
        val raw = prefs.getString(KEY_FLOW, null) ?: return null
        return runCatching { FlowDesignCodec.decode(raw) }.getOrNull()
    }

    fun save(snapshot: FlowDesignSnapshot) {
        prefs.edit().putString(KEY_FLOW, FlowDesignCodec.encode(snapshot)).apply()
    }

    fun exportJson(snapshot: FlowDesignSnapshot): String = FlowDesignCodec.encode(snapshot)

    companion object {
        const val CURRENT_SCHEMA_VERSION = 1
        private const val PREFS_NAME = "flow_builder"
        private const val KEY_FLOW = "saved_flow"

        fun defaultFlow(): FlowDesignSnapshot = FlowDesignSnapshot(
            title = "Climate settings flow",
            screens = listOf(
                FlowScreenDefinition(
                    id = "overview",
                    title = "Overview",
                    componentIds = listOf("metric-card", "filter-chip", "list-tile"),
                ),
                FlowScreenDefinition(
                    id = "adjust",
                    title = "Adjust",
                    componentIds = listOf("slider", "segmented-button", "button-primary"),
                ),
                FlowScreenDefinition(
                    id = "confirm",
                    title = "Confirm",
                    componentIds = listOf("button-primary", "button-secondary", "snackbar"),
                ),
            ),
        )
    }
}
