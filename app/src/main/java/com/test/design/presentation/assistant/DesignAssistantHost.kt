package com.test.design.presentation.assistant

import android.app.Application
import com.test.design.assistant.api.AssistantCabinContext
import com.test.design.assistant.api.AssistantHost

/**
 * Process-wide cabin snapshot for [DesignAssistantHost].
 * Host apps can publish updated [AssistantCabinContext] values at runtime.
 */
object DesignCabinContextStore {
    @Volatile
    var latest: AssistantCabinContext = AssistantCabinContext()
        private set

    fun publish(context: AssistantCabinContext) {
        latest = context
    }
}

/**
 * Host bridge — cabin context + optional cluster hand-off.
 */
class DesignAssistantHost(
    @Suppress("UNUSED_PARAMETER") private val app: Application,
) : AssistantHost {
    override fun cabinContext(): AssistantCabinContext = DesignCabinContextStore.latest

    override fun openClusterHandOff() {
        // No cluster surface in the standalone assist-bot host.
    }
}
