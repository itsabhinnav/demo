package com.test.design.presentation.ivi.climate

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.test.design.presentation.ivi.climate.components.AnimatedTemperatureCounter
import com.test.design.presentation.ivi.climate.components.MorphingAirflowSegmentedButton
import com.test.design.presentation.ivi.dashboard.model.DashboardWidget
import com.test.design.theme.CarDesignTokens
import com.test.design.theme.ClimateDialShape
import com.test.design.theme.WidgetCardShape
import com.test.design.theme.carTouchTarget
import com.test.design.theme.climateColorScheme
import com.test.design.theme.temperatureToFraction

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.ClimateControlScreen(
    uiState: ClimateUiState,
    onEvent: (ClimateEvent) -> Unit,
    onBack: () -> Unit,
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier,
) {
    val temperatureFraction = temperatureToFraction(
        celsius = uiState.temperatureCelsius,
        min = uiState.minTemperature,
        max = uiState.maxTemperature,
    )
    val dynamicScheme = climateColorScheme(temperatureFraction)

    MaterialTheme(
        colorScheme = dynamicScheme,
        typography = MaterialTheme.typography,
        shapes = MaterialTheme.shapes,
        motionScheme = MaterialTheme.motionScheme,
    ) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .sharedBounds(
                    rememberSharedContentState(key = DashboardWidget.Climate.sharedElementKey),
                    animatedVisibilityScope = animatedVisibilityScope,
                    resizeMode = SharedTransitionScope.ResizeMode.RemeasureToBounds,
                    clipInOverlayDuringTransition = OverlayClip(WidgetCardShape),
                )
                .background(MaterialTheme.colorScheme.background)
                .padding(CarDesignTokens.ContentPadding),
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(CarDesignTokens.SectionSpacing),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.carTouchTarget(),
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back to dashboard",
                            modifier = Modifier.size(CarDesignTokens.PrimaryIcon),
                        )
                    }
                    Text(
                        text = "Climate",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                    Switch(
                        checked = uiState.isAcEnabled,
                        onCheckedChange = { onEvent(ClimateEvent.ToggleAc) },
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    TemperatureAdjustButton(
                        icon = Icons.Default.Remove,
                        contentDescription = "Decrease temperature",
                        onClick = { onEvent(ClimateEvent.DecreaseTemperature) },
                    )

                    Box(
                        modifier = Modifier
                            .size(280.dp)
                            .clip(ClimateDialShape)
                            .background(
                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.6f),
                            ),
                        contentAlignment = Alignment.Center,
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            AnimatedTemperatureCounter(temperature = uiState.temperatureCelsius)
                            Text(
                                text = if (uiState.isAcEnabled) "A/C On" else "A/C Off",
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.onPrimaryContainer,
                            )
                        }
                    }

                    TemperatureAdjustButton(
                        icon = Icons.Default.Add,
                        contentDescription = "Increase temperature",
                        onClick = { onEvent(ClimateEvent.IncreaseTemperature) },
                    )
                }

                Column(verticalArrangement = Arrangement.spacedBy(CarDesignTokens.TouchTargetSpacing)) {
                    Text(
                        text = "Airflow",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                    MorphingAirflowSegmentedButton(
                        selectedMode = uiState.airflowMode,
                        onModeSelected = { onEvent(ClimateEvent.SelectAirflow(it)) },
                    )
                }
            }
        }
    }
}

@Composable
private fun TemperatureAdjustButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
) {
    FilledIconButton(
        onClick = onClick,
        modifier = Modifier
            .size(CarDesignTokens.MinTouchTarget)
            .clip(CircleShape)
            .carTouchTarget(),
        colors = IconButtonDefaults.filledIconButtonColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
        ),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            modifier = Modifier.size(CarDesignTokens.PrimaryIcon),
        )
    }
}
