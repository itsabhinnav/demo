package com.test.design

import android.content.Intent
import android.graphics.Color as AndroidColor
import android.graphics.PixelFormat
import android.os.Bundle
import android.view.ViewTreeObserver
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import com.test.design.presentation.assistant.ImmersiveAssistantOverlayService
import com.test.design.presentation.assistant.VirtualAssistantActivity
import com.test.design.presentation.assistant.gallery.AssistantUiGalleryActivity
import com.test.design.presentation.assistant.overlay.AssistantOverlayBootstrapActivity

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.setFormat(PixelFormat.OPAQUE)
        window.setBackgroundDrawableResource(R.color.canvas_background)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(AndroidColor.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(AndroidColor.TRANSPARENT),
        )

        setContent {
            MaterialTheme(colorScheme = darkColorScheme()) {
                AssistBotHome(
                    onOpenAssistant = {
                        VirtualAssistantActivity.launch(this)
                    },
                    onOpenGallery = {
                        startActivity(Intent(this, AssistantUiGalleryActivity::class.java))
                    },
                    onOpenOverlay = {
                        startActivity(Intent(this, AssistantOverlayBootstrapActivity::class.java))
                    },
                    onShowImmersive = {
                        ImmersiveAssistantOverlayService.show(this)
                    },
                )
            }
        }

        val content = findViewById<android.view.View>(android.R.id.content)
        content.setBackgroundResource(R.color.canvas_background)
        val vto = content.viewTreeObserver
        vto.addOnDrawListener(object : ViewTreeObserver.OnDrawListener {
            override fun onDraw() {
                content.post {
                    if (vto.isAlive) {
                        runCatching { vto.removeOnDrawListener(this) }
                    }
                    reportFullyDrawn()
                }
            }
        })
    }
}

@Composable
private fun AssistBotHome(
    onOpenAssistant: () -> Unit,
    onOpenGallery: () -> Unit,
    onOpenOverlay: () -> Unit,
    onShowImmersive: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1C1E22))
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Assist Bot",
            style = MaterialTheme.typography.headlineLarge,
            color = Color.White,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Assistant · gallery · overlay",
            style = MaterialTheme.typography.bodyLarge,
            color = Color(0xFFE0E3E8),
        )
        Spacer(Modifier.height(32.dp))
        val buttonMod = Modifier.widthIn(min = 280.dp)
        Button(onClick = onOpenAssistant, modifier = buttonMod) {
            Text("Open assistant")
        }
        Spacer(Modifier.height(12.dp))
        Button(onClick = onOpenGallery, modifier = buttonMod) {
            Text("UI gallery")
        }
        Spacer(Modifier.height(12.dp))
        Button(onClick = onShowImmersive, modifier = buttonMod) {
            Text("Immersive overlay")
        }
        Spacer(Modifier.height(12.dp))
        Button(onClick = onOpenOverlay, modifier = buttonMod) {
            Text("Legacy overlay")
        }
    }
}
