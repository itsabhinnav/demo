package com.test.design

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import com.test.design.component.theme.OemTheme
import com.test.design.core.driving.LocalDrivingUxController
import com.test.design.core.driving.rememberDrivingUxController
import com.test.design.presentation.navigation.AppNavGraph

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val startDemoId = extractDemoId(intent)

        setContent {
            val drivingUxController = rememberDrivingUxController()
            OemTheme(drivingUxState = drivingUxController.state) {
                CompositionLocalProvider(LocalDrivingUxController provides drivingUxController) {
                    AppNavGraph(
                        startDemoId = startDemoId,
                        modifier = Modifier
                            .fillMaxSize()
                            .windowInsetsPadding(WindowInsets.safeDrawing),
                    )
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
    }

    private fun extractDemoId(intent: Intent?): String? {
        val data: Uri = intent?.data ?: return null
        if (data.scheme != "oemdesign" || data.host != "demo") return null
        return data.pathSegments?.lastOrNull()?.takeIf { it.isNotBlank() }
    }
}
