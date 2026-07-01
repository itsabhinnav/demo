package com.test.design.presentation.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.test.design.component.core.currentDrivingUxState
import com.test.design.component.motion.OemMotion
import com.test.design.presentation.demo.DemoRoute
import com.test.design.presentation.home.HomeRoute

@Composable
fun AppNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDemoId: String? = null,
) {
    val drivingState = currentDrivingUxState()

    LaunchedEffect(startDemoId) {
        if (!startDemoId.isNullOrBlank()) {
            navController.navigate(Routes.demo(startDemoId)) {
                popUpTo(Routes.HOME)
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = Routes.HOME,
        modifier = modifier,
    ) {
        composable(Routes.HOME) {
            HomeRoute(
                onNavigateToDemo = { demoId ->
                    navController.navigate(Routes.demo(demoId))
                },
            )
        }

        composable(
            route = Routes.DEMO,
            enterTransition = { NavMotion.detailEnter(drivingState) },
            exitTransition = { NavMotion.detailExit(drivingState) },
            popEnterTransition = {
                val duration = OemMotion.durationMs(drivingState, opening = true, OemMotion.CloseDurationMs)
                if (duration == 0) {
                    EnterTransition.None
                } else {
                    fadeIn(tween(durationMillis = duration, easing = OemMotion.StandardEasing))
                }
            },
            popExitTransition = { NavMotion.detailExit(drivingState) },
        ) { backStackEntry ->
            val demoId = backStackEntry.arguments?.getString("demoId").orEmpty()
            DemoRoute(
                demoId = demoId,
                onNavigateBack = { navController.popBackStack() },
            )
        }
    }
}
