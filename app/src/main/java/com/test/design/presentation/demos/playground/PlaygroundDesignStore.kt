package com.test.design.presentation.demos.playground

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject

data class PlaygroundDesignSnapshot(
    val components: List<PlacedComponent>,
    val nextInstanceId: Int,
)

class PlaygroundDesignStore(context: Context) {

    private val prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    fun load(): PlaygroundDesignSnapshot? {
        val raw = prefs.getString(KEY_DESIGN, null) ?: return null
        return runCatching { decode(raw) }.getOrNull()
    }

    fun save(components: List<PlacedComponent>, nextInstanceId: Int) {
        val encoded = encode(components, nextInstanceId)
        prefs.edit().putString(KEY_DESIGN, encoded).apply()
    }

    fun clear() {
        prefs.edit().remove(KEY_DESIGN).apply()
    }

    private fun encode(components: List<PlacedComponent>, nextInstanceId: Int): String {
        val root = JSONObject()
        root.put("nextInstanceId", nextInstanceId)
        val array = JSONArray()
        components.forEach { component ->
            array.put(
                JSONObject().apply {
                    put("instanceId", component.instanceId)
                    put("componentId", component.componentId)
                    put("xFraction", component.xFraction.toDouble())
                    put("yFraction", component.yFraction.toDouble())
                    component.widthFraction?.let { put("widthFraction", it.toDouble()) }
                    component.heightFraction?.let { put("heightFraction", it.toDouble()) }
                    put("marginDp", component.marginDp.toDouble())
                    put("paddingDp", component.paddingDp.toDouble())
                    component.textContent?.let { put("textContent", it) }
                },
            )
        }
        root.put("components", array)
        return root.toString()
    }

    private fun decode(raw: String): PlaygroundDesignSnapshot {
        val root = JSONObject(raw)
        val nextInstanceId = root.optInt("nextInstanceId", 0)
        val array = root.optJSONArray("components") ?: JSONArray()
        val components = buildList {
            for (index in 0 until array.length()) {
                val item = array.getJSONObject(index)
                add(
                    PlacedComponent(
                        instanceId = item.getInt("instanceId"),
                        componentId = item.getString("componentId"),
                        xFraction = item.optFraction("xFraction", "xDp", default = 0.05f),
                        yFraction = item.optFraction("yFraction", "yDp", default = 0.05f),
                        widthFraction = item.optNullableFloat("widthFraction"),
                        heightFraction = item.optNullableFloat("heightFraction"),
                        marginDp = item.optDouble("marginDp", 0.0).toFloat(),
                        paddingDp = item.optDouble("paddingDp", 0.0).toFloat(),
                        textContent = item.optString("textContent").takeIf { it.isNotEmpty() },
                    ),
                )
            }
        }
        return PlaygroundDesignSnapshot(components = components, nextInstanceId = nextInstanceId)
    }

    /** Supports legacy absolute dp positions by converting with a reference canvas size. */
    private fun JSONObject.optFraction(
        fractionKey: String,
        legacyDpKey: String,
        default: Float,
    ): Float {
        if (has(fractionKey)) return getDouble(fractionKey).toFloat()
        if (has(legacyDpKey)) {
            val legacyDp = getDouble(legacyDpKey).toFloat()
            return (legacyDp / LEGACY_REFERENCE_CANVAS_DP).coerceIn(0f, 1f)
        }
        return default
    }

    private fun JSONObject.optNullableFloat(key: String): Float? =
        if (has(key) && !isNull(key)) getDouble(key).toFloat() else null

    companion object {
        private const val PREFS_NAME = "component_playground"
        private const val KEY_DESIGN = "saved_design"
        private const val LEGACY_REFERENCE_CANVAS_DP = 960f
    }
}
