package com.test.design.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.test.design.core.driving.DrivingUxViewModel
import com.test.design.core.driving.LocalDrivingUxUpdater
import com.test.design.core.motion.LocalMotionSchemeUpdater
import com.test.design.core.motion.MotionSchemeViewModel
import com.test.design.core.theme.LocalThemeModeUpdater
import com.test.design.core.theme.ThemeModeViewModel
import com.test.design.presentation.assistant.AssistantAppearanceViewModel
import com.test.design.presentation.assistant.DesignCabinContextStore
import com.test.design.presentation.assistant.LocalAssistantChromeBottomSpace
import com.test.design.presentation.assistant.LocalAssistantHighContrast
import com.test.design.presentation.assistant.LocalAssistantHighContrastUpdater
import com.test.design.presentation.common.ScreenWithBackground
import com.test.design.presentation.ivi.climate.ClimateViewModel
import com.test.design.presentation.ivi.dashboard.FloatingSystemBarsVisibility
import com.test.design.presentation.ivi.dashboard.components.FloatingBottomSystemBar
import com.test.design.presentation.ivi.dashboard.components.FloatingSystemBarEdgeInset
import com.test.design.presentation.ivi.dashboard.components.FloatingSystemBarInset
import com.test.design.presentation.ivi.dashboard.components.FloatingTopSystemBar
import com.test.design.presentation.ivi.dashboard.components.rememberedFloatingChromeBottomSpace
import com.test.design.presentation.ivi.hun.DemoHunNotifications
import com.test.design.presentation.ivi.hun.HeadsUpNotificationHost
import com.test.design.presentation.ivi.vehicle.VehicleViewModel
import com.test.design.theme.AppTheme

/** Host-scoped ViewModel (Activity or overlay Service ViewModelStoreOwner). */
@Composable
inline fun <reified VM : ViewModel> activityViewModel(): VM {
    val owner = checkNotNull(LocalViewModelStoreOwner.current) {
        "No ViewModelStoreOwner provided for activityViewModel<${VM::class.java.simpleName}>()"
    }
    return viewModel(viewModelStoreOwner = owner)
}

@Composable
fun DesignAppShell(
    applySafeDrawingInsets: Boolean = true,
    /** Host allows floating bars; runtime visibility is [FloatingSystemBarsVisibility] (hidden by default). */
    showFloatingSystemBars: Boolean = false,
    /** When false, skip opaque screen backdrop (Scalable UI panel hosts). */
    showScreenBackground: Boolean = true,
    onOpenApps: () -> Unit = {},
    onOpenSettings: () -> Unit = {},
    onOpenHome: () -> Unit = {},
    onOpenAssistant: () -> Unit = {},
    content: @Composable () -> Unit,
) {
    val drivingUxViewModel: DrivingUxViewModel = viewModel()
    val motionSchemeViewModel: MotionSchemeViewModel = viewModel()
    val themeModeViewModel: ThemeModeViewModel = viewModel()
    val assistantAppearanceViewModel: AssistantAppearanceViewModel = viewModel()
    val climateViewModel: ClimateViewModel = activityViewModel()
    val vehicleViewModel: VehicleViewModel = activityViewModel()
    val drivingState by drivingUxViewModel.drivingUxState.collectAsStateWithLifecycle()
    val motionScheme by motionSchemeViewModel.motionScheme.collectAsStateWithLifecycle()
    val themeMode by themeModeViewModel.themeMode.collectAsStateWithLifecycle()
    val assistantHighContrast by assistantAppearanceViewModel.highContrast.collectAsStateWithLifecycle()
    val climateState by climateViewModel.state.collectAsStateWithLifecycle()
    val vehicleState by vehicleViewModel.state.collectAsStateWithLifecycle()
    val adbSystemBarsVisible by FloatingSystemBarsVisibility.visible.collectAsStateWithLifecycle()
    val barsVisible = showFloatingSystemBars && adbSystemBarsVisible

    var hunVisible by remember { mutableStateOf(false) }
    var hunNotifications by remember { mutableStateOf(DemoHunNotifications) }

    LaunchedEffect(barsVisible) {
        if (!barsVisible) hunVisible = false
    }

    LaunchedEffect(drivingState, vehicleState) {
        DesignCabinContextStore.publish(drivingState, vehicleState)
    }

    AppTheme(
        themeMode = themeMode,
        drivingUxState = drivingState,
        appMotionScheme = motionScheme,
    ) {
        val floatingChromeBottom = rememberedFloatingChromeBottomSpace()
        val chromeBottom = if (barsVisible) floatingChromeBottom else 0.dp
        CompositionLocalProvider(
            LocalDrivingUxUpdater provides drivingUxViewModel::update,
            LocalMotionSchemeUpdater provides motionSchemeViewModel::update,
            LocalThemeModeUpdater provides themeModeViewModel::update,
            LocalAssistantHighContrast provides assistantHighContrast,
            LocalAssistantHighContrastUpdater provides assistantAppearanceViewModel::update,
            LocalAssistantChromeBottomSpace provides chromeBottom,
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
                    AnimatedVisibility(
                        visible = barsVisible,
                        enter = fadeIn() + slideInVertically { -it },
                        exit = fadeOut() + slideOutVertically { -it },
                        modifier = Modifier.align(Alignment.TopCenter),
                    ) {
                        FloatingTopSystemBar(
                            onNotificationsClick = {
                                if (hunNotifications.isEmpty()) {
                                    hunNotifications = DemoHunNotifications
                                }
                                hunVisible = !hunVisible
                            },
                            notificationCount = if (hunVisible) 0 else hunNotifications.size,
                            modifier = Modifier.padding(
                                start = FloatingSystemBarInset,
                                end = FloatingSystemBarInset,
                                top = FloatingSystemBarEdgeInset,
                            ),
                        )
                    }
                    if (barsVisible) {
                        HeadsUpNotificationHost(
                            visible = hunVisible,
                            notifications = hunNotifications,
                            onDismiss = { id ->
                                hunNotifications = hunNotifications.filterNot { it.id == id }
                                if (hunNotifications.isEmpty()) hunVisible = false
                            },
                            onDismissAll = {
                                hunNotifications = emptyList()
                                hunVisible = false
                            },
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .padding(
                                    start = FloatingSystemBarInset,
                                    end = FloatingSystemBarInset,
                                    top = FloatingSystemBarEdgeInset + 72.dp,
                                ),
                        )
                    }
                    AnimatedVisibility(
                        visible = barsVisible,
                        enter = fadeIn() + slideInVertically { it },
                        exit = fadeOut() + slideOutVertically { it },
                        modifier = Modifier.align(Alignment.BottomCenter),
                    ) {
                        FloatingBottomSystemBar(
                            climateState = climateState,
                            onClimateEvent = climateViewModel::onEvent,
                            onOpenApps = onOpenApps,
                            onOpenSettings = onOpenSettings,
                            onOpenHome = onOpenHome,
                            onOpenAssistant = onOpenAssistant,
                            modifier = Modifier.padding(
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
