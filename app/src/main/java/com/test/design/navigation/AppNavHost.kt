package com.test.design.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.test.design.presentation.ivi.dashboard.IviDemoScreen
import com.test.design.presentation.ivi.driving.DrivingHomeScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = AppDestination.DrivingHome,
        modifier = modifier,
    ) {
        composable(AppDestination.DrivingHome) {
            DrivingHomeScreen(
                onOpenWidgetDashboard = {
                    navController.navigate(AppDestination.Dashboard)
                },
            )
        }
        composable(AppDestination.Dashboard) {
            IviDemoScreen(
                onBack = { navController.popBackStack() },
            )
        }
    }
}
