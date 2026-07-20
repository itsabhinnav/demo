package com.test.design.presentation.assistant.gallery

import android.graphics.Color as AndroidColor
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import com.test.design.presentation.DesignAppShell

/**
 * Transparent host for the semi-transparent assistant UI gallery.
 *
 * ADB:
 * ```
 * adb shell am start -n com.test.design/.presentation.assistant.gallery.AssistantUiGalleryActivity
 * ```
 */
class AssistantUiGalleryActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.setBackgroundDrawable(ColorDrawable(AndroidColor.TRANSPARENT))
        window.setDimAmount(0f)
        window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
        // Explicitly clear blur-behind — theme must not enable it (emulator SF crash).
        window.clearFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND)
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S) {
            window.attributes = window.attributes.apply { blurBehindRadius = 0 }
        }
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(AndroidColor.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(AndroidColor.TRANSPARENT),
        )

        val initial = intent?.getStringExtra(EXTRA_STYLE)
            ?.let { runCatching { AssistantUiStyle.valueOf(it) }.getOrNull() }
            ?: AssistantUiStyle.VoicePlate

        setContent {
            DesignAppShell(
                applySafeDrawingInsets = false,
                showFloatingSystemBars = false,
                showScreenBackground = false,
            ) {
                AssistantUiGalleryScreen(
                    onClose = { finish() },
                    modifier = Modifier.fillMaxSize(),
                    initialStyle = initial,
                )
            }
        }
    }

    companion object {
        const val EXTRA_STYLE = "style"
        const val ACTION_OPEN_GALLERY = "com.test.design.action.OPEN_ASSISTANT_GALLERY"
    }
}
