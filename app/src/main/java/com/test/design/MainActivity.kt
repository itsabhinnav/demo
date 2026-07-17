package com.test.design

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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.test.design.navigation.AppDestination
import com.test.design.navigation.AppNavHost
import com.test.design.presentation.DesignAppShell
import com.test.design.presentation.ivi.dashboard.DashboardEvent
import com.test.design.presentation.ivi.dashboard.DashboardViewModel
import com.test.design.presentation.ivi.dashboard.model.DashboardWidget
import com.test.design.presentation.ivi.map.EXTRA_OPEN_DASHBOARD

class MainActivity : ComponentActivity() {

    private var openDashboardRequest = mutableStateOf(false)
    private val dashboardViewModel: DashboardViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(AndroidColor.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(AndroidColor.TRANSPARENT),
        )
        // Immersive temporarily off — keep system bars visible.
        // hideSystemBarsImmersive()

        openDashboardRequest.value = intent.getBooleanExtra(EXTRA_OPEN_DASHBOARD, false)

        setContent {
            val shouldOpenDashboard by openDashboardRequest
            val navController = rememberNavController()

            // Immersive temporarily off; pad for system bars while debugging.
            DesignAppShell(
                applySafeDrawingInsets = true,
                onOpenApps = {
                    navController.navigate(AppDestination.Dashboard) {
                        launchSingleTop = true
                    }
                },
                onOpenSettings = {
                    dashboardViewModel.onEvent(
                        DashboardEvent.WidgetTapped(DashboardWidget.Settings),
                    )
                    // Expand Settings on whichever host is active (shared activity VM).
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
            ) {
                AppNavHost(
                    navController = navController,
                    openDashboardOnStart = shouldOpenDashboard,
                    onDashboardOpened = { openDashboardRequest.value = false },
                    modifier = Modifier.fillMaxSize(),
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        openDashboardRequest.value = intent.getBooleanExtra(EXTRA_OPEN_DASHBOARD, false)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        // Immersive temporarily off.
        // if (hasFocus) hideSystemBarsImmersive()
    }
}
