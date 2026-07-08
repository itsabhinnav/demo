package com.test.design.presentation.ivi.common

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.test.design.theme.CarDesignTokens
import com.test.design.theme.ExpressiveShapes

@Composable
fun DetailSurfaceCard(
    modifier: Modifier = Modifier,
    emphasized: Boolean = false,
    content: @Composable ColumnScope.() -> Unit,
) {
    val motionSpec = MaterialTheme.motionScheme.defaultSpatialSpec<Float>()
    val scale by animateFloatAsState(
        targetValue = if (emphasized) 1.02f else 1f,
        animationSpec = motionSpec,
        label = "detail_card_scale",
    )
    Card(
        modifier = modifier.graphicsLayer {
            scaleX = scale
            scaleY = scale
        },
        shape = ExpressiveShapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = if (emphasized) 8.dp else 2.dp),
    ) {
        Column(
            modifier = Modifier.padding(CarDesignTokens.TouchTargetSpacing),
            content = content,
        )
    }
}
