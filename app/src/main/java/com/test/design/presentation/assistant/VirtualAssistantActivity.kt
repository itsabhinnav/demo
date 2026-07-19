package com.test.design.presentation.assistant

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color as AndroidColor
import android.graphics.drawable.ColorDrawable
import android.os.Build
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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.core.view.WindowCompat
import com.test.design.presentation.DesignAppShell
import kotlinx.coroutines.flow.collectLatest

/**
 * Standalone assistant task — separate from [com.test.design.MainActivity] / in-app home.
 * Translucent window with blur-behind so the previous screen shows through softly.
 *
 * ```
 * adb shell am start -a com.test.design.action.OPEN_ASSISTANT \
 *   -n com.test.design/.presentation.assistant.VirtualAssistantActivity
 * ```
 *
 * In-app mic / widget still uses [VirtualAssistantScreen] over the dashboard.
 */
class VirtualAssistantActivity : ComponentActivity() {

    private var micGranted by mutableStateOf(false)
    private var summonEpoch by mutableIntStateOf(0)

    private val micPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { granted ->
        micGranted = granted
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupTranslucentBlurWindow()

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
                    if (summonEpoch > 0) {
                        notifyImmersiveAssistantHotword()
                    }
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        summonEpoch++
    }

    private fun setupTranslucentBlurWindow() {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = AndroidColor.TRANSPARENT
        window.navigationBarColor = AndroidColor.TRANSPARENT
        window.setBackgroundDrawable(ColorDrawable(AndroidColor.TRANSPARENT))
        window.setDimAmount(0f)
        window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            window.setBackgroundBlurRadius(48)
            window.attributes = window.attributes.apply {
                blurBehindRadius = 48
                flags = flags or WindowManager.LayoutParams.FLAG_BLUR_BEHIND
            }
        }
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(AndroidColor.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(AndroidColor.TRANSPARENT),
        )
    }

    companion object {
        const val ACTION_OPEN_ASSISTANT = "com.test.design.action.OPEN_ASSISTANT"
    }
}
