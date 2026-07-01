package com.test.design.presentation.demos.playground

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

data class ComponentPresetSnapshot(
    val componentId: String,
    val props: Map<String, String>,
    val textContent: String? = null,
)

class ComponentPresetStore(context: Context) {

    private val prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun load(componentId: String): ComponentPresetSnapshot? {
        val raw = prefs.getString(keyFor(componentId), null) ?: return null
        return runCatching { decode(componentId, raw) }.getOrNull()
    }

    fun save(snapshot: ComponentPresetSnapshot) {
        prefs.edit().putString(keyFor(snapshot.componentId), encode(snapshot)).apply()
    }

    fun clear(componentId: String) {
        prefs.edit().remove(keyFor(componentId)).apply()
    }

    private fun keyFor(componentId: String) = "$KEY_PREFIX$componentId"

    private fun encode(snapshot: ComponentPresetSnapshot): String {
        val root = JSONObject()
        root.put("componentId", snapshot.componentId)
        snapshot.textContent?.let { root.put("textContent", it) }
        val propsObject = JSONObject()
        snapshot.props.forEach { (key, value) -> propsObject.put(key, value) }
        root.put("props", propsObject)
        return root.toString()
    }

    private fun decode(componentId: String, raw: String): ComponentPresetSnapshot {
        val root = JSONObject(raw)
        val propsObject = root.optJSONObject("props") ?: JSONObject()
        val props = buildMap {
            val keys = propsObject.keys()
            while (keys.hasNext()) {
                val key = keys.next()
                put(key, propsObject.getString(key))
            }
        }
        return ComponentPresetSnapshot(
            componentId = root.optString("componentId", componentId),
            props = props,
            textContent = root.optString("textContent").takeIf { it.isNotEmpty() },
        )
    }

    companion object {
        private const val PREFS_NAME = "component_presets"
        private const val KEY_PREFIX = "preset_"
    }
}
