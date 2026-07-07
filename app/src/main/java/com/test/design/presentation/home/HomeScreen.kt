package com.test.design.presentation.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SearchOff
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.test.design.component.core.DrivingUxState
import com.test.design.component.core.RestrictedComponentPolicy
import com.test.design.component.core.currentDrivingUxState
import com.test.design.component.core.currentTouchTarget
import com.test.design.core.driving.LocalDrivingUxUpdater
import com.test.design.domain.model.DemoCategory
import com.test.design.domain.model.FeatureDemo
import com.test.design.presentation.home.mapper.mapFeatureIcon
import com.test.design.presentation.home.mapper.mapToSystemInfoUiState
import com.test.design.template.adaptive.rememberAutomotiveWindowInfo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    state: HomeState,
    onFeatureClick: (FeatureDemo) -> Unit,
    onCategorySelected: (DemoCategory) -> Unit,
    onSearchQueryChanged: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = state.title,
                            style = MaterialTheme.typography.titleLarge,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Text(
                            text = state.subtitle,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
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
            val windowInfo = rememberAutomotiveWindowInfo(maxWidth, maxHeight)
            Row(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .weight(0.68f)
                    .fillMaxHeight(),
            ) {
                HomeSearchAndFilters(
                    searchQuery = state.searchQuery,
                    categories = state.categories,
                    selectedCategory = state.selectedCategory,
                    onSearchQueryChanged = onSearchQueryChanged,
                    onCategorySelected = onCategorySelected,
                )
                HomeDemoGrid(
                    features = state.filteredFeatures,
                    onFeatureClick = onFeatureClick,
                    modifier = Modifier.weight(1f),
                )
            }
            VerticalDivider()
            HomeInfoPanel(
                state = state,
                windowInfo = windowInfo,
                modifier = Modifier
                    .weight(0.32f)
                    .fillMaxHeight(),
            )
            }
        }
    }
}

@Composable
private fun HomeSearchAndFilters(
    searchQuery: String,
    categories: List<DemoCategory>,
    selectedCategory: DemoCategory,
    onSearchQueryChanged: (String) -> Unit,
    onCategorySelected: (DemoCategory) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchQueryChanged,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("Search demos") },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = "Search")
            },
            singleLine = true,
        )
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            items(categories, key = { it.name }) { category ->
                FilterChip(
                    selected = category == selectedCategory,
                    onClick = { onCategorySelected(category) },
                    label = { Text(category.label) },
                )
            }
        }
    }
}

@Composable
private fun HomeDemoGrid(
    features: List<FeatureDemo>,
    onFeatureClick: (FeatureDemo) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (features.isEmpty()) {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Icon(
                imageVector = Icons.Default.SearchOff,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(12.dp))
            Text(
                text = "No demos found",
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                text = "Try another category or search term.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        return
    }

    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 380.dp),
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        items(features, key = { it.id }) { feature ->
            ElevatedCard(
                onClick = { onFeatureClick(feature) },
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 1.dp),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.Top,
                ) {
                    Surface(
                        shape = MaterialTheme.shapes.medium,
                        color = MaterialTheme.colorScheme.secondaryContainer,
                        modifier = Modifier.size(56.dp),
                    ) {
                        Icon(
                            imageVector = mapFeatureIcon(feature),
                            contentDescription = null,
                            modifier = Modifier.padding(14.dp),
                            tint = MaterialTheme.colorScheme.onSecondaryContainer,
                        )
                    }
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 16.dp),
                    ) {
                        Text(
                            text = feature.tagline,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Text(
                            text = feature.title,
                            style = MaterialTheme.typography.titleLarge,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(top = 4.dp),
                        )
                        Text(
                            text = feature.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(top = 4.dp),
                        )
                    }
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .size(24.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun HomeInfoPanel(
    state: HomeState,
    windowInfo: com.test.design.template.adaptive.AutomotiveWindowInfo,
    modifier: Modifier = Modifier,
) {
    val drivingState = currentDrivingUxState()
    val onDrivingStateChange = LocalDrivingUxUpdater.current
    val density = LocalDensity.current.density
    val systemInfo = mapToSystemInfoUiState(windowInfo, density)

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
            Text(
                text = "Driving state",
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                text = "Simulate AAOS UXR restrictions across every screen.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(DrivingUxState.entries, key = { it.name }) { uxState ->
                    FilterChip(
                        selected = drivingState == uxState,
                        onClick = { onDrivingStateChange(uxState) },
                        label = { Text(uxState.name) },
                    )
                }
            }
            Text(
                text = "Touch ${currentTouchTarget().value.toInt()}dp · " +
                    "Anim ${RestrictedComponentPolicy.maxAnimationDurationMs(drivingState)}ms",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            HorizontalDivider()
            Text(
                text = "Playground",
                style = MaterialTheme.typography.titleMedium,
            )
            ListItem(
                headlineContent = { Text("Demos") },
                supportingContent = { Text("${state.features.size} available") },
            )
            ListItem(
                headlineContent = { Text("Category") },
                supportingContent = { Text(state.selectedCategory.label) },
            )
            HorizontalDivider()
            Text(
                text = "Display",
                style = MaterialTheme.typography.titleMedium,
            )
            ListItem(
                headlineContent = { Text("Profile") },
                supportingContent = { Text(systemInfo.displayLabel) },
            )
            ListItem(
                headlineContent = { Text("Resolution") },
                supportingContent = { Text("${systemInfo.widthLabel} × ${systemInfo.heightLabel}") },
            )
            ListItem(
                headlineContent = { Text("Layout split") },
                supportingContent = { Text(systemInfo.layoutLabel) },
            )
            ListItem(
                headlineContent = { Text("Blue zone") },
                supportingContent = { Text(systemInfo.blueZoneLabel) },
            )
        }
    }
}
