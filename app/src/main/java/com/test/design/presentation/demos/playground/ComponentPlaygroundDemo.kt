package com.test.design.presentation.demos.playground

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.DragIndicator
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.automirrored.filled.ViewSidebar
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.test.design.component.components.CustomSectionHeader
import com.test.design.component.components.CustomTopBar
import com.test.design.component.theme.OemBorder
import com.test.design.component.theme.OemOnSurfaceVariant
import com.test.design.component.theme.OemSpacing
import com.test.design.component.theme.OemSurface
import com.test.design.component.theme.OemSurfaceElevated
import com.test.design.component.theme.OemVisuals
import com.test.design.component.theme.oemSurfaceBorder
import kotlin.math.roundToInt

private val PaletteWidth = 300.dp
private val DragGhostSize = 56.dp

@Composable
fun ComponentPlaygroundDemo(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var paletteVisible by remember { mutableStateOf(true) }
    val placedComponents = remember { mutableStateListOf<PlacedComponent>() }
    var nextInstanceId by remember { mutableIntStateOf(0) }
    var selectedCategory by remember { mutableStateOf(PlaygroundCatalog.categories.first()) }

    var activeDrag by remember { mutableStateOf<PlaygroundComponentDefinition?>(null) }
    var dragPosition by remember { mutableStateOf(Offset.Zero) }
    var canvasBounds by remember { mutableStateOf(Rect.Zero) }
    var isCanvasHovered by remember { mutableStateOf(false) }

    val paletteWidth by animateDpAsState(
        targetValue = if (paletteVisible) PaletteWidth else 0.dp,
        label = "paletteWidth",
    )

    fun addComponent(componentId: String) {
        placedComponents.add(PlacedComponent(instanceId = nextInstanceId++, componentId = componentId))
    }

    fun removeComponent(instanceId: Int) {
        placedComponents.removeAll { it.instanceId == instanceId }
    }

    fun handleDrop() {
        val dragged = activeDrag ?: return
        if (isCanvasHovered || !paletteVisible) {
            addComponent(dragged.id)
        }
        activeDrag = null
        isCanvasHovered = false
    }

    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            PlaygroundTopBar(
                onBack = onBack,
                paletteVisible = paletteVisible,
                onTogglePalette = { paletteVisible = !paletteVisible },
                onClear = { placedComponents.clear() },
                componentCount = placedComponents.size,
            )

            Row(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
            ) {
                AnimatedVisibility(
                    visible = paletteVisible,
                    enter = slideInHorizontally { -it } + fadeIn(),
                    exit = slideOutHorizontally { -it } + fadeOut(),
                ) {
                    ComponentPalette(
                        selectedCategory = selectedCategory,
                        onCategorySelected = { selectedCategory = it },
                        onComponentClick = { addComponent(it.id) },
                        onDragStart = { component, rootPosition ->
                            activeDrag = component
                            dragPosition = rootPosition
                            isCanvasHovered = canvasBounds.contains(rootPosition)
                        },
                        onDrag = { rootPosition ->
                            dragPosition = rootPosition
                            isCanvasHovered = canvasBounds.contains(rootPosition)
                        },
                        onDragEnd = { handleDrop() },
                        onDragCancel = {
                            activeDrag = null
                            isCanvasHovered = false
                        },
                        modifier = Modifier
                            .width(paletteWidth)
                            .fillMaxHeight(),
                    )
                }

                PlaygroundCanvas(
                    placedComponents = placedComponents,
                    onRemove = ::removeComponent,
                    isDropTargetActive = activeDrag != null,
                    isHovered = isCanvasHovered,
                    onBoundsChanged = { canvasBounds = it },
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                )
            }
        }

        activeDrag?.let { dragged ->
            DragGhost(
                component = dragged,
                position = dragPosition,
            )
        }
    }
}

@Composable
private fun PlaygroundTopBar(
    onBack: () -> Unit,
    paletteVisible: Boolean,
    onTogglePalette: () -> Unit,
    onClear: () -> Unit,
    componentCount: Int,
) {
    CustomTopBar(
        title = "Component Playground",
        showBack = true,
        onBackClick = onBack,
        tabs = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(OemSpacing.sm),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "$componentCount placed",
                    style = MaterialTheme.typography.bodyMedium,
                    color = OemOnSurfaceVariant,
                )
                PlaygroundToolbarButton(
                    icon = if (paletteVisible) Icons.Default.Fullscreen else Icons.Default.FullscreenExit,
                    contentDescription = if (paletteVisible) "Hide component list" else "Show component list",
                    onClick = onTogglePalette,
                )
                PlaygroundToolbarButton(
                    icon = Icons.Default.DeleteSweep,
                    contentDescription = "Clear canvas",
                    onClick = onClear,
                    enabled = componentCount > 0,
                )
            }
        },
    )
}

