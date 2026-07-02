package com.test.design.presentation.demos.flow

import org.json.JSONArray
import org.json.JSONObject

internal object FlowDesignCodec {

    fun encode(snapshot: FlowDesignSnapshot): String {
        val root = JSONObject()
        root.put("schemaVersion", snapshot.schemaVersion)
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

    fun decode(raw: String): FlowDesignSnapshot {
        val root = JSONObject(raw)
        val schemaVersion = root.optInt("schemaVersion", 1)
        require(schemaVersion <= FlowDesignStore.CURRENT_SCHEMA_VERSION) {
            "Unsupported flow schema version: $schemaVersion"
        }
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
        require(screens.isNotEmpty()) { "Flow must contain at least one screen" }
        return FlowDesignSnapshot(
            title = title,
            screens = screens,
            schemaVersion = schemaVersion,
        )
    }
}
