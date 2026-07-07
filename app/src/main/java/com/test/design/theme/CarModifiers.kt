package com.test.design.theme

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

fun Modifier.carTouchTarget(): Modifier = defaultMinSize(
    minWidth = CarDesignTokens.MinTouchTarget,
    minHeight = CarDesignTokens.MinTouchTarget,
)

fun Modifier.carListItemHeight(): Modifier = defaultMinSize(
    minHeight = CarDesignTokens.ListItemHeight,
)

val CarButtonContentPadding = PaddingValues(
    horizontal = CarDesignTokens.TouchTargetSpacing,
    vertical = 20.dp,
)
