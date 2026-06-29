package com.test.design.component.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import com.test.design.component.preview.AutomotivePreviews
import com.test.design.component.theme.OemBorder
import com.test.design.component.theme.OemOnSurface
import com.test.design.component.theme.OemOnSurfaceVariant
import com.test.design.component.theme.OemSpacing
import com.test.design.component.theme.OemSurfaceElevated
import com.test.design.component.theme.OemTheme
import com.test.design.component.theme.OemVisuals
import com.test.design.component.theme.oemSurfaceBorder

enum class ListItemStyle {
    Standard,
    Compact,
}

data class CustomListItem(
    val id: String,
    val title: String,
    val subtitle: String? = null,
)

@Composable
fun <T> CustomList(
    items: List<T>,
    modifier: Modifier = Modifier,
    key: ((T) -> Any)? = null,
    style: ListItemStyle = ListItemStyle.Standard,
    scrollable: Boolean = true,
    onItemClick: ((T) -> Unit)? = null,
    itemContent: @Composable (T) -> Unit,
) {
    val renderItem: @Composable (T) -> Unit = { item ->
        CustomListItemContainer(
            style = style,
            onClick = onItemClick?.let { { it(item) } },
        ) {
            itemContent(item)
        }
    }

    if (scrollable) {
        LazyColumn(
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(OemSpacing.xs),
        ) {
            if (key != null) {
                items(items, key = key) { item -> renderItem(item) }
            } else {
                items(items) { item -> renderItem(item) }
            }
        }
    } else {
        Column(
            modifier = modifier,
            verticalArrangement = Arrangement.spacedBy(OemSpacing.xs),
        ) {
            items.forEach { item -> renderItem(item) }
        }
    }
}

@Composable
fun CustomListItemRow(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    trailing: @Composable (() -> Unit)? = null,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        trailing?.invoke()
    }
}

@Composable
private fun CustomListItemContainer(
    style: ListItemStyle,
    onClick: (() -> Unit)?,
    content: @Composable () -> Unit,
) {
    val height = when (style) {
        ListItemStyle.Standard -> OemSpacing.listItemHeight
        ListItemStyle.Compact -> OemSpacing.minTouchTarget
    }
    val shape = OemVisuals.cardShape
    val interactionSource = remember { MutableInteractionSource() }
    val clickableModifier = if (onClick != null) {
        Modifier.clickable(
            interactionSource = interactionSource,
            indication = null,
            onClick = onClick,
        )
    } else {
        Modifier
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = OemSpacing.xs)
            .clip(shape)
            .background(OemSurfaceElevated)
            .oemSurfaceBorder(shape, OemBorder)
            .then(clickableModifier),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
                .padding(horizontal = OemSpacing.md, vertical = OemSpacing.sm),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            content()
        }
    }
}

@AutomotivePreviews
@Composable
private fun CustomListPreview() {
    val items = listOf(
        CustomListItem("1", "Compose Basics", "Introduction to Jetpack Compose"),
        CustomListItem("2", "Design System", "Oem tokens and theming"),
    )
    OemTheme {
        CustomList(
            items = items,
            key = { it.id },
            onItemClick = {},
        ) { item ->
            CustomListItemRow(title = item.title, subtitle = item.subtitle)
        }
    }
}
