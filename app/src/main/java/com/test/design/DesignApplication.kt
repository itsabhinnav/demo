package com.test.design

import android.app.Application
import com.test.design.presentation.assistant.AssistantFaceConfig
import org.osmdroid.config.Configuration

/**
 * Eager OsmDroid config so [com.test.design.presentation.ivi.navigation.components.OsmMapBackground]
 * does not hit disk/SharedPreferences on the first Compose frame.
 */
class DesignApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AssistantFaceConfig.install(this)
        Configuration.getInstance().apply {
            load(this@DesignApplication, getSharedPreferences(OSM_PREFS, MODE_PRIVATE))
            userAgentValue = packageName
        }
    }

    companion object {
        const val OSM_PREFS = "osmdroid"
    }
}
