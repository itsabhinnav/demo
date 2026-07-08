package com.test.design.presentation.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Widgets
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.test.design.core.DrivingUxState
import com.test.design.core.LocalDrivingUxState
import com.test.design.core.driving.LocalDrivingUxUpdater
import com.test.design.core.motion.AppMotionScheme
import com.test.design.core.motion.LocalAppMotionScheme
import com.test.design.core.motion.LocalEffectiveMotionScheme
import com.test.design.core.motion.LocalMotionSchemeUpdater
import com.test.design.theme.CarDesignTokens
import com.test.design.theme.carListItemHeight
import com.test.design.theme.carTouchTarget

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToIviDemo: () -> Unit,
    onNavigateToMaterialComponents: () -> Unit,
    onNavigateToCustomizedMaterialComponents: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        containerColor = Color.Transparent,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "AAOS Playground",
                        style = MaterialTheme.typography.titleLarge,
                    )
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
                        .padding(CarDesignTokens.ContentPadding),
                    verticalArrangement = Arrangement.spacedBy(CarDesignTokens.TouchTargetSpacing),
                ) {
                    Text(
                        text = "Welcome",
                        style = MaterialTheme.typography.headlineMedium,
                    )
                    HomeEntryCard(
                        title = "IVI Expressive Demo",
                        description = "Dashboard, climate, and media with M3 motion",
                        icon = Icons.Default.Dashboard,
                        onClick = onNavigateToIviDemo,
                    )
                    HomeEntryCard(
                        title = "Material Components",
                        description = "Browse buttons, chips, cards, sliders, and more",
                        icon = Icons.Default.Widgets,
                        onClick = onNavigateToMaterialComponents,
                    )
                    HomeEntryCard(
                        title = "Customized Material",
                        description = "Production OEM brand system on Material foundations",
                        icon = Icons.Default.Palette,
                        onClick = onNavigateToCustomizedMaterialComponents,
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
private fun HomeEntryCard(
    title: String,
    description: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
        ),
    ) {
        ListItem(
            modifier = Modifier.carListItemHeight(),
            headlineContent = {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            },
            supportingContent = {
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f),
                )
            },
            leadingContent = {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(CarDesignTokens.PrimaryIcon),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            },
            trailingContent = {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    modifier = Modifier.size(CarDesignTokens.PrimaryIcon),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            },
        )
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
    val selectedMotionScheme = LocalAppMotionScheme.current
    val effectiveMotionScheme = LocalEffectiveMotionScheme.current
    val onMotionSchemeChange = LocalMotionSchemeUpdater.current
    val motionLocked = drivingState != DrivingUxState.Parked

    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.surfaceContainerLow,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(CarDesignTokens.ContentPadding),
            verticalArrangement = Arrangement.spacedBy(CarDesignTokens.TouchTargetSpacing),
        ) {
            Text(text = "Driving state", style = MaterialTheme.typography.titleMedium)
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
            HorizontalDivider()
            Text(text = "Motion scheme", style = MaterialTheme.typography.titleMedium)
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
            HorizontalDivider()
            Text(text = "Display", style = MaterialTheme.typography.titleMedium)
            ListItem(
                modifier = Modifier.carListItemHeight(),
                headlineContent = { Text("Viewport", style = MaterialTheme.typography.bodyLarge) },
                supportingContent = {
                    Text("${widthDp}dp × ${heightDp}dp", style = MaterialTheme.typography.bodyMedium)
                },
            )
            ListItem(
                modifier = Modifier.carListItemHeight(),
                headlineContent = { Text("Orientation", style = MaterialTheme.typography.bodyLarge) },
                supportingContent = { Text("Landscape", style = MaterialTheme.typography.bodyMedium) },
            )
            ListItem(
                modifier = Modifier.carListItemHeight(),
                headlineContent = { Text("Motion scheme", style = MaterialTheme.typography.bodyLarge) },
                supportingContent = {
                    Text(
                        text = buildString {
                            append(effectiveMotionScheme.label)
                            if (effectiveMotionScheme != selectedMotionScheme) {
                                append(" (selected ${selectedMotionScheme.label})")
                            }
                        },
                        style = MaterialTheme.typography.bodyMedium,
                    )
                },
            )
        }
    }
}
