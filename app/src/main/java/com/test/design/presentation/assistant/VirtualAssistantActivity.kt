package com.test.design.presentation.assistant

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color as AndroidColor
import android.os.Bundle
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
import com.test.design.presentation.DesignAppShell
import kotlinx.coroutines.flow.collectLatest

/**
 * Standalone assistant entry — separate from [com.test.design.MainActivity] / in-app home.
 *
 * Hosts the immersive UI in this activity (reliable on AAOS). System overlay windows are
 * force-hidden while Settings / other apps set hide-non-system-overlay, so we no longer
 * trampoline into [ImmersiveAssistantOverlayService] and finish.
 *
 * ```
 * adb shell am start -a com.test.design.action.OPEN_ASSISTANT \
 *   -n com.test.design/.presentation.assistant.VirtualAssistantActivity
 * ```
 */
class VirtualAssistantActivity : ComponentActivity() {

    private var micGranted by mutableStateOf(false)
    private var summonEpoch by mutableStateOf(0)

    private val micPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { granted ->
        micGranted = granted
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(AndroidColor.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(AndroidColor.TRANSPARENT),
        )

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
                        notifyImmersiveAssistantHotword()
                    }
                }
                VirtualAssistantOverlay(
                    onDismiss = { finish() },
                    modifier = Modifier.fillMaxSize(),
                    awaitHotword = false,
                )
                LaunchedEffect(summonEpoch) {
                    if (summonEpoch > 0) notifyImmersiveAssistantHotword()
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        summonEpoch++
    }

    companion object {
        const val ACTION_OPEN_ASSISTANT = "com.test.design.action.OPEN_ASSISTANT"

        fun launch(context: Context) {
            context.startActivity(
                Intent(context, VirtualAssistantActivity::class.java).apply {
                    action = ACTION_OPEN_ASSISTANT
                    // Stay on the main AAOS app panel (same affinity as MainActivity).
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                },
            )
        }
    }
}
