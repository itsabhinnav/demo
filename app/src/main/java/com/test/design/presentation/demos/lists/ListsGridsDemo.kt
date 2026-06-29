package com.test.design.presentation.demos.lists

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import com.test.design.component.theme.OemSpacing
import com.test.design.presentation.demos.shared.DemoScaffold
import com.test.design.presentation.demos.shared.DemoTipsPanel

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
            scrollable = false,
            modifier = Modifier.padding(vertical = OemSpacing.md),
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
        StaticAppGrid(
            items = gridItems,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = OemSpacing.md),
        )
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
private fun StaticAppGrid(
    items: List<AppItem>,
    modifier: Modifier = Modifier,
    columnCount: Int = 3,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(OemSpacing.md),
    ) {
        items.chunked(columnCount).forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(OemSpacing.md),
            ) {
                rowItems.forEach { item ->
                    CustomCard(
                        onClick = {},
                        modifier = Modifier.weight(1f),
                    ) {
                        Icon(item.icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Text(
                            text = item.title,
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.padding(top = OemSpacing.sm),
                        )
                    }
                }
                repeat(columnCount - rowItems.size) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun rememberGridItems(): List<AppItem> = listOf(
    AppItem("g1", "Maps", "", Icons.Default.Map),
    AppItem("g2", "Music", "", Icons.Default.MusicNote),
    AppItem("g3", "Climate", "", Icons.Default.AcUnit),
    AppItem("g4", "Phone", "", Icons.Default.Phone),
    AppItem("g5", "Settings", "", Icons.Default.Settings),
)
