package com.test.design.presentation.ivi.map

import android.content.Intent
import android.graphics.Color as AndroidColor
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.test.design.presentation.DesignAppShell

/**
 * Dedicated map activity for AAOS Scalable UI map panels and the Maps launcher icon.
 *
 * Always opens [NavigationScreen] (same as tapping "Search maps" on the driving home).
 * Draws the map edge-to-edge; overlay cards respect Scalable UI SafeBounds / system
 * insets via [WindowInsets.safeDrawing][androidx.compose.foundation.layout.WindowInsets].
 *
 * Launch via:
 * - App launcher (MAIN / LAUNCHER)
 * - [MapIntents.openMap] or [ACTION_OPEN_MAP]
 * - `geo:` / `androidx.car.app.action.NAVIGATE` intents
 *
 * Not used as Scalable UI `map_panel` host (that uses [ScalableUiBackdropActivity]).
 */
class MapActivity : ComponentActivity() {

    private val mapViewModel: MapViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(AndroidColor.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(AndroidColor.TRANSPARENT),
        )
        // Do not hide system bars — CarSystemUI owns status/nav Scalable UI panels.

        mapViewModel.applyIntent(intent)

        setContent {
            DesignAppShell(
                applySafeDrawingInsets = false,
                showFloatingSystemBars = false,
                showScreenBackground = false,
                onOpenApps = {
                    startActivity(MapIntents.openMain(this, openDashboard = true))
                },
                onOpenSettings = {
                    startActivity(MapIntents.openMain(this, openDashboard = true))
                },
                onOpenHome = {
                    startActivity(MapIntents.openMain(this))
                    finish()
                },
            ) {
                MapHostContent(
                    onBack = {
                        startActivity(MapIntents.openMain(this))
                        finish()
                    },
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        mapViewModel.applyIntent(intent)
    }
}
