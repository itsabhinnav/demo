package com.test.design.presentation

import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.test.design.core.driving.DrivingUxViewModel
import com.test.design.core.driving.LocalDrivingUxUpdater
import com.test.design.core.motion.LocalMotionSchemeUpdater
import com.test.design.core.motion.MotionSchemeViewModel
import com.test.design.core.theme.LocalThemeModeUpdater
import com.test.design.core.theme.ThemeModeViewModel
import com.test.design.presentation.common.ScreenWithBackground
import com.test.design.presentation.ivi.climate.ClimateViewModel
import com.test.design.presentation.ivi.dashboard.components.FloatingBottomSystemBar
import com.test.design.presentation.ivi.dashboard.components.FloatingSystemBarEdgeInset
import com.test.design.presentation.ivi.dashboard.components.FloatingSystemBarInset
import com.test.design.presentation.ivi.dashboard.components.FloatingTopSystemBar
import com.test.design.theme.AppTheme

/** Activity-scoped ViewModel shared across NavHost destinations (e.g. climate for floating bars). */
@Composable
inline fun <reified VM : ViewModel> activityViewModel(): VM {
    val activity = LocalContext.current as ComponentActivity
    return viewModel(viewModelStoreOwner = activity)
}

@Composable
fun DesignAppShell(
    applySafeDrawingInsets: Boolean = true,
    showFloatingSystemBars: Boolean = true,
    /** When false, skip opaque screen backdrop (Scalable UI panel hosts). */
    showScreenBackground: Boolean = true,
    onOpenApps: () -> Unit = {},
    onOpenSettings: () -> Unit = {},
    onOpenHome: () -> Unit = {},
    content: @Composable () -> Unit,
) {
    val drivingUxViewModel: DrivingUxViewModel = viewModel()
    val motionSchemeViewModel: MotionSchemeViewModel = viewModel()
    val themeModeViewModel: ThemeModeViewModel = viewModel()
    val climateViewModel: ClimateViewModel = activityViewModel()
    val drivingState by drivingUxViewModel.drivingUxState.collectAsStateWithLifecycle()
    val motionScheme by motionSchemeViewModel.motionScheme.collectAsStateWithLifecycle()
    val themeMode by themeModeViewModel.themeMode.collectAsStateWithLifecycle()
    val climateState by climateViewModel.state.collectAsStateWithLifecycle()

    AppTheme(
        themeMode = themeMode,
        drivingUxState = drivingState,
        appMotionScheme = motionScheme,
    ) {
        CompositionLocalProvider(
            LocalDrivingUxUpdater provides drivingUxViewModel::update,
            LocalMotionSchemeUpdater provides motionSchemeViewModel::update,
            LocalThemeModeUpdater provides themeModeViewModel::update,
        ) {
            val shellModifier = if (applySafeDrawingInsets) {
                Modifier
                    .fillMaxSize()
                    .windowInsetsPadding(WindowInsets.safeDrawing)
            } else {
                Modifier.fillMaxSize()
            }
            val body = @Composable {
                Box(modifier = Modifier.fillMaxSize()) {
                    content()
                    if (showFloatingSystemBars) {
                        FloatingTopSystemBar(
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .padding(
                                    start = FloatingSystemBarInset,
                                    end = FloatingSystemBarInset,
                                    top = FloatingSystemBarEdgeInset,
                                ),
                        )
                        FloatingBottomSystemBar(
                            climateState = climateState,
                            onClimateEvent = climateViewModel::onEvent,
                            onOpenApps = onOpenApps,
                            onOpenSettings = onOpenSettings,
                            onOpenHome = onOpenHome,
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(
                                    start = FloatingSystemBarInset,
                                    end = FloatingSystemBarInset,
                                    bottom = FloatingSystemBarEdgeInset,
                                ),
                        )
                    }
                }
            }
            if (showScreenBackground) {
                ScreenWithBackground(modifier = shellModifier, content = body)
            } else {
                Box(modifier = shellModifier, content = { body() })
            }
        }
    }
}
