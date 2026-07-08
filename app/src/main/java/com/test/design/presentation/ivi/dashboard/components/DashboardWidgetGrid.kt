package com.test.design.presentation.ivi.dashboard.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import com.test.design.presentation.ivi.dashboard.model.DashboardWidget
import com.test.design.theme.CarDesignTokens
import com.test.design.theme.WidgetCardShape
import com.test.design.theme.carTouchTarget

/**
 * 4-column × 2-row grid; each widget spans 1×2 cells (one column, full height).
 */
@Composable
fun DashboardWidgetGrid(
    widgets: List<DashboardWidget>,
    widgetContent: @Composable (DashboardWidget, Modifier) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(CarDesignTokens.SectionSpacing),
    ) {
        widgets.forEach { widget ->
            widgetContent(
                widget,
                Modifier
                    .weight(widget.gridColumnSpan.toFloat())
                    .fillMaxHeight(),
            )
        }
    }
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.WidgetPlaceholderScreen(
    widget: DashboardWidget,
    onBack: () -> Unit,
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .sharedBounds(
                rememberSharedContentState(key = widget.sharedElementKey),
                animatedVisibilityScope = animatedVisibilityScope,
                resizeMode = SharedTransitionScope.ResizeMode.RemeasureToBounds,
                clipInOverlayDuringTransition = OverlayClip(WidgetCardShape),
            )
            .clip(WidgetCardShape)
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .padding(CarDesignTokens.ContentPadding),
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(CarDesignTokens.SectionSpacing),
        ) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.carTouchTarget(),
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back to dashboard",
                    modifier = Modifier.size(CarDesignTokens.PrimaryIcon),
                )
            }
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Icon(
                    imageVector = widget.icon,
                    contentDescription = null,
                    modifier = Modifier.size(CarDesignTokens.PrimaryIcon * 2),
                    tint = MaterialTheme.colorScheme.primary,
                )
                Text(
                    text = widget.title,
                    style = MaterialTheme.typography.displaySmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center,
                )
                Text(
                    text = widget.subtitle,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}
