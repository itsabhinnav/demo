package com.test.design.presentation.ivi.map

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import android.graphics.Color as AndroidColor
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.test.design.presentation.DesignAppShell

/**
 * Dedicated map activity for AAOS Scalable UI map panels and the Maps launcher icon.
 *
 * Always opens [NavigationScreen] (same as tapping "Search maps" on the driving home).
 * Never hosts [com.test.design.presentation.ivi.driving.DrivingHomeScreen] — that is MainActivity only.
 *
 * Launch via:
 * - App launcher (MAIN / LAUNCHER / APP_MAPS) — separate task, does not open MainActivity
 * - Scalable UI `config_default_activities`: `map_panel;com.test.design/.presentation.ivi.map.MapActivity`
 * - [MapIntents.openMap] or [ACTION_OPEN_MAP]
 * - `geo:` / `androidx.car.app.action.NAVIGATE` intents
 */
class MapActivity : ComponentActivity() {

    private val mapViewModel: MapViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(AndroidColor.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(AndroidColor.TRANSPARENT),
        )

        mapViewModel.applyIntent(intent)

        setContent {
            val config by mapViewModel.config.collectAsStateWithLifecycle()

            DesignAppShell {
                MapHostContent(
                    showRoute = config.showRoute || config.expandNavigation,
                    onBack = { finish() },
                    onOpenDesign = {
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
