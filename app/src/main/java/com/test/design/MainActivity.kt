package com.test.design

import android.content.Intent
import android.graphics.PixelFormat
import android.os.Bundle
import android.view.ViewTreeObserver
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import android.graphics.Color as AndroidColor
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.navigation.compose.rememberNavController
import com.test.design.navigation.AppDestination
import com.test.design.navigation.AppNavHost
import com.test.design.presentation.DesignAppShell
import com.test.design.presentation.assistant.ImmersiveAssistantOverlayService
import com.test.design.presentation.ivi.dashboard.DashboardEvent
import com.test.design.presentation.ivi.dashboard.DashboardViewModel
import com.test.design.presentation.ivi.dashboard.FloatingSystemBarsVisibility
import com.test.design.presentation.ivi.dashboard.model.DashboardWidget
import com.test.design.presentation.ivi.map.EXTRA_OPEN_DASHBOARD

class MainActivity : ComponentActivity() {

    private var openDashboardRequest = mutableStateOf(false)
    private val dashboardViewModel: DashboardViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FloatingSystemBarsVisibility.hide()

        // Keep the activity buffer opaque — transparent BLAST buffers read as a black screen
        // on the AAOS emulator (screencap pixels were RGBA 0,0,0,0).
        window.setFormat(PixelFormat.OPAQUE)
        window.setBackgroundDrawableResource(R.color.canvas_background)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(AndroidColor.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(AndroidColor.TRANSPARENT),
        )

        openDashboardRequest.value = intent.getBooleanExtra(EXTRA_OPEN_DASHBOARD, false)

        setContent {
            val shouldOpenDashboard by openDashboardRequest
            val navController = rememberNavController()

            DesignAppShell(
                applySafeDrawingInsets = true,
                showFloatingSystemBars = true,
                onOpenApps = {
                    navController.navigate(AppDestination.Dashboard) {
                        launchSingleTop = true
                    }
                },
                onOpenSettings = {
                    dashboardViewModel.onEvent(
                        DashboardEvent.WidgetTapped(DashboardWidget.Settings),
                    )
                    val route = navController.currentBackStackEntry?.destination?.route
                    if (route == AppDestination.Dashboard) return@DesignAppShell
                    navController.navigate(AppDestination.DrivingHome) {
                        popUpTo(AppDestination.DrivingHome) { inclusive = false }
                        launchSingleTop = true
                    }
                },
                onOpenHome = {
                    dashboardViewModel.onEvent(DashboardEvent.CollapseWidget)
                    navController.navigate(AppDestination.DrivingHome) {
                        popUpTo(AppDestination.DrivingHome) { inclusive = false }
                        launchSingleTop = true
                    }
                },
                onOpenAssistant = {
                    ImmersiveAssistantOverlayService.show(this@MainActivity)
                },
            ) {
                AppNavHost(
                    navController = navController,
                    openDashboardOnStart = shouldOpenDashboard,
                    onDashboardOpened = { openDashboardRequest.value = false },
                    modifier = Modifier.fillMaxSize(),
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

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        openDashboardRequest.value = intent.getBooleanExtra(EXTRA_OPEN_DASHBOARD, false)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
    }
}
