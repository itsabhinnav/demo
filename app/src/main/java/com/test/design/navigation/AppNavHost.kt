package com.test.design.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.test.design.presentation.home.HomeScreen
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
                onNavigateToMaterialComponents = {
                    navController.navigate(AppDestination.MaterialComponents)
                },
                onNavigateToMotionPhysicsSample = {
                    navController.navigate(AppDestination.MotionPhysicsSample)
                },
            )
        }
        composable(AppDestination.MaterialComponents) {
            MaterialComponentsScreen(
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
