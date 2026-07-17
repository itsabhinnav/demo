package com.test.design.presentation.assistant

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color as AndroidColor
import android.graphics.drawable.ColorDrawable
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
 * Transparent NOMI host — black orb only, no panel / no text.
 *
 * Listens for “Hey assistant”; on match the orb peeks / bounces / falls in
 * with a random entrance. Tap summons when speech isn’t available.
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
        window.setBackgroundDrawable(ColorDrawable(AndroidColor.TRANSPARENT))
        window.setDimAmount(0f)
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

    companion object {
        const val ACTION_OPEN_ASSISTANT = "com.test.design.action.OPEN_ASSISTANT"
    }
}
