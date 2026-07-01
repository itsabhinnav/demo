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
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.layout.wrapContentHeight
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.debounce
import com.test.design.component.components.ButtonStyle
import com.test.design.component.components.CustomButton
import com.test.design.component.components.CustomSectionHeader
import com.test.design.component.components.CustomSlider
import com.test.design.component.components.CustomSwitch
import com.test.design.component.components.CustomTopBar
import com.test.design.component.components.CustomTextField
import com.test.design.component.theme.OemBackground
import com.test.design.component.theme.OemBorder
import com.test.design.component.theme.OemGrayDark
import com.test.design.component.theme.OemOnSurfaceVariant
import com.test.design.component.theme.OemSpacing
import com.test.design.component.theme.OemSurface
import com.test.design.component.theme.OemSurfaceElevated
import com.test.design.component.theme.OemSurfaceVariant
import com.test.design.component.theme.OemVisuals
import com.test.design.component.theme.OemWhite
import com.test.design.component.theme.oemSurfaceBorder
import kotlin.math.roundToInt

private val PaletteWidth = 280.dp
private val ConfigBarWidth = 260.dp
private val DragGhostSize = 56.dp
private val ResizeHandleWidth = 12.dp
private val MinWidthFraction = 0.12f
private val MaxWidthFraction = 1f
private val MinHeightFraction = 0.08f
private val MaxHeightFraction = 1f
private val MaxLayoutSpacingDp = 48f
private const val AutosaveDebounceMs = 400L
private const val PreviewControlsHideDelayMs = 2000L
private val PreviewCornerHotspotSize = OemSpacing.minTouchTarget
private val PreviewEdgeSwipeWidth = 28.dp
private const val PreviewEdgeSwipeThresholdDp = 72f
private val CanvasGridMinorStep = OemSpacing.sm
private val CanvasGridMajorStep = OemSpacing.md

private data class PlaygroundBackgroundOption(
    val label: String,
    val color: Color,
)

private val PlaygroundBackgroundOptions = listOf(
    PlaygroundBackgroundOption("Black", OemBackground),
    PlaygroundBackgroundOption("Surface", OemSurface),
    PlaygroundBackgroundOption("Elevated", OemSurfaceElevated),
    PlaygroundBackgroundOption("Variant", OemSurfaceVariant),
    PlaygroundBackgroundOption("Gray", OemGrayDark),
    PlaygroundBackgroundOption("White", OemWhite),
)

private fun Color.encodeForStorage(): Long = value.toLong()

private fun Long.decodeToColor(): Color = Color(toULong())

private enum class SaveStatus { Saved }

