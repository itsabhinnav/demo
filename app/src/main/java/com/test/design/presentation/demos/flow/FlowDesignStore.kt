package com.test.design.presentation.demos.flow

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

data class FlowScreenDefinition(
    val id: String,
    val title: String,
    val componentIds: List<String>,
)

data class FlowDesignSnapshot(
    val title: String,
    val screens: List<FlowScreenDefinition>,
)

class FlowDesignStore(context: Context) {

    private val prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun load(): FlowDesignSnapshot? {
        val raw = prefs.getString(KEY_FLOW, null) ?: return null
        return runCatching { decode(raw) }.getOrNull()
    }

    fun save(snapshot: FlowDesignSnapshot) {
        prefs.edit().putString(KEY_FLOW, encode(snapshot)).apply()
    }

    fun exportJson(snapshot: FlowDesignSnapshot): String = encode(snapshot)

    private fun encode(snapshot: FlowDesignSnapshot): String {
        val root = JSONObject()
        root.put("title", snapshot.title)
        val screens = JSONArray()
        snapshot.screens.forEach { screen ->
            screens.put(
                JSONObject().apply {
                    put("id", screen.id)
                    put("title", screen.title)
                    put("components", JSONArray(screen.componentIds))
                },
            )
        }
        root.put("screens", screens)
        return root.toString(2)
    }

    private fun decode(raw: String): FlowDesignSnapshot {
        val root = JSONObject(raw)
        val title = root.optString("title", "Untitled flow")
        val screensArray = root.optJSONArray("screens") ?: JSONArray()
        val screens = buildList {
            for (index in 0 until screensArray.length()) {
                val item = screensArray.getJSONObject(index)
                val componentsArray = item.optJSONArray("components") ?: JSONArray()
                val componentIds = buildList {
                    for (componentIndex in 0 until componentsArray.length()) {
                        add(componentsArray.getString(componentIndex))
                    }
                }
                add(
                    FlowScreenDefinition(
                        id = item.getString("id"),
                        title = item.getString("title"),
                        componentIds = componentIds,
                    ),
                )
            }
        }
        return FlowDesignSnapshot(title = title, screens = screens)
    }

    companion object {
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
                    componentIds = listOf("dialog-trigger", "button-secondary", "snackbar"),
                ),
            ),
        )
    }
}
