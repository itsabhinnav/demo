package com.test.design.presentation.settings

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.test.design.core.DrivingUxState
import com.test.design.core.LocalDrivingUxState
import com.test.design.core.driving.LocalDrivingUxUpdater
import com.test.design.core.motion.AppMotionScheme
import com.test.design.core.motion.LocalAppMotionScheme
import com.test.design.core.motion.LocalEffectiveMotionScheme
import com.test.design.core.motion.LocalMotionSchemeUpdater
import com.test.design.core.theme.AppThemeMode
import com.test.design.core.theme.LocalAppThemeMode
import com.test.design.core.theme.LocalThemeModeUpdater
import com.test.design.presentation.assistant.LocalAssistantHighContrast
import com.test.design.presentation.assistant.LocalAssistantHighContrastUpdater
import com.test.design.presentation.ivi.common.DetailSurfaceCard
import com.test.design.presentation.ivi.common.SimulatedBadge
import com.test.design.presentation.ivi.common.WidgetScreenHeader
import com.test.design.presentation.ivi.dashboard.model.DashboardWidget
import com.test.design.presentation.ivi.dashboard.widgetContainerTransform
import com.test.design.presentation.ivi.dashboard.widgetContentSharedElement
import com.test.design.presentation.motion.MotionPhysicsSampleScreen
import com.test.design.theme.AdaptiveLayout
import com.test.design.theme.CarDesignTokens
import com.test.design.theme.carListItemHeight
import com.test.design.theme.carTouchTarget

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.SettingsScreen(
    onBack: () -> Unit,
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier,
) {
    val drivingState = LocalDrivingUxState.current
    val onDrivingStateChange = LocalDrivingUxUpdater.current
    val selectedMotionScheme = LocalAppMotionScheme.current
    val effectiveMotionScheme = LocalEffectiveMotionScheme.current
    val onMotionSchemeChange = LocalMotionSchemeUpdater.current
    val selectedThemeMode = LocalAppThemeMode.current
    val onThemeModeChange = LocalThemeModeUpdater.current
    val assistantHighContrast = LocalAssistantHighContrast.current
    val onAssistantHighContrastChange = LocalAssistantHighContrastUpdater.current
    val motionLocked = drivingState != DrivingUxState.Parked
    var showMotionStudio by remember { mutableStateOf(false) }

    if (showMotionStudio) {
        MotionPhysicsSampleScreen(
            onBack = { showMotionStudio = false },
            modifier = modifier.fillMaxSize(),
        )
        return
    }

    DetailSurfaceCard(
        modifier = widgetContainerTransform(
            widget = DashboardWidget.Settings,
            animatedVisibilityScope = animatedVisibilityScope,
            modifier = modifier
                .fillMaxSize()
                .padding(CarDesignTokens.ContentPadding),
        ),
    ) {
        AdaptiveLayout(modifier = Modifier.fillMaxSize()) { layout ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(CarDesignTokens.TouchTargetSpacing),
            ) {
                WidgetScreenHeader(
                    widget = DashboardWidget.Settings,
                    onBack = onBack,
                    animatedVisibilityScope = animatedVisibilityScope,
                    modifier = Modifier.fillMaxWidth(),
                    trailingContent = { SimulatedBadge() },
                )

                Column(
                    modifier = widgetContentSharedElement(
                        widget = DashboardWidget.Settings,
                        animatedVisibilityScope = animatedVisibilityScope,
                        modifier = Modifier.fillMaxWidth(),
                    ),
                    verticalArrangement = Arrangement.spacedBy(CarDesignTokens.TouchTargetSpacing),
                ) {
                    Text(
                        text = "Driving state",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(CarDesignTokens.TouchTargetSpacing)) {
                        items(DrivingUxState.entries, key = { it.name }) { state ->
                            FilterChip(
                                selected = drivingState == state,
                                onClick = { onDrivingStateChange(state) },
                                modifier = Modifier
                                    .carTouchTarget()
                                    .height(CarDesignTokens.MinTouchTarget),
                                label = { Text(state.name, style = MaterialTheme.typography.labelLarge) },
                            )
                        }
                    }
                    Text(
                        text = when (drivingState) {
                            DrivingUxState.Parked ->
                                "Expressive motion allowed. Cluster shows 0 MPH / Park."
                            DrivingUxState.Driving ->
                                "Motion clamped to Standard. Cluster shows highway speed."
                            DrivingUxState.Restricted ->
                                "Driver distraction policy — glanceables only (see Dual Zone)."
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    HorizontalDivider()
                    Text(
                        text = "Motion scheme",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(CarDesignTokens.TouchTargetSpacing)) {
                        items(AppMotionScheme.entries, key = { it.name }) { scheme ->
                            FilterChip(
                                selected = selectedMotionScheme == scheme,
                                onClick = { onMotionSchemeChange(scheme) },
                                enabled = !motionLocked || scheme == AppMotionScheme.Standard,
                                modifier = Modifier
                                    .carTouchTarget()
                                    .height(CarDesignTokens.MinTouchTarget),
                                label = { Text(scheme.label, style = MaterialTheme.typography.labelLarge) },
                            )
                        }
                    }
                    if (motionLocked) {
                        Text(
                            text = "Driving forces Standard motion for safety.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    ListItem(
                        modifier = Modifier.carListItemHeight(),
                        headlineContent = {
                            Text("Motion Studio", style = MaterialTheme.typography.bodyLarge)
                        },
                        supportingContent = {
                            Text(
                                "Compare Standard / Expressive / Custom springs",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        },
                        trailingContent = {
                            TextButton(onClick = { showMotionStudio = true }) {
                                Text("Open")
                            }
                        },
                    )
                    HorizontalDivider()
                    Text(
                        text = "Appearance",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(CarDesignTokens.TouchTargetSpacing)) {
                        items(AppThemeMode.entries, key = { it.name }) { mode ->
                            FilterChip(
                                selected = selectedThemeMode == mode,
                                onClick = { onThemeModeChange(mode) },
                                modifier = Modifier
                                    .carTouchTarget()
                                    .height(CarDesignTokens.MinTouchTarget),
                                label = { Text(mode.label, style = MaterialTheme.typography.labelLarge) },
                            )
                        }
                    }
                    ListItem(
                        modifier = Modifier.carListItemHeight(),
                        headlineContent = {
                            Text("Assistant high contrast", style = MaterialTheme.typography.bodyLarge)
                        },
                        supportingContent = {
                            Text(
                                "Sunlight-safe eyes · stronger glow",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        },
                        trailingContent = {
                            Switch(
                                checked = assistantHighContrast,
                                onCheckedChange = onAssistantHighContrastChange,
                            )
                        },
                    )
                    HorizontalDivider()
                    Text(
                        text = "Display",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    ListItem(
                        modifier = Modifier.carListItemHeight(),
                        headlineContent = {
                            Text("Theme", style = MaterialTheme.typography.bodyLarge)
                        },
                        supportingContent = {
                            Text(
                                selectedThemeMode.label,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        },
                    )
                    ListItem(
                        modifier = Modifier.carListItemHeight(),
                        headlineContent = {
                            Text("Viewport", style = MaterialTheme.typography.bodyLarge)
                        },
                        supportingContent = {
                            Text(
                                "${layout.width.value.toInt()}dp × ${layout.height.value.toInt()}dp",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        },
                    )
                    ListItem(
                        modifier = Modifier.carListItemHeight(),
                        headlineContent = {
                            Text("Orientation", style = MaterialTheme.typography.bodyLarge)
                        },
                        supportingContent = {
                            Text(
                                layout.orientationLabel,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        },
                    )
                    ListItem(
                        modifier = Modifier.carListItemHeight(),
                        headlineContent = {
                            Text("Width class", style = MaterialTheme.typography.bodyLarge)
                        },
                        supportingContent = {
                            Text(
                                layout.widthClassLabel,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        },
                    )
                    ListItem(
                        modifier = Modifier.carListItemHeight(),
                        headlineContent = {
                            Text("Motion scheme", style = MaterialTheme.typography.bodyLarge)
                        },
                        supportingContent = {
                            Text(
                                text = buildString {
                                    append(effectiveMotionScheme.label)
                                    if (effectiveMotionScheme != selectedMotionScheme) {
                                        append(" (selected ${selectedMotionScheme.label})")
                                    }
                                },
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        },
                    )
                }
            }
        }
    }
}
