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
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ViewSidebar
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.DragIndicator
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.test.design.component.components.ButtonStyle
import com.test.design.component.components.CustomButton
import com.test.design.component.components.CustomSectionHeader
import com.test.design.component.components.CustomSlider
import com.test.design.component.components.CustomSwitch
import com.test.design.component.components.CustomTopBar
import com.test.design.component.components.CustomTextField
import com.test.design.component.theme.OemBorder
import com.test.design.component.theme.OemOnSurfaceVariant
import com.test.design.component.theme.OemSpacing
import com.test.design.component.theme.OemSurface
import com.test.design.component.theme.OemSurfaceElevated
import com.test.design.component.theme.OemVisuals
import com.test.design.component.theme.oemSurfaceBorder
import kotlin.math.roundToInt

private val PaletteWidth = 280.dp
private val ConfigBarWidth = 260.dp
private val DragGhostSize = 56.dp
private val ResizeHandleWidth = 12.dp
private val MinWidthFraction = 0.12f
private val MaxWidthFraction = 1f

@Composable
fun ComponentPlaygroundDemo(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var paletteVisible by remember { mutableStateOf(true) }
    val placedComponents = remember { mutableStateListOf<PlacedComponent>() }
    var nextInstanceId by remember { mutableIntStateOf(0) }
    var selectedCategory by remember { mutableStateOf(PlaygroundCatalog.categories.first()) }
    var selectedInstanceId by remember { mutableStateOf<Int?>(null) }

    var activeDrag by remember { mutableStateOf<PlaygroundComponentDefinition?>(null) }
    var dragPosition by remember { mutableStateOf(Offset.Zero) }
    var canvasBounds by remember { mutableStateOf(Rect.Zero) }
    var isCanvasHovered by remember { mutableStateOf(false) }

    val isPreviewMode = !paletteVisible
    val paletteWidth by animateDpAsState(
        targetValue = if (paletteVisible) PaletteWidth else 0.dp,
        label = "paletteWidth",
    )

    fun updatePlaced(instanceId: Int, transform: (PlacedComponent) -> PlacedComponent) {
        val index = placedComponents.indexOfFirst { it.instanceId == instanceId }
        if (index >= 0) placedComponents[index] = transform(placedComponents[index])
    }

    fun addComponent(componentId: String, canvasLocalOffset: Offset? = null) {
        val stagger = placedComponents.size * 40f
        val x = canvasLocalOffset?.x?.coerceAtLeast(0f) ?: (32f + (placedComponents.size % 4) * 20f)
        val y = canvasLocalOffset?.y?.coerceAtLeast(0f) ?: (32f + stagger)
        val placed = PlacedComponent(
            instanceId = nextInstanceId++,
            componentId = componentId,
            xDp = x,
            yDp = y,
            widthFraction = defaultWidthFraction(componentId),
            textContent = if (PlaygroundCatalog.isTextComponent(componentId)) {
                PlaygroundCatalog.defaultTextContent(componentId)
            } else {
                null
            },
        )
        placedComponents.add(placed)
        selectedInstanceId = placed.instanceId
    }

    fun removeComponent(instanceId: Int) {
        placedComponents.removeAll { it.instanceId == instanceId }
        if (selectedInstanceId == instanceId) selectedInstanceId = null
    }

    fun handleDrop() {
        val dragged = activeDrag ?: return
        if (isCanvasHovered || isPreviewMode) {
            val local = Offset(
                x = dragPosition.x - canvasBounds.left,
                y = dragPosition.y - canvasBounds.top,
            )
            addComponent(dragged.id, local)
        }
        activeDrag = null
        isCanvasHovered = false
    }

    val selectedPlaced = placedComponents.find { it.instanceId == selectedInstanceId }

    Box(modifier = modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            AnimatedVisibility(visible = !isPreviewMode) {
                PlaygroundTopBar(
                    onBack = onBack,
                    onTogglePreview = { paletteVisible = false },
                    onClear = {
                        placedComponents.clear()
                        selectedInstanceId = null
                    },
                    componentCount = placedComponents.size,
                )
            }

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
                    selectedInstanceId = selectedInstanceId,
                    isDropTargetActive = activeDrag != null,
                    isHovered = isCanvasHovered,
                    isPreviewMode = isPreviewMode,
                    onBoundsChanged = { canvasBounds = it },
                    onSelect = { selectedInstanceId = it },
                    onDeselect = { selectedInstanceId = null },
                    onMove = { instanceId, deltaXDp, deltaYDp ->
                        updatePlaced(instanceId) { placed ->
                            placed.copy(
                                xDp = (placed.xDp + deltaXDp).coerceAtLeast(0f),
                                yDp = (placed.yDp + deltaYDp).coerceAtLeast(0f),
                            )
                        }
                    },
                    onResize = { instanceId, deltaFraction ->
                        updatePlaced(instanceId) { placed ->
                            val current = placed.widthFraction ?: 0.35f
                            placed.copy(
                                widthFraction = (current + deltaFraction)
                                    .coerceIn(MinWidthFraction, MaxWidthFraction),
                            )
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight(),
                )

                AnimatedVisibility(
                    visible = selectedPlaced != null && !isPreviewMode,
                    enter = slideInHorizontally { it } + fadeIn(),
                    exit = slideOutHorizontally { it } + fadeOut(),
                ) {
                    selectedPlaced?.let { placed ->
                        ComponentConfigBar(
                            placed = placed,
                            onWidthFractionChange = { fraction ->
                                updatePlaced(placed.instanceId) { it.copy(widthFraction = fraction) }
                            },
                            onWrapContentToggle = { wrap ->
                                updatePlaced(placed.instanceId) {
                                    it.copy(widthFraction = if (wrap) null else 0.4f)
                                }
                            },
                            onTextContentChange = { content ->
                                updatePlaced(placed.instanceId) { it.copy(textContent = content) }
                            },
                            onTextStyleChange = { styleId ->
                                updatePlaced(placed.instanceId) { current ->
                                    current.copy(
                                        componentId = styleId,
                                        textContent = current.textContent
                                            ?: PlaygroundCatalog.defaultTextContent(styleId),
                                    )
                                }
                            },
                            onDelete = { removeComponent(placed.instanceId) },
                            modifier = Modifier
                                .width(ConfigBarWidth)
                                .fillMaxHeight(),
                        )
                    }
                }
            }
        }

        if (isPreviewMode) {
            PreviewModeOverlay(
                onBack = onBack,
                onExitPreview = { paletteVisible = true },
                onClear = {
                    placedComponents.clear()
                    selectedInstanceId = null
                },
                hasComponents = placedComponents.isNotEmpty(),
            )
        }

        activeDrag?.let { dragged ->
            DragGhost(component = dragged, position = dragPosition)
        }
    }
}

