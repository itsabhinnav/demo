package com.test.design.presentation.ivi.common

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.test.design.presentation.ivi.dashboard.model.DashboardWidget
import com.test.design.presentation.ivi.dashboard.widgetIconSharedElement
import com.test.design.presentation.ivi.dashboard.widgetTitleSharedElement
import com.test.design.theme.CarDesignTokens
import com.test.design.theme.carTouchTarget

@Composable
fun WidgetScreenHeader(
    title: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    trailingContent: @Composable () -> Unit = {},
) {
    WidgetScreenHeaderContent(
        title = title,
        onBack = onBack,
        modifier = modifier,
        trailingContent = trailingContent,
    )
}

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun SharedTransitionScope.WidgetScreenHeader(
    widget: DashboardWidget,
    onBack: () -> Unit,
    animatedVisibilityScope: AnimatedVisibilityScope,
    modifier: Modifier = Modifier,
    trailingContent: @Composable () -> Unit = {},
) {
    WidgetScreenHeaderContent(
        title = widget.title,
        onBack = onBack,
        modifier = modifier,
        trailingContent = trailingContent,
        iconContent = {
            Icon(
                imageVector = widget.icon,
                contentDescription = null,
                modifier = widgetIconSharedElement(
                    widget = widget,
                    animatedVisibilityScope = animatedVisibilityScope,
                    modifier = Modifier.size(CarDesignTokens.PrimaryIcon),
                ),
                tint = MaterialTheme.colorScheme.onBackground,
            )
        },
        titleModifier = widgetTitleSharedElement(
            widget = widget,
            animatedVisibilityScope = animatedVisibilityScope,
            modifier = Modifier,
        ),
    )
}

@Composable
private fun WidgetScreenHeaderContent(
    title: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    trailingContent: @Composable () -> Unit = {},
    iconContent: @Composable (() -> Unit)? = null,
    titleModifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
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
        Row(
            horizontalArrangement = Arrangement.spacedBy(CarDesignTokens.TouchTargetSpacing),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            iconContent?.invoke()
            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = titleModifier,
            )
        }
        trailingContent()
    }
}
