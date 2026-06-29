package com.test.design.presentation.demos.lists

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.test.design.component.components.CustomCard
import com.test.design.component.components.CustomList
import com.test.design.component.components.CustomListItemRow
import com.test.design.component.components.CustomSectionHeader
import com.test.design.component.components.ListItemStyle
import com.test.design.component.theme.NissanSpacing
import com.test.design.presentation.demos.shared.DemoScaffold
import com.test.design.presentation.demos.shared.DemoTipsPanel
import androidx.compose.ui.unit.dp

private data class AppItem(val id: String, val title: String, val subtitle: String, val icon: ImageVector)

@Composable
fun ListsGridsDemo(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val listItems = rememberListItems()
    val gridItems = rememberGridItems()

    DemoScaffold(
        title = "Lists & Grids",
        onBack = onBack,
        modifier = modifier,
        yellowContent = {
            DemoTipsPanel(
                tips = listOf(
                    "Use LazyColumn for long lists",
                    "LazyVerticalGrid for app launchers",
                    "64dp row height for touch safety",
                    "Always provide stable keys",
                ),
            )
        },
    ) {
        CustomSectionHeader(title = "List", subtitle = "CustomList with automotive row height")
        CustomList(
            items = listItems,
            key = { it.id },
            style = ListItemStyle.Standard,
            modifier = Modifier.padding(vertical = NissanSpacing.md),
        ) { item ->
            CustomListItemRow(
                title = item.title,
                subtitle = item.subtitle,
                trailing = {
                    Icon(item.icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                },
            )
        }

        CustomSectionHeader(title = "Grid", subtitle = "Adaptive grid for app shortcuts")
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 160.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = NissanSpacing.md),
            contentPadding = PaddingValues(vertical = NissanSpacing.sm),
            horizontalArrangement = Arrangement.spacedBy(NissanSpacing.md),
            verticalArrangement = Arrangement.spacedBy(NissanSpacing.md),
        ) {
            items(gridItems, key = { it.id }) { item ->
                CustomCard(onClick = {}) {
                    Icon(item.icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(top = NissanSpacing.sm),
                    )
                }
            }
        }
    }
}

@Composable
private fun rememberListItems(): List<AppItem> = listOf(
    AppItem("1", "Navigation", "Home — 12 min", Icons.Default.Map),
    AppItem("2", "Climate", "22°C Auto", Icons.Default.AcUnit),
    AppItem("3", "Phone", "No device connected", Icons.Default.Phone),
    AppItem("4", "Settings", "Vehicle preferences", Icons.Default.Settings),
)

@Composable
private fun rememberGridItems(): List<AppItem> = listOf(
    AppItem("g1", "Maps", "", Icons.Default.Map),
    AppItem("g2", "Music", "", Icons.Default.MusicNote),
    AppItem("g3", "Climate", "", Icons.Default.AcUnit),
    AppItem("g4", "Phone", "", Icons.Default.Phone),
    AppItem("g5", "Settings", "", Icons.Default.Settings),
)