private fun defaultWidthFraction(componentId: String): Float? = when {
    componentId.startsWith("text-body") -> 0.5f
    componentId.startsWith("text-display") || componentId.startsWith("text-headline") -> null
    componentId in setOf(
        "text-field", "search-bar", "slider", "tabs", "segmented-button",
        "list-tile", "empty-state", "snackbar", "linear-progress", "card",
    ) -> 0.45f
    else -> null
}

@Composable
private fun PlaygroundTopBar(
    onBack: () -> Unit,
    onTogglePreview: () -> Unit,
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
                    icon = Icons.Default.Fullscreen,
                    contentDescription = "Preview full screen",
                    onClick = onTogglePreview,
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
private fun PreviewModeOverlay(
    onBack: () -> Unit,
    onExitPreview: () -> Unit,
    onClear: () -> Unit,
    hasComponents: Boolean,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(OemSpacing.md)
            .zIndex(5f),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(OemSpacing.sm)) {
            FloatingPlaygroundButton(
                icon = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "Back",
                onClick = onBack,
            )
            FloatingPlaygroundButton(
                icon = Icons.AutoMirrored.Filled.ViewSidebar,
                contentDescription = "Exit preview",
                onClick = onExitPreview,
            )
        }
        if (hasComponents) {
            FloatingPlaygroundButton(
                icon = Icons.Default.DeleteSweep,
                contentDescription = "Clear canvas",
                onClick = onClear,
            )
        }
    }
}

