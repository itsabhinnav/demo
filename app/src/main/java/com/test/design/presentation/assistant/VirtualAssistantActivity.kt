package com.test.design.presentation.assistant

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color as AndroidColor
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import com.test.design.presentation.DesignAppShell
import kotlinx.coroutines.flow.collectLatest

/**
 * Transparent NOMI overlay on whatever is already on screen (same task as main).
 *
 * Launch over existing content:
 * ```
 * adb shell am start -a com.test.design.action.OPEN_ASSISTANT \
 *   -n com.test.design/.presentation.assistant.VirtualAssistantActivity
 * ```
 *
 * Say “Hey assistant” (or tap) for a random peek / bounce / fall entrance.
 */
class VirtualAssistantActivity : ComponentActivity() {

    private var micGranted by mutableStateOf(false)

    private val micPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { granted ->
        micGranted = granted
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupTransparentOverlayWindow()

        micGranted = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) ==
            PackageManager.PERMISSION_GRANTED
        if (!micGranted) {
            micPermission.launch(Manifest.permission.RECORD_AUDIO)
        }

        setContent {
            DesignAppShell(
                applySafeDrawingInsets = false,
                showFloatingSystemBars = false,
                showScreenBackground = false,
            ) {
                LaunchedEffect(micGranted) {
                    if (!micGranted) return@LaunchedEffect
                    hotwordDetections(this@VirtualAssistantActivity).collectLatest {
                        notifyAssistantHotword()
                    }
                }

                VirtualAssistantOverlay(
                    onDismiss = { /* keep listening for the next hotword */ },
                    modifier = Modifier.fillMaxSize(),
                    awaitHotword = true,
                )
            }
        }
    }

    private fun setupTransparentOverlayWindow() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = AndroidColor.TRANSPARENT
        window.navigationBarColor = AndroidColor.TRANSPARENT
        window.setBackgroundDrawable(ColorDrawable(AndroidColor.TRANSPARENT))
        window.setDimAmount(0f)
        window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        window.addFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS or
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
        )
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(AndroidColor.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(AndroidColor.TRANSPARENT),
        )
    }

    companion object {
        const val ACTION_OPEN_ASSISTANT = "com.test.design.action.OPEN_ASSISTANT"
    }
}
