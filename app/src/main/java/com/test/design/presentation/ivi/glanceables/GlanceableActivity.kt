package com.test.design.presentation.ivi.glanceables

import android.graphics.Color as AndroidColor
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.test.design.presentation.DesignAppShell

/**
 * Base for Scalable UI TaskPanel glanceables.
 *
 * SystemUI owns status/dock chrome; these activities only draw panel content
 * and respect SafeBounds via window insets when [applySafeDrawingInsets] is true.
 */
abstract class GlanceableActivity : ComponentActivity() {

    protected open val applySafeDrawingInsets: Boolean = false

    @Composable
    protected abstract fun GlanceContent()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(AndroidColor.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(AndroidColor.TRANSPARENT),
        )
        // Do not hide system bars — CarSystemUI Scalable UI owns status/nav panels.
        setContent {
            DesignAppShell(
                applySafeDrawingInsets = applySafeDrawingInsets,
                showFloatingSystemBars = false,
                showScreenBackground = false,
            ) {
                GlanceContent()
            }
        }
    }
}

@Composable
internal fun GlanceRoot(content: @Composable () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), content = { content() })
}