@Composable
private fun PlaygroundToolbarButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
    enabled: Boolean = true,
) {
    val shape = OemVisuals.chipShape
    val interactionSource = remember { MutableInteractionSource() }
    Box(
        modifier = Modifier
            .size(OemSpacing.minTouchTarget)
            .clip(shape)
            .background(if (enabled) OemSurface else OemSurface.copy(alpha = 0.5f))
            .oemSurfaceBorder(shape, OemBorder)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = if (enabled) MaterialTheme.colorScheme.onSurface else OemOnSurfaceVariant,
            modifier = Modifier.size(OemSpacing.lg),
        )
    }
}

@Composable
private fun ComponentPalette(
    selectedCategory: String,
    onCategorySelected: (String) -> Unit,
    onComponentClick: (PlaygroundComponentDefinition) -> Unit,
    onDragStart: (PlaygroundComponentDefinition, Offset) -> Unit,
    onDrag: (Offset) -> Unit,
    onDragEnd: () -> Unit,
    onDragCancel: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        color = OemSurface,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(OemSpacing.md),
        ) {
            CustomSectionHeader(
                title = "Components",
                subtitle = "Tap or long-press & drag to canvas",
            )

            CategoryTabs(
                categories = PlaygroundCatalog.categories,
                selected = selectedCategory,
                onSelected = onCategorySelected,
                modifier = Modifier.padding(vertical = OemSpacing.sm),
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(OemSpacing.sm),
            ) {
                items(
                    items = PlaygroundCatalog.byCategory(selectedCategory),
                    key = { it.id },
                ) { component ->
                    PaletteItem(
                        component = component,
                        onClick = { onComponentClick(component) },
                        onDragStart = { rootPosition -> onDragStart(component, rootPosition) },
                        onDrag = onDrag,
                        onDragEnd = onDragEnd,
                        onDragCancel = onDragCancel,
                    )
                }
            }
        }
    }
}

