package com.test.design.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.test.design.presentation.home.HomeScreen
import com.test.design.presentation.ivi.dashboard.IviDemoScreen
import com.test.design.presentation.material.CustomizedMaterialComponentsScreen
import com.test.design.presentation.material.MaterialComponentsScreen
import com.test.design.presentation.motion.MotionPhysicsSampleScreen

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = AppDestination.Home,
        modifier = modifier,
    ) {
        composable(AppDestination.Home) {
            HomeScreen(
                onNavigateToIviDemo = {
                    navController.navigate(AppDestination.IviExpressiveDemo)
                },
                onNavigateToMaterialComponents = {
                    navController.navigate(AppDestination.MaterialComponents)
                },
                onNavigateToCustomizedMaterialComponents = {
                    navController.navigate(AppDestination.CustomizedMaterialComponents)
                },
                onNavigateToMotionLab = {
                    navController.navigate(AppDestination.MotionPhysicsSample)
                },
            )
        }
        composable(AppDestination.IviExpressiveDemo) {
            IviDemoScreen(
                onExit = { navController.popBackStack() },
            )
        }
        composable(AppDestination.MaterialComponents) {
            MaterialComponentsScreen(
                onBack = { navController.popBackStack() },
            )
        }
        composable(AppDestination.CustomizedMaterialComponents) {
            CustomizedMaterialComponentsScreen(
                onBack = { navController.popBackStack() },
            )
        }
        composable(AppDestination.MotionPhysicsSample) {
            MotionPhysicsSampleScreen(
                onBack = { navController.popBackStack() },
            )
        }
    }
}