@Composable
private fun FloatingPlaygroundButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit,
) {
    val shape = OemVisuals.chipShape
    val interactionSource = remember { MutableInteractionSource() }
    Box(
        modifier = Modifier
            .size(OemSpacing.minTouchTarget)
            .clip(shape)
            .background(OemSurfaceElevated.copy(alpha = 0.92f))
            .oemSurfaceBorder(shape, OemBorder)
            .clickable(interactionSource = interactionSource, indication = null, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            tint = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.size(OemSpacing.lg),
        )
    }
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
private fun ComponentConfigBar(
    placed: PlacedComponent,
    onWidthFractionChange: (Float) -> Unit,
    onWrapContentToggle: (Boolean) -> Unit,
    onTextContentChange: (String) -> Unit,
    onTextStyleChange: (String) -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val definition = PlaygroundCatalog.findById(placed.componentId)
    val isWrapContent = placed.widthFraction == null
    val widthValue = placed.widthFraction ?: 0.4f
    val isText = PlaygroundCatalog.isTextComponent(placed.componentId)
    val textStyle = PlaygroundTextStyle.fromComponentId(placed.componentId)

    Surface(modifier = modifier, color = OemSurface) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(OemSpacing.md),
            verticalArrangement = Arrangement.spacedBy(OemSpacing.md),
        ) {
            CustomSectionHeader(
                title = "Properties",
                subtitle = definition?.name ?: placed.componentId,
            )

            if (isText) {
                CustomTextField(
                    value = placed.textContent.orEmpty(),
                    onValueChange = onTextContentChange,
                    label = "Text content",
                    placeholder = "Enter label or copy",
                    modifier = Modifier.fillMaxWidth(),
                )

                Text(
                    text = "Typography style",
                    style = MaterialTheme.typography.labelLarge,
                    color = OemOnSurfaceVariant,
                )
                Column(verticalArrangement = Arrangement.spacedBy(OemSpacing.xs)) {
                    PlaygroundTextStyle.entriesList.chunked(2).forEach { rowStyles ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(OemSpacing.xs),
                        ) {
                            rowStyles.forEach { style ->
                                val selected = textStyle == style
                                val shape = OemVisuals.chipShape
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(shape)
                                        .background(if (selected) OemSurfaceElevated else OemSurface)
                                        .oemSurfaceBorder(
                                            shape,
                                            if (selected) MaterialTheme.colorScheme.onSurface else OemBorder,
                                        )
                                        .clickable { onTextStyleChange(style.id) }
                                        .padding(horizontal = OemSpacing.sm, vertical = OemSpacing.xs),
                                ) {
                                    Text(
                                        text = style.label,
                                        style = MaterialTheme.typography.labelMedium,
                                        color = if (selected) {
                                            MaterialTheme.colorScheme.onSurface
                                        } else {
                                            OemOnSurfaceVariant
                                        },
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                    )
                                }
                            }
                            if (rowStyles.size == 1) {
                                Box(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }

            CustomSwitch(
                label = "Wrap content width",
                checked = isWrapContent,
                onCheckedChange = onWrapContentToggle,
            )

            if (!isWrapContent) {
                CustomSlider(
                    value = widthValue,
                    onValueChange = onWidthFractionChange,
                    label = "Width",
                    valueRange = MinWidthFraction..MaxWidthFraction,
                )
                Text(
                    text = "${(widthValue * 100).roundToInt()}% of canvas",
                    style = MaterialTheme.typography.bodySmall,
                    color = OemOnSurfaceVariant,
                )
            }

            Text(
                text = "Drag the component on canvas to reposition. Drag the right edge handle to resize.",
                style = MaterialTheme.typography.bodySmall,
                color = OemOnSurfaceVariant,
            )

            CustomButton(
                text = "Remove component",
                onClick = onDelete,
                style = ButtonStyle.Destructive,
                modifier = Modifier.fillMaxWidth(),
            )
        }
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
    Surface(modifier = modifier, color = OemSurface) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(OemSpacing.md),
        ) {
            CustomSectionHeader(
                title = "Components",
                subtitle = "Tap or drag onto canvas",
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
                items(PlaygroundCatalog.byCategory(selectedCategory), key = { it.id }) { component ->
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
            .onGloballyPositioned { itemRootPosition = it.positionInRoot() }
            .clip(shape)
            .background(OemSurfaceElevated)
            .oemSurfaceBorder(shape)
            .clickable(onClick = onClick)
            .pointerInput(component.id) {
                detectDragGesturesAfterLongPress(
                    onDragStart = { offset -> onDragStart(itemRootPosition + offset) },
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
        Icon(Icons.Default.DragIndicator, null, tint = OemOnSurfaceVariant, modifier = Modifier.size(OemSpacing.md))
        Icon(component.icon, null, tint = MaterialTheme.colorScheme.onSurface, modifier = Modifier.size(OemSpacing.lg))
        Column(modifier = Modifier.weight(1f)) {
            Text(component.name, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurface, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(component.category, style = MaterialTheme.typography.bodySmall, color = OemOnSurfaceVariant)
        }
    }
}

@Composable
private fun PlaygroundCanvas(
    placedComponents: List<PlacedComponent>,
    selectedInstanceId: Int?,
    isDropTargetActive: Boolean,
    isHovered: Boolean,
    isPreviewMode: Boolean,
    onBoundsChanged: (Rect) -> Unit,
    onSelect: (Int) -> Unit,
    onDeselect: () -> Unit,
    onMove: (instanceId: Int, deltaXDp: Float, deltaYDp: Float) -> Unit,
    onResize: (instanceId: Int, deltaFraction: Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    val density = LocalDensity.current
    val borderColor = when {
        isHovered -> MaterialTheme.colorScheme.onSurface
        isDropTargetActive -> OemOnSurfaceVariant
        else -> if (isPreviewMode) OemBorder.copy(alpha = 0f) else OemBorder
    }
    val borderWidth = if (isHovered) 2.dp else if (isPreviewMode) 0.dp else 1.dp
    val canvasPadding = if (isPreviewMode) 0.dp else OemSpacing.md

    Surface(
        modifier = modifier.onGloballyPositioned { coordinates ->
            val pos = coordinates.positionInRoot()
            val size = coordinates.size
            onBoundsChanged(Rect(pos.x, pos.y, pos.x + size.width, pos.y + size.height))
        },
        color = MaterialTheme.colorScheme.background,
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(canvasPadding)
                .clip(if (isPreviewMode) RoundedCornerShape(0.dp) else OemVisuals.cardShape)
                .border(borderWidth, borderColor, if (isPreviewMode) RoundedCornerShape(0.dp) else OemVisuals.cardShape)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onDeselect,
                ),
        ) {
            val canvasWidthDp = maxWidth.value

            if (placedComponents.isEmpty()) {
                EmptyCanvasHint(isDropTargetActive = isDropTargetActive, isPreviewMode = isPreviewMode)
            } else {
                placedComponents.forEach { placed ->
                    key(placed.instanceId) {
                        CanvasPlacedComponent(
                            placed = placed,
                            isSelected = selectedInstanceId == placed.instanceId && !isPreviewMode,
                            canvasWidthDp = canvasWidthDp,
                            onSelect = { onSelect(placed.instanceId) },
                            onMove = { deltaXDp, deltaYDp -> onMove(placed.instanceId, deltaXDp, deltaYDp) },
                            onResize = { deltaFraction -> onResize(placed.instanceId, deltaFraction) },
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CanvasPlacedComponent(
    placed: PlacedComponent,
    isSelected: Boolean,
    canvasWidthDp: Float,
    onSelect: () -> Unit,
    onMove: (deltaXDp: Float, deltaYDp: Float) -> Unit,
    onResize: (deltaFraction: Float) -> Unit,
) {
    val density = LocalDensity.current
    val widthModifier = if (placed.widthFraction != null) {
        Modifier.width((canvasWidthDp * placed.widthFraction).dp)
    } else {
        Modifier.wrapContentWidth()
    }

    Box(
        modifier = Modifier
            .offset {
                IntOffset(
                    with(density) { placed.xDp.dp.roundToPx() },
                    with(density) { placed.yDp.dp.roundToPx() },
                )
            }
            .then(widthModifier)
            .then(
                if (isSelected) {
                    Modifier.border(1.dp, MaterialTheme.colorScheme.onSurface, OemVisuals.chipShape)
                } else {
                    Modifier
                },
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onSelect,
            )
            .pointerInput(placed.instanceId) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    val deltaXDp = with(density) { dragAmount.x.toDp().value }
                    val deltaYDp = with(density) { dragAmount.y.toDp().value }
                    onMove(deltaXDp, deltaYDp)
                }
            },
    ) {
        PlaygroundComponentRenderer(
            componentId = placed.componentId,
            textContent = placed.textContent,
            modifier = if (placed.widthFraction != null) Modifier.fillMaxWidth() else Modifier,
        )

        if (isSelected) {
            Box(
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .width(ResizeHandleWidth)
                    .height(OemSpacing.minTouchTarget)
                    .pointerInput(placed.instanceId) {
                        detectHorizontalDragGestures { change, dragAmount ->
                            change.consume()
                            val deltaFraction = dragAmount / (canvasWidthDp * density.density)
                            onResize(deltaFraction)
                        }
                    },
                contentAlignment = Alignment.Center,
            ) {
                Box(
                    modifier = Modifier
                        .size(width = 4.dp, height = OemSpacing.lg)
                        .clip(RoundedCornerShape(2.dp))
                        .background(MaterialTheme.colorScheme.onSurface),
                )
            }
        }
    }
}

@Composable
private fun EmptyCanvasHint(
    isDropTargetActive: Boolean,
    isPreviewMode: Boolean,
    modifier: Modifier = Modifier,
) {
    if (isPreviewMode) return

    Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(OemSpacing.sm),
        ) {
            Icon(
                Icons.AutoMirrored.Filled.ViewSidebar,
                contentDescription = null,
                tint = OemOnSurfaceVariant,
                modifier = Modifier.size(OemSpacing.xl),
            )
            Text(
                text = if (isDropTargetActive) "Drop here" else "Build your screen here",
                style = MaterialTheme.typography.titleMedium,
                color = if (isDropTargetActive) MaterialTheme.colorScheme.onSurface else OemOnSurfaceVariant,
            )
            Text(
                text = "Tap or drag components from the palette",
                style = MaterialTheme.typography.bodyMedium,
                color = OemOnSurfaceVariant,
            )
        }
    }
}

@Composable
private fun DragGhost(
    component: PlaygroundComponentDefinition,
    position: Offset,
) {
    val ghostSizePx = with(LocalDensity.current) { DragGhostSize.toPx() }

    Box(modifier = Modifier.fillMaxSize().zIndex(10f)) {
        Box(
            modifier = Modifier
                .offset {
                    IntOffset(
                        (position.x - ghostSizePx / 2).roundToInt(),
                        (position.y - ghostSizePx / 2).roundToInt(),
                    )
                }
                .size(DragGhostSize)
                .graphicsLayer { alpha = 0.85f; shadowElevation = 8f }
                .clip(OemVisuals.cardShape)
                .background(OemSurfaceElevated)
                .border(2.dp, MaterialTheme.colorScheme.onSurface, OemVisuals.cardShape),
            contentAlignment = Alignment.Center,
        ) {
            Icon(component.icon, component.name, tint = MaterialTheme.colorScheme.onSurface, modifier = Modifier.size(OemSpacing.lg))
        }
    }
}
