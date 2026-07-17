package com.test.design.presentation.assistant

import android.graphics.Color as AndroidColor
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.test.design.presentation.DesignAppShell
import com.test.design.presentation.ivi.map.MapIntents

/**
 * Standalone virtual-assistant personality demo — eyes + mouth with mood animations.
 *
 * Launch from the app launcher or via [ACTION_OPEN_ASSISTANT].
 */
class VirtualAssistantActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(AndroidColor.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(AndroidColor.TRANSPARENT),
        )

        setContent {
            DesignAppShell(
                applySafeDrawingInsets = true,
                showFloatingSystemBars = false,
                onOpenApps = {
                    startActivity(MapIntents.openMain(this, openDashboard = true))
                },
                onOpenSettings = {
                    startActivity(MapIntents.openMain(this, openDashboard = true))
                },
                onOpenHome = {
                    startActivity(MapIntents.openMain(this))
                    finish()
                },
            ) {
                VirtualAssistantScreen(
                    onBack = {
                        startActivity(MapIntents.openMain(this))
                        finish()
                    },
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }

    companion object {
        const val ACTION_OPEN_ASSISTANT = "com.test.design.action.OPEN_ASSISTANT"
    }
}
