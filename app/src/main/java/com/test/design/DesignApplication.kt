package com.test.design

import android.app.Application
import com.test.design.assistant.api.AssistantRuntime
import com.test.design.presentation.assistant.AssistantFaceConfig
import com.test.design.presentation.assistant.DesignAssistantHost
import com.test.design.presentation.assistant.backend.DemoAssistantBackend
import com.test.design.presentation.assistant.backend.platformAssistantTts
import org.osmdroid.config.Configuration

/**
 * Eager OsmDroid config so [com.test.design.presentation.ivi.navigation.components.OsmMapBackground]
 * does not hit disk/SharedPreferences on the first Compose frame.
 *
 * Also installs the assistant host bridge + demo backend. Swap
 * [DemoAssistantBackend] for a remote/LLM implementation without touching UI.
 */
class DesignApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        AssistantFaceConfig.install(this)
        AssistantRuntime.install(
            host = DesignAssistantHost(this),
            backend = DemoAssistantBackend(
                speakingTts = platformAssistantTts(this),
            ),
        )
        Configuration.getInstance().apply {
            load(this@DesignApplication, getSharedPreferences(OSM_PREFS, MODE_PRIVATE))
            userAgentValue = packageName
        }
    }

    companion object {
        const val OSM_PREFS = "osmdroid"
    }
}
