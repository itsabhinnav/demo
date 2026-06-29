package com.test.design.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.test.design.presentation.demo.DemoRoute
import com.test.design.presentation.home.HomeRoute

@Composable
fun AppNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
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

        composable(Routes.DEMO) { backStackEntry ->
            val demoId = backStackEntry.arguments?.getString("demoId").orEmpty()
            DemoRoute(
                demoId = demoId,
                onNavigateBack = { navController.popBackStack() },
            )
        }
    }
}
