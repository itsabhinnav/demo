package com.test.design.presentation.assistant

import android.graphics.Color as AndroidColor
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.test.design.presentation.DesignAppShell

/**
 * Transparent virtual-assistant host — only the side panel is drawn.
 *
 * Launch from the app launcher or via [ACTION_OPEN_ASSISTANT]. Content behind
 * the activity stays fully visible (no scrim / dim).
 */
class VirtualAssistantActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setBackgroundDrawable(ColorDrawable(AndroidColor.TRANSPARENT))
        window.setDimAmount(0f)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(AndroidColor.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(AndroidColor.TRANSPARENT),
        )

        setContent {
            DesignAppShell(
                applySafeDrawingInsets = false,
                showFloatingSystemBars = false,
                showScreenBackground = false,
            ) {
                VirtualAssistantOverlay(
                    onDismiss = { finish() },
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }

    companion object {
        const val ACTION_OPEN_ASSISTANT = "com.test.design.action.OPEN_ASSISTANT"
    }
}
