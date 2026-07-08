package com.test.design.presentation.ivi.vehicle

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.test.design.presentation.ivi.common.DetailSurfaceCard
import com.test.design.presentation.ivi.common.WidgetScreenHeader
import com.test.design.presentation.ivi.dashboard.model.DashboardWidget
import com.test.design.presentation.ivi.dashboard.widgetContainerTransform
import com.test.design.presentation.ivi.vehicle.components.AnimatedBatteryGauge
import com.test.design.presentation.ivi.vehicle.components.DriveModeSelector
import com.test.design.presentation.ivi.vehicle.components.TirePressureGrid
import com.test.design.theme.CarDesignTokens

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.VehicleScreen(
    uiState: VehicleUiState,
    onEvent: (VehicleEvent) -> Unit,
    onBack: () -> Unit,
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = widgetContainerTransform(
            widget = DashboardWidget.Vehicle,
            animatedVisibilityScope = animatedVisibilityScope,
            modifier = modifier.fillMaxSize(),
        )
            .background(MaterialTheme.colorScheme.background)
            .padding(CarDesignTokens.ContentPadding),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(CarDesignTokens.SectionSpacing),
        ) {
            WidgetScreenHeader(
                widget = DashboardWidget.Vehicle,
                onBack = onBack,
                animatedVisibilityScope = animatedVisibilityScope,
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(CarDesignTokens.SectionSpacing),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                AnimatedBatteryGauge(
                    percent = uiState.batteryPercent,
                    rangeMiles = uiState.rangeMiles,
                    onClick = { onEvent(VehicleEvent.CycleBatteryDemo) },
                    modifier = Modifier.weight(0.4f),
                )
                Column(
                    modifier = Modifier.weight(0.6f),
                    verticalArrangement = Arrangement.spacedBy(CarDesignTokens.TouchTargetSpacing),
                ) {
                    DetailSurfaceCard(modifier = Modifier.fillMaxWidth()) {
                        Text("Drive mode", style = MaterialTheme.typography.titleMedium)
                        DriveModeSelector(
                            selected = uiState.driveMode,
                            onSelected = { onEvent(VehicleEvent.SelectDriveMode(it)) },
                            modifier = Modifier.padding(top = 12.dp),
                        )
                    }
                    DetailSurfaceCard(modifier = Modifier.fillMaxWidth()) {
                        Text("Trip efficiency", style = MaterialTheme.typography.titleMedium)
                        Text(
                            text = "${uiState.efficiencyMpkWh} mi/kWh",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.primary,
                        )
                        Text(
                            text = "Odometer ${uiState.odometerMiles} mi · ${uiState.tripEnergyKwh} kWh used",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }

            DetailSurfaceCard(modifier = Modifier.fillMaxWidth()) {
                Text("Tire pressure", style = MaterialTheme.typography.titleMedium)
                TirePressureGrid(
                    tires = uiState.tirePressures,
                    modifier = Modifier.padding(top = 12.dp),
                )
            }
        }
    }
}
