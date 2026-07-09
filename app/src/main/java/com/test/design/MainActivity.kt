package com.test.design

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import android.graphics.Color as AndroidColor
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.test.design.presentation.common.ScreenWithBackground
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.test.design.core.driving.DrivingUxViewModel
import com.test.design.core.driving.LocalDrivingUxUpdater
import com.test.design.core.motion.LocalMotionSchemeUpdater
import com.test.design.core.motion.MotionSchemeViewModel
import com.test.design.navigation.AppNavHost
import com.test.design.theme.AppTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(AndroidColor.TRANSPARENT),
            navigationBarStyle = SystemBarStyle.dark(AndroidColor.TRANSPARENT),
        )

        setContent {
            val drivingUxViewModel: DrivingUxViewModel = viewModel()
            val motionSchemeViewModel: MotionSchemeViewModel = viewModel()
            val drivingState by drivingUxViewModel.drivingUxState.collectAsStateWithLifecycle()
            val motionScheme by motionSchemeViewModel.motionScheme.collectAsStateWithLifecycle()

            AppTheme(
                drivingUxState = drivingState,
                appMotionScheme = motionScheme,
            ) {
                CompositionLocalProvider(
                    LocalDrivingUxUpdater provides drivingUxViewModel::update,
                    LocalMotionSchemeUpdater provides motionSchemeViewModel::update,
                ) {
                    ScreenWithBackground(
                        modifier = Modifier
                            .fillMaxSize()
                            .windowInsetsPadding(WindowInsets.safeDrawing),
                    ) {
                        AppNavHost(
                            navController = rememberNavController(),
                            modifier = Modifier.fillMaxSize(),
                        )
                    }
                }
            }
        }
    }
}
