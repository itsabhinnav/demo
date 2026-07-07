package com.test.design

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.test.design.core.driving.DrivingUxViewModel
import com.test.design.core.driving.LocalDrivingUxUpdater
import com.test.design.presentation.home.HomeScreen
import com.test.design.theme.AppTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            val drivingUxViewModel: DrivingUxViewModel = viewModel()
            val drivingState by drivingUxViewModel.drivingUxState.collectAsStateWithLifecycle()

            AppTheme(drivingUxState = drivingState) {
                CompositionLocalProvider(
                    LocalDrivingUxUpdater provides drivingUxViewModel::update,
                ) {
                    HomeScreen(
                        modifier = Modifier
                            .fillMaxSize()
                            .windowInsetsPadding(WindowInsets.safeDrawing),
                    )
                }
            }
        }
    }
}
