package com.test.design.presentation

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.test.design.core.driving.DrivingUxViewModel
import com.test.design.core.driving.LocalDrivingUxUpdater
import com.test.design.core.motion.LocalMotionSchemeUpdater
import com.test.design.core.motion.MotionSchemeViewModel
import com.test.design.presentation.common.ScreenWithBackground
import com.test.design.theme.AppTheme

@Composable
fun DesignAppShell(
    applySafeDrawingInsets: Boolean = true,
    content: @Composable () -> Unit,
) {
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
            val shellModifier = if (applySafeDrawingInsets) {
                Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets.safeDrawing)
            } else {
                Modifier.fillMaxSize()
            }
            ScreenWithBackground(modifier = shellModifier) {
                content()
            }
        }
    }
}