@Composable
fun ComponentPlaygroundDemo(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val designStore = remember { PlaygroundDesignStore(context) }

    var paletteVisible by remember { mutableStateOf(true) }
    val placedComponents = remember { mutableStateListOf<PlacedComponent>() }
    var nextInstanceId by remember { mutableIntStateOf(0) }
    var selectedCategory by remember { mutableStateOf(PlaygroundCatalog.categories.first()) }
    var selectedInstanceId by remember { mutableStateOf<Int?>(null) }
    var saveStatus by remember { mutableStateOf<SaveStatus?>(null) }

    var activeDrag by remember { mutableStateOf<PlaygroundComponentDefinition?>(null) }
    var dragPosition by remember { mutableStateOf(Offset.Zero) }
    var canvasBounds by remember { mutableStateOf(Rect.Zero) }
    var canvasContentSize by remember { mutableStateOf(IntSize.Zero) }
    var isCanvasHovered by remember { mutableStateOf(false) }
    var canvasBackgroundColor by remember { mutableStateOf(OemBackground) }

    val isPreviewMode = !paletteVisible
    val paletteWidth by animateDpAsState(
        targetValue = if (paletteVisible) PaletteWidth else 0.dp,
        label = "paletteWidth",
    )

    LaunchedEffect(Unit) {
        designStore.load()?.let { snapshot ->
            placedComponents.clear()
            placedComponents.addAll(
                snapshot.components.map { placed ->
                    placed.copy(
                        props = PlaygroundComponentProps.mergeWithDefaults(placed.componentId, placed.props),
                    )
                },
            )
            nextInstanceId = snapshot.nextInstanceId
            canvasBackgroundColor = snapshot.backgroundColorArgb?.decodeToColor() ?: OemBackground
        }
    }

    LaunchedEffect(Unit) {
        @OptIn(FlowPreview::class)
        run {
            snapshotFlow {
                Triple(placedComponents.toList(), nextInstanceId, canvasBackgroundColor)
            }
                .debounce(AutosaveDebounceMs)
                .collect { (components, nextId, backgroundColor) ->
                    designStore.save(components, nextId, backgroundColor.encodeForStorage())
                    saveStatus = SaveStatus.Saved
                }
        }
    }

    fun updatePlaced(instanceId: Int, transform: (PlacedComponent) -> PlacedComponent) {
        val index = placedComponents.indexOfFirst { it.instanceId == instanceId }
        if (index >= 0) placedComponents[index] = transform(placedComponents[index])
    }

    fun addComponent(
        componentId: String,
        xFraction: Float? = null,
        yFraction: Float? = null,
    ) {
        val x = xFraction ?: (0.05f + (placedComponents.size % 4) * 0.02f)
        val y = yFraction ?: (0.05f + placedComponents.size * 0.04f).coerceAtMost(0.85f)
        val placed = PlacedComponent(
            instanceId = nextInstanceId++,
            componentId = componentId,
            xFraction = x.coerceIn(0f, 1f),
            yFraction = y.coerceIn(0f, 1f),
            widthFraction = defaultWidthFraction(componentId),
            textContent = if (PlaygroundCatalog.isTextComponent(componentId)) {
                PlaygroundCatalog.defaultTextContent(componentId)
            } else {
                null
            },
            props = PlaygroundComponentProps.defaultProps(componentId),
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
            if (canvasContentSize.width > 0 && canvasContentSize.height > 0) {
                val localX = (dragPosition.x - canvasBounds.left).coerceAtLeast(0f)
                val localY = (dragPosition.y - canvasBounds.top).coerceAtLeast(0f)
                val xFraction = localX / canvasContentSize.width
                val yFraction = localY / canvasContentSize.height
                addComponent(dragged.id, xFraction, yFraction)
            } else {
                addComponent(dragged.id)
            }
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
                    onSave = {
                        designStore.save(
                            placedComponents.toList(),
                            nextInstanceId,
                            canvasBackgroundColor.encodeForStorage(),
                        )
                        saveStatus = SaveStatus.Saved
                    },
                    onClear = {
                        placedComponents.clear()
                        selectedInstanceId = null
                        nextInstanceId = 0
                        canvasBackgroundColor = OemBackground
                        designStore.clear()
                    },
                    componentCount = placedComponents.size,
                    saveStatus = saveStatus,
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
                    backgroundColor = canvasBackgroundColor,
                    onBoundsChanged = { bounds, contentSize ->
                        canvasBounds = bounds
                        canvasContentSize = contentSize
                    },
                    onSelect = { selectedInstanceId = it },
                    onDeselect = { selectedInstanceId = null },
                    onMove = { instanceId, deltaXFraction, deltaYFraction ->
                        updatePlaced(instanceId) { placed ->
                            placed.copy(
                                xFraction = (placed.xFraction + deltaXFraction).coerceIn(0f, 1f),
                                yFraction = (placed.yFraction + deltaYFraction).coerceIn(0f, 1f),
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
                    visible = !isPreviewMode,
                    enter = slideInHorizontally { it } + fadeIn(),
                    exit = slideOutHorizontally { it } + fadeOut(),
                ) {
                    if (selectedPlaced != null) {
                        ComponentConfigBar(
                            placed = selectedPlaced,
                            onWidthFractionChange = { fraction ->
                                updatePlaced(selectedPlaced.instanceId) { it.copy(widthFraction = fraction) }
                            },
                            onWrapContentToggle = { wrap ->
                                updatePlaced(selectedPlaced.instanceId) {
                                    it.copy(widthFraction = if (wrap) null else 0.4f)
                                }
                            },
                            onTextContentChange = { content ->
                                updatePlaced(selectedPlaced.instanceId) { it.copy(textContent = content) }
                            },
                            onTextStyleChange = { styleId ->
                                updatePlaced(selectedPlaced.instanceId) { current ->
                                    current.copy(
                                        componentId = styleId,
                                        textContent = current.textContent
                                            ?: PlaygroundCatalog.defaultTextContent(styleId),
                                        props = PlaygroundComponentProps.defaultProps(styleId),
                                    )
                                }
                            },
                            onPropChange = { key, value ->
                                updatePlaced(selectedPlaced.instanceId) { current ->
                                    current.copy(props = current.props + (key to value))
                                }
                            },
                            onHeightFractionChange = { fraction ->
                                updatePlaced(selectedPlaced.instanceId) { it.copy(heightFraction = fraction) }
                            },
                            onWrapContentHeightToggle = { wrap ->
                                updatePlaced(selectedPlaced.instanceId) {
                                    it.copy(heightFraction = if (wrap) null else 0.25f)
                                }
                            },
                            onMarginChange = { margin ->
                                updatePlaced(selectedPlaced.instanceId) {
                                    it.copy(marginDp = margin.coerceIn(0f, MaxLayoutSpacingDp))
                                }
                            },
                            onPaddingChange = { padding ->
                                updatePlaced(selectedPlaced.instanceId) {
                                    it.copy(paddingDp = padding.coerceIn(0f, MaxLayoutSpacingDp))
                                }
                            },
                            onDelete = { removeComponent(selectedPlaced.instanceId) },
                            modifier = Modifier
                                .width(ConfigBarWidth)
                                .fillMaxHeight(),
                        )
                    } else {
                        CanvasConfigBar(
                            selectedColor = canvasBackgroundColor,
                            onColorSelected = { canvasBackgroundColor = it },
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
    onSave: () -> Unit,
    onClear: () -> Unit,
    componentCount: Int,
    saveStatus: SaveStatus?,
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
                if (saveStatus == SaveStatus.Saved) {
                    Text(
                        text = "Saved",
                        style = MaterialTheme.typography.bodySmall,
                        color = OemOnSurfaceVariant,
                    )
                }
                CustomButton(
                    text = "Save",
                    onClick = onSave,
                    style = ButtonStyle.Secondary,
                )
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
) {
    var controlsVisible by remember { mutableStateOf(true) }
    var hideTimerGeneration by remember { mutableIntStateOf(0) }
    val density = LocalDensity.current

    fun revealControls() {
        controlsVisible = true
        hideTimerGeneration++
    }

    LaunchedEffect(hideTimerGeneration) {
        delay(PreviewControlsHideDelayMs)
        controlsVisible = false
    }

    Box(modifier = Modifier.fillMaxSize().zIndex(5f)) {
        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(PreviewEdgeSwipeWidth)
                .align(Alignment.CenterStart)
                .pointerInput(Unit) {
                    val swipeThresholdPx = with(density) { PreviewEdgeSwipeThresholdDp.dp.toPx() }
                    var accumulatedSwipe = 0f
                    detectHorizontalDragGestures(
                        onDragStart = { accumulatedSwipe = 0f },
                        onDragEnd = {
                            if (accumulatedSwipe >= swipeThresholdPx) onExitPreview()
                            accumulatedSwipe = 0f
                        },
                        onHorizontalDrag = { change, dragAmount ->
                            change.consume()
                            if (dragAmount > 0f) accumulatedSwipe += dragAmount
                        },
                    )
                },
        )

        Box(
            modifier = Modifier
                .size(PreviewCornerHotspotSize)
                .align(Alignment.TopStart)
                .pointerInput(Unit) {
                    detectTapGestures(
                        onLongPress = { revealControls() },
                        onTap = { revealControls() },
                    )
                },
        )

        AnimatedVisibility(
            visible = controlsVisible,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(OemSpacing.md),
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(OemSpacing.sm)) {
                FloatingPlaygroundButton(
                    icon = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    onClick = {
                        revealControls()
                        onBack()
                    },
                )
                FloatingPlaygroundButton(
                    icon = Icons.AutoMirrored.Filled.ViewSidebar,
                    contentDescription = "Exit preview",
                    onClick = {
                        revealControls()
                        onExitPreview()
                    },
                )
            }
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
private fun CanvasConfigBar(
    selectedColor: Color,
    onColorSelected: (Color) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(modifier = modifier, color = OemSurface) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(OemSpacing.md),
            verticalArrangement = Arrangement.spacedBy(OemSpacing.md),
        ) {
            CustomSectionHeader(
                title = "Screen",
                subtitle = "Background color",
            )

            Column(verticalArrangement = Arrangement.spacedBy(OemSpacing.sm)) {
                PlaygroundBackgroundOptions.chunked(2).forEach { rowOptions ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(OemSpacing.sm),
                    ) {
                        rowOptions.forEach { option ->
                            val selected = selectedColor.value == option.color.value
                            BackgroundColorSwatch(
                                label = option.label,
                                color = option.color,
                                selected = selected,
                                onClick = { onColorSelected(option.color) },
                                modifier = Modifier.weight(1f),
                            )
                        }
                        if (rowOptions.size == 1) {
                            Box(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }

            Text(
                text = "Tap a component to edit its properties",
                style = MaterialTheme.typography.bodySmall,
                color = OemOnSurfaceVariant,
            )
        }
    }
}

@Composable
private fun BackgroundColorSwatch(
    label: String,
    color: Color,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val shape = OemVisuals.chipShape
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(OemSpacing.xs),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .clip(shape)
                .background(color)
                .oemSurfaceBorder(
                    shape,
                    if (selected) MaterialTheme.colorScheme.onSurface else OemBorder,
                )
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onClick,
                ),
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            color = if (selected) MaterialTheme.colorScheme.onSurface else OemOnSurfaceVariant,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun ComponentConfigBar(
    placed: PlacedComponent,
    onWidthFractionChange: (Float) -> Unit,
    onWrapContentToggle: (Boolean) -> Unit,
    onHeightFractionChange: (Float) -> Unit,
    onWrapContentHeightToggle: (Boolean) -> Unit,
    onMarginChange: (Float) -> Unit,
    onPaddingChange: (Float) -> Unit,
    onTextContentChange: (String) -> Unit,
    onTextStyleChange: (String) -> Unit,
    onPropChange: (String, String) -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val isWrapContentWidth = placed.widthFraction == null
    val widthValue = placed.widthFraction ?: 0.4f
    val isWrapContentHeight = placed.heightFraction == null
    val heightValue = placed.heightFraction ?: 0.25f

    Surface(modifier = modifier, color = OemSurface) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(OemSpacing.md),
            verticalArrangement = Arrangement.spacedBy(OemSpacing.md),
        ) {
            ComponentPropertyEditor(
                componentId = placed.componentId,
                props = placed.props,
                textContent = placed.textContent,
                onPropChange = onPropChange,
                onTextContentChange = onTextContentChange,
                onTextStyleChange = onTextStyleChange,
            )

            CustomSectionHeader(
                title = "Layout",
                subtitle = "Size and spacing",
            )

            CustomSwitch(
                label = "Wrap content width",
                checked = isWrapContentWidth,
                onCheckedChange = onWrapContentToggle,
            )

            if (!isWrapContentWidth) {
                CustomSlider(
                    value = widthValue,
                    onValueChange = onWidthFractionChange,
                    label = "Width",
                    valueRange = MinWidthFraction..MaxWidthFraction,
                )
                Text(
                    text = "${(widthValue * 100).roundToInt()}% of canvas width",
                    style = MaterialTheme.typography.bodySmall,
                    color = OemOnSurfaceVariant,
                )
            }

            CustomSwitch(
                label = "Wrap content height",
                checked = isWrapContentHeight,
                onCheckedChange = onWrapContentHeightToggle,
            )

            if (!isWrapContentHeight) {
                CustomSlider(
                    value = heightValue,
                    onValueChange = onHeightFractionChange,
                    label = "Height",
                    valueRange = MinHeightFraction..MaxHeightFraction,
                )
                Text(
                    text = "${(heightValue * 100).roundToInt()}% of canvas height",
                    style = MaterialTheme.typography.bodySmall,
                    color = OemOnSurfaceVariant,
                )
            }

            CustomSlider(
                value = placed.marginDp,
                onValueChange = onMarginChange,
                label = "Margin",
                valueRange = 0f..MaxLayoutSpacingDp,
            )
            Text(
                text = "${placed.marginDp.roundToInt()}dp outer spacing",
                style = MaterialTheme.typography.bodySmall,
                color = OemOnSurfaceVariant,
            )

            CustomSlider(
                value = placed.paddingDp,
                onValueChange = onPaddingChange,
                label = "Padding",
                valueRange = 0f..MaxLayoutSpacingDp,
            )
            Text(
                text = "${placed.paddingDp.roundToInt()}dp inner spacing",
                style = MaterialTheme.typography.bodySmall,
                color = OemOnSurfaceVariant,
            )

            Text(
                text = "Drag to reposition. Drag the right edge to resize width.",
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
    backgroundColor: Color,
    onBoundsChanged: (Rect, IntSize) -> Unit,
    onSelect: (Int) -> Unit,
    onDeselect: () -> Unit,
    onMove: (instanceId: Int, deltaXFraction: Float, deltaYFraction: Float) -> Unit,
    onResize: (instanceId: Int, deltaFraction: Float) -> Unit,
    modifier: Modifier = Modifier,
) {
    val borderColor = when {
        isHovered -> MaterialTheme.colorScheme.onSurface
        isDropTargetActive -> OemOnSurfaceVariant
        else -> if (isPreviewMode) OemBorder.copy(alpha = 0f) else OemBorder
    }
    val borderWidth = if (isHovered) 2.dp else if (isPreviewMode) 0.dp else 1.dp
    val canvasPadding = OemSpacing.sm
    val canvasDeselectInteractionSource = remember { MutableInteractionSource() }

    Surface(
        modifier = modifier,
        color = MaterialTheme.colorScheme.background,
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(canvasPadding)
                .onGloballyPositioned { coordinates ->
                    val pos = coordinates.positionInRoot()
                    val size = coordinates.size
                    onBoundsChanged(
                        Rect(pos.x, pos.y, pos.x + size.width, pos.y + size.height),
                        IntSize(size.width, size.height),
                    )
                }
                .clip(if (isPreviewMode) RoundedCornerShape(0.dp) else OemVisuals.cardShape)
                .border(borderWidth, borderColor, if (isPreviewMode) RoundedCornerShape(0.dp) else OemVisuals.cardShape)
                .then(
                    if (isPreviewMode) {
                        Modifier
                    } else {
                        Modifier.clickable(
                            interactionSource = canvasDeselectInteractionSource,
                            indication = null,
                            onClick = onDeselect,
                        )
                    },
                ),
        ) {
            val canvasWidthDp = maxWidth.value
            val canvasHeightDp = maxHeight.value
            val guideColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.55f)
            val guideMinorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.28f)
            val guidesModifier = if (isPreviewMode) {
                Modifier
            } else {
                Modifier.drawBehind {
                    drawPlaygroundGuides(
                        gridMinorColor = OemBorder.copy(alpha = 0.22f),
                        gridMajorColor = OemBorder.copy(alpha = 0.38f),
                        guideColor = guideColor,
                        guideMinorColor = guideMinorColor,
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(backgroundColor)
                    .then(guidesModifier),
            ) {
                if (placedComponents.isEmpty()) {
                    EmptyCanvasHint(isDropTargetActive = isDropTargetActive, isPreviewMode = isPreviewMode)
                } else {
                    placedComponents.forEach { placed ->
                        key(placed.instanceId) {
                            CanvasPlacedComponent(
                                placed = placed,
                                isSelected = selectedInstanceId == placed.instanceId && !isPreviewMode,
                                isFrozen = isPreviewMode,
                                canvasWidthDp = canvasWidthDp,
                                canvasHeightDp = canvasHeightDp,
                                onSelect = { onSelect(placed.instanceId) },
                                onMove = { deltaXFraction, deltaYFraction ->
                                    onMove(placed.instanceId, deltaXFraction, deltaYFraction)
                                },
                                onResize = { deltaFraction -> onResize(placed.instanceId, deltaFraction) },
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun DrawScope.drawPlaygroundGuides(
    gridMinorColor: Color,
    gridMajorColor: Color,
    guideColor: Color,
    guideMinorColor: Color,
) {
    val minorStepPx = CanvasGridMinorStep.toPx()
    val majorEvery = (CanvasGridMajorStep / CanvasGridMinorStep).roundToInt().coerceAtLeast(1)
    val dashEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 8f), 0f)

    var x = 0f
    var column = 0
    while (x <= size.width) {
        val isMajor = column % majorEvery == 0
        drawLine(
            color = if (isMajor) gridMajorColor else gridMinorColor,
            start = Offset(x, 0f),
            end = Offset(x, size.height),
            strokeWidth = if (isMajor) 1f else 0.5f,
        )
        x += minorStepPx
        column++
    }

    var y = 0f
    var row = 0
    while (y <= size.height) {
        val isMajor = row % majorEvery == 0
        drawLine(
            color = if (isMajor) gridMajorColor else gridMinorColor,
            start = Offset(0f, y),
            end = Offset(size.width, y),
            strokeWidth = if (isMajor) 1f else 0.5f,
        )
        y += minorStepPx
        row++
    }

    val centerX = size.width / 2f
    val centerY = size.height / 2f
    drawLine(
        color = guideColor,
        start = Offset(centerX, 0f),
        end = Offset(centerX, size.height),
        strokeWidth = 1.5f,
        pathEffect = dashEffect,
    )
    drawLine(
        color = guideColor,
        start = Offset(0f, centerY),
        end = Offset(size.width, centerY),
        strokeWidth = 1.5f,
        pathEffect = dashEffect,
    )

    listOf(size.width / 3f, size.width * 2f / 3f).forEach { guideX ->
        drawLine(
            color = guideMinorColor,
            start = Offset(guideX, 0f),
            end = Offset(guideX, size.height),
            strokeWidth = 1f,
            pathEffect = dashEffect,
        )
    }
    listOf(size.height / 3f, size.height * 2f / 3f).forEach { guideY ->
        drawLine(
            color = guideMinorColor,
            start = Offset(0f, guideY),
            end = Offset(size.width, guideY),
            strokeWidth = 1f,
            pathEffect = dashEffect,
        )
    }
}

@Composable
private fun CanvasPlacedComponent(
    placed: PlacedComponent,
    isSelected: Boolean,
    isFrozen: Boolean,
    canvasWidthDp: Float,
    canvasHeightDp: Float,
    onSelect: () -> Unit,
    onMove: (deltaXFraction: Float, deltaYFraction: Float) -> Unit,
    onResize: (deltaFraction: Float) -> Unit,
) {
    val density = LocalDensity.current
    val canvasWidthPx = canvasWidthDp * density.density
    val canvasHeightPx = canvasHeightDp * density.density
    val widthModifier = if (placed.widthFraction != null) {
        Modifier.width((canvasWidthDp * placed.widthFraction).dp)
    } else {
        Modifier.wrapContentWidth()
    }
    val heightModifier = if (placed.heightFraction != null) {
        Modifier.height((canvasHeightDp * placed.heightFraction).dp)
    } else {
        Modifier.wrapContentHeight()
    }
    val contentModifier = when {
        placed.widthFraction != null && placed.heightFraction != null -> Modifier.fillMaxSize()
        placed.widthFraction != null -> Modifier.fillMaxWidth()
        placed.heightFraction != null -> Modifier.fillMaxHeight()
        else -> Modifier
    }

    val selectionInteractionSource = remember { MutableInteractionSource() }
    val interactionModifier = if (isFrozen) {
        Modifier
    } else {
        Modifier
            .clickable(
                interactionSource = selectionInteractionSource,
                indication = null,
                onClick = onSelect,
            )
            .pointerInput(placed.instanceId) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    if (canvasWidthPx > 0f && canvasHeightPx > 0f) {
                        onMove(
                            dragAmount.x / canvasWidthPx,
                            dragAmount.y / canvasHeightPx,
                        )
                    }
                }
            }
    }

    Box(
        modifier = Modifier.offset {
            IntOffset(
                (placed.xFraction * canvasWidthDp).dp.roundToPx(),
                (placed.yFraction * canvasHeightDp).dp.roundToPx(),
            )
        },
    ) {
        Box(
            modifier = Modifier
                .padding(placed.marginDp.dp)
                .then(widthModifier)
                .then(heightModifier)
                .then(
                    if (isSelected) {
                        Modifier.border(1.dp, MaterialTheme.colorScheme.onSurface, OemVisuals.chipShape)
                    } else {
                        Modifier
                    },
                )
                .clip(OemVisuals.chipShape)
                .then(interactionModifier),
        ) {
            Box(
                modifier = Modifier
                    .padding(placed.paddingDp.dp)
                    .then(contentModifier),
            ) {
                PlaygroundComponentRenderer(
                    componentId = placed.componentId,
                    textContent = placed.textContent,
                    props = placed.props,
                    modifier = contentModifier,
                )
            }

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
                text = "Tap canvas for screen background · drag from palette",
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
