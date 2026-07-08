package com.test.design.presentation.ivi.dashboard.components

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.test.design.presentation.ivi.dashboard.model.DashboardWidget
import com.test.design.presentation.ivi.dashboard.widgetContainerTransform
import com.test.design.presentation.ivi.dashboard.widgetIconSharedElement
import com.test.design.presentation.ivi.dashboard.widgetTitleSharedElement
import com.test.design.theme.CarBackgroundTokens
import com.test.design.theme.CarDesignTokens
import com.test.design.theme.WidgetCardShape
import com.test.design.theme.carTouchTarget
import com.test.design.theme.rememberClimateCardShape
import com.test.design.theme.rememberMediaCardShape
import com.test.design.theme.rememberVehicleCardShape
import androidx.compose.ui.graphics.Shape

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.DashboardWidgetCard(
    widget: DashboardWidget,
    subtitle: String,
    onClick: () -> Unit,
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier,
    isExpanded: Boolean = false,
    morphExpanded: Boolean = false,
) {
    val containerColor = when (widget) {
        DashboardWidget.Media -> MaterialTheme.colorScheme.secondaryContainer
        DashboardWidget.Climate -> MaterialTheme.colorScheme.tertiaryContainer
        DashboardWidget.Navigation -> MaterialTheme.colorScheme.primaryContainer
        DashboardWidget.Vehicle -> MaterialTheme.colorScheme.surfaceContainerHigh
        else -> MaterialTheme.colorScheme.surfaceContainer
    }.copy(alpha = CarBackgroundTokens.GlassSurfaceAlpha)
    val contentColor = when (widget) {
        DashboardWidget.Media -> MaterialTheme.colorScheme.onSecondaryContainer
        DashboardWidget.Climate -> MaterialTheme.colorScheme.onTertiaryContainer
        DashboardWidget.Navigation -> MaterialTheme.colorScheme.onPrimaryContainer
        DashboardWidget.Vehicle -> MaterialTheme.colorScheme.onSurface
        else -> MaterialTheme.colorScheme.onSurface
    }

    val cardShape: Shape = when (widget) {
        DashboardWidget.Climate -> rememberClimateCardShape(active = morphExpanded)
        DashboardWidget.Media -> rememberMediaCardShape(playing = morphExpanded)
        DashboardWidget.Vehicle -> rememberVehicleCardShape(active = morphExpanded)
        else -> WidgetCardShape
    }

    Box(
        modifier = widgetContainerTransform(
            widget = widget,
            animatedVisibilityScope = animatedVisibilityScope,
            modifier = modifier
                .clip(cardShape)
                .background(containerColor)
                .clickable(enabled = !isExpanded, onClick = onClick)
                .padding(CarDesignTokens.SectionPadding),
        ),
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Icon(
                imageVector = widget.icon,
                contentDescription = null,
                modifier = widgetIconSharedElement(
                    widget = widget,
                    animatedVisibilityScope = animatedVisibilityScope,
                    modifier = Modifier
                        .size(CarDesignTokens.PrimaryIcon)
                        .carTouchTarget(),
                ),
                tint = contentColor,
            )
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = widget.title,
                    style = MaterialTheme.typography.headlineSmall,
                    color = contentColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = widgetTitleSharedElement(
                        widget = widget,
                        animatedVisibilityScope = animatedVisibilityScope,
                        modifier = Modifier,
                    ),
                )
                if (!isExpanded) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = contentColor.copy(alpha = 0.75f),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}
