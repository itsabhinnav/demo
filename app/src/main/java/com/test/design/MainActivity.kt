package com.test.design

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.test.design.component.theme.OemTheme
import com.test.design.core.driving.DrivingUxViewModel
import com.test.design.core.driving.LocalDrivingUxUpdater
import com.test.design.di.AppContainer
import com.test.design.presentation.navigation.AppNavGraph
import com.test.design.presentation.navigation.AppNavigationViewModel
import com.test.design.presentation.navigation.AppNavigationViewModelFactory

class MainActivity : ComponentActivity() {

    private val navigationViewModel: AppNavigationViewModel by viewModels {
        AppNavigationViewModelFactory(AppContainer.featureDemoRepository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        navigationViewModel.handleIntent(intent)

        setContent {
            val drivingUxViewModel: DrivingUxViewModel = viewModel()
            val drivingState by drivingUxViewModel.drivingUxState.collectAsStateWithLifecycle()
            val pendingNavigation by navigationViewModel.pendingNavigation.collectAsStateWithLifecycle()

            OemTheme(drivingUxState = drivingState) {
                CompositionLocalProvider(
                    LocalDrivingUxUpdater provides drivingUxViewModel::update,
                ) {
                    AppNavGraph(
                        pendingNavigation = pendingNavigation,
                        onPendingNavigationConsumed = navigationViewModel::consumePendingNavigation,
                        modifier = Modifier
                            .fillMaxSize()
                            .windowInsetsPadding(WindowInsets.safeDrawing),
                    )
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        navigationViewModel.handleIntent(intent)
    }
}
