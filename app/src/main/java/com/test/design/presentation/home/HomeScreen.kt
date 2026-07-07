package com.test.design.presentation.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.test.design.core.DrivingUxState
import com.test.design.core.LocalDrivingUxState
import com.test.design.core.driving.LocalDrivingUxUpdater

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Oem AAOS Playground",
                            style = MaterialTheme.typography.titleLarge,
                        )
                        Text(
                            text = "Material 3 expressive motion for landscape displays",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
            )
        },
    ) { padding ->
        BoxWithConstraints(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
        ) {
            val widthDp = maxWidth.value.toInt()
            val heightDp = maxHeight.value.toInt()
            Row(modifier = Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .weight(0.68f)
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                ) {
                    Text(
                        text = "Welcome",
                        style = MaterialTheme.typography.headlineMedium,
                    )
                    Text(
                        text = "This app is a clean Material 3 shell for AAOS landscape (1920×720). " +
                            "Custom OEM components, template zones, and demo galleries have been removed.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        text = "Use the side panel to simulate driving restrictions. " +
                            "MotionScheme switches between expressive (parked) and standard (driving).",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                VerticalDivider()
                HomeSidePanel(
                    widthDp = widthDp,
                    heightDp = heightDp,
                    modifier = Modifier
                        .weight(0.32f)
                        .fillMaxSize(),
                )
            }
        }
    }
}

@Composable
private fun HomeSidePanel(
    widthDp: Int,
    heightDp: Int,
    modifier: Modifier = Modifier,
) {
    val drivingState = LocalDrivingUxState.current
    val onDrivingStateChange = LocalDrivingUxUpdater.current

    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.surfaceContainerLow,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(text = "Driving state", style = MaterialTheme.typography.titleMedium)
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(DrivingUxState.entries, key = { it.name }) { state ->
                    FilterChip(
                        selected = drivingState == state,
                        onClick = { onDrivingStateChange(state) },
                        label = { Text(state.name) },
                    )
                }
            }
            HorizontalDivider()
            Text(text = "Display", style = MaterialTheme.typography.titleMedium)
            ListItem(
                headlineContent = { Text("Viewport") },
                supportingContent = { Text("${widthDp}dp × ${heightDp}dp") },
            )
            ListItem(
                headlineContent = { Text("Orientation") },
                supportingContent = { Text("Landscape") },
            )
            ListItem(
                headlineContent = { Text("Motion scheme") },
                supportingContent = {
                    Text(
                        if (drivingState == DrivingUxState.Parked) "Expressive" else "Standard",
                    )
                },
            )
        }
    }
}
