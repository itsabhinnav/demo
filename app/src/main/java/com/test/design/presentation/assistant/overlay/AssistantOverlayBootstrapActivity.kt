package com.test.design.presentation.assistant.overlay

import android.content.Intent
import android.graphics.Color as AndroidColor
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.test.design.presentation.DesignAppShell

/**
 * Thin launcher that requests overlay permission then starts [AssistantOverlayService].
 *
 * ADB:
 * ```
 * adb shell am start -n com.test.design/.presentation.assistant.overlay.AssistantOverlayBootstrapActivity
 * adb shell am startservice -n com.test.design/.presentation.assistant.overlay.AssistantOverlayService
 * adb shell am startservice -a com.test.design.assistant.UPDATE_STATE --es state LISTENING \
 *   -n com.test.design/.presentation.assistant.overlay.AssistantOverlayService
 * ```
 */
class AssistantOverlayBootstrapActivity : ComponentActivity() {

    private var hasOverlay by mutableStateOf(false)

    private val overlayPermission = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
    ) {
        hasOverlay = Settings.canDrawOverlays(this)
        if (hasOverlay) {
            AssistantOverlayService.start(this)
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.setBackgroundDrawable(ColorDrawable(AndroidColor.TRANSPARENT))
        enableEdgeToEdge()
        hasOverlay = Settings.canDrawOverlays(this)
        if (hasOverlay) {
            AssistantOverlayService.start(this)
            finish()
            return
        }
        setContent {
            DesignAppShell(
                applySafeDrawingInsets = true,
                showFloatingSystemBars = false,
                showScreenBackground = true,
            ) {
                OverlayPermissionGate(
                    onGrant = {
                        overlayPermission.launch(
                            Intent(
                                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                                Uri.parse("package:$packageName"),
                            ),
                        )
                    },
                )
            }
        }
    }
}

@Composable
private fun OverlayPermissionGate(onGrant: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Allow display over other apps",
            style = MaterialTheme.typography.titleMedium,
        )
        Text(
            text = "Required for the floating assistant capsule.",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(top = 8.dp, bottom = 24.dp),
        )
        Button(onClick = onGrant) {
            Text("Grant overlay permission")
        }
    }
}