@Composable
private fun CategoryTabs(
    categories: List<String>,
    selected: String,
    onSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(OemSpacing.xs),
    ) {
        categories.forEach { category ->
            val isSelected = category == selected
            val shape = OemVisuals.chipShape
            Box(
                modifier = Modifier
                    .clip(shape)
                    .background(if (isSelected) OemSurfaceElevated else OemSurface)
                    .oemSurfaceBorder(shape, if (isSelected) MaterialTheme.colorScheme.onSurface else OemBorder)
                    .clickable { onSelected(category) }
                    .padding(horizontal = OemSpacing.sm, vertical = OemSpacing.xs),
            ) {
                Text(
                    text = category,
                    style = MaterialTheme.typography.labelLarge,
                    color = if (isSelected) MaterialTheme.colorScheme.onSurface else OemOnSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun PaletteItem(
    component: PlaygroundComponentDefinition,
    onClick: () -> Unit,
    onDragStart: (Offset) -> Unit,
    onDrag: (Offset) -> Unit,
    onDragEnd: () -> Unit,
    onDragCancel: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = OemVisuals.cardShape
    var itemRootPosition by remember { mutableStateOf(Offset.Zero) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .onGloballyPositioned { coordinates ->
                itemRootPosition = coordinates.positionInRoot()
            }
            .clip(shape)
            .background(OemSurfaceElevated)
            .oemSurfaceBorder(shape)
            .clickable(onClick = onClick)
            .pointerInput(component.id) {
                detectDragGesturesAfterLongPress(
                    onDragStart = { offset ->
                        onDragStart(itemRootPosition + offset)
                    },
                    onDragEnd = { onDragEnd() },
                    onDragCancel = { onDragCancel() },
                    onDrag = { change, _ ->
                        change.consume()
                        onDrag(itemRootPosition + change.position)
                    },
                )
            }
            .padding(OemSpacing.sm),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(OemSpacing.sm),
    ) {
        Icon(
            imageVector = Icons.Default.DragIndicator,
            contentDescription = null,
            tint = OemOnSurfaceVariant,
            modifier = Modifier.size(OemSpacing.md),
        )
        Icon(
            imageVector = component.icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.size(OemSpacing.lg),
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = component.name,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = component.category,
                style = MaterialTheme.typography.bodySmall,
                color = OemOnSurfaceVariant,
            )
        }
    }
}

@Composable
private fun PlaygroundCanvas(
    placedComponents: List<PlacedComponent>,
    onRemove: (Int) -> Unit,
    isDropTargetActive: Boolean,
    isHovered: Boolean,
    onBoundsChanged: (Rect) -> Unit,
    modifier: Modifier = Modifier,
) {
    val borderColor = when {
        isHovered -> MaterialTheme.colorScheme.onSurface
        isDropTargetActive -> OemOnSurfaceVariant
        else -> OemBorder
    }
    val borderWidth = if (isHovered) 2.dp else 1.dp

    Surface(
        modifier = modifier
            .onGloballyPositioned { coordinates ->
                val pos = coordinates.positionInRoot()
                val size = coordinates.size
                onBoundsChanged(
                    Rect(
                        left = pos.x,
                        top = pos.y,
                        right = pos.x + size.width,
                        bottom = pos.y + size.height,
                    ),
                )
            },
        color = MaterialTheme.colorScheme.background,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(OemSpacing.md)
                .clip(OemVisuals.cardShape)
                .border(borderWidth, borderColor, OemVisuals.cardShape)
                .padding(OemSpacing.md),
        ) {
            if (placedComponents.isEmpty()) {
                EmptyCanvasHint(isDropTargetActive = isDropTargetActive)
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(OemSpacing.md),
                ) {
                    placedComponents.forEach { placed ->
                        PlacedComponentItem(
                            placed = placed,
                            onRemove = { onRemove(placed.instanceId) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun EmptyCanvasHint(
    isDropTargetActive: Boolean,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(OemSpacing.sm),
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ViewSidebar,
                contentDescription = null,
                tint = OemOnSurfaceVariant,
                modifier = Modifier.size(OemSpacing.xl),
            )
            Text(
                text = if (isDropTargetActive) "Drop here to add component" else "Build your screen here",
                style = MaterialTheme.typography.titleMedium,
                color = if (isDropTargetActive) MaterialTheme.colorScheme.onSurface else OemOnSurfaceVariant,
            )
            Text(
                text = "Drag components from the list or tap to add instantly",
                style = MaterialTheme.typography.bodyMedium,
                color = OemOnSurfaceVariant,
            )
        }
    }
}

@Composable
private fun PlacedComponentItem(
    placed: PlacedComponent,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val definition = PlaygroundCatalog.findById(placed.componentId)
    val shape = OemVisuals.cardShape

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
            .background(OemSurfaceElevated)
            .oemSurfaceBorder(shape)
            .padding(OemSpacing.md),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = OemSpacing.sm),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = definition?.name ?: placed.componentId,
                style = MaterialTheme.typography.labelLarge,
                color = OemOnSurfaceVariant,
            )
            Box(
                modifier = Modifier
                    .size(OemSpacing.minTouchTarget)
                    .clip(RoundedCornerShape(8.dp))
                    .clickable(onClick = onRemove),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Remove ${definition?.name}",
                    tint = OemOnSurfaceVariant,
                    modifier = Modifier.size(OemSpacing.md),
                )
            }
        }
        PlaygroundComponentRenderer(
            componentId = placed.componentId,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun DragGhost(
    component: PlaygroundComponentDefinition,
    position: Offset,
) {
    val ghostSizePx = with(androidx.compose.ui.platform.LocalDensity.current) { DragGhostSize.toPx() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .zIndex(10f),
    ) {
        Box(
            modifier = Modifier
                .offset {
                    IntOffset(
                        (position.x - ghostSizePx / 2).roundToInt(),
                        (position.y - ghostSizePx / 2).roundToInt(),
                    )
                }
                .size(DragGhostSize)
                .graphicsLayer {
                    alpha = 0.85f
                    shadowElevation = 8f
                }
                .clip(OemVisuals.cardShape)
                .background(OemSurfaceElevated)
                .border(2.dp, MaterialTheme.colorScheme.onSurface, OemVisuals.cardShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = component.icon,
                contentDescription = component.name,
                tint = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.size(OemSpacing.lg),
            )
        }
    }
}
