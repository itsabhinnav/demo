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
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.test.design.theme.CarDesignTokens
import com.test.design.theme.DetailCardExpandedRadii
import com.test.design.theme.DetailCardRestRadii
import com.test.design.theme.ExpressiveShapes
import com.test.design.theme.MorphingCornerRadii
import com.test.design.theme.rememberMorphingRoundedShape

@Composable
fun DetailSurfaceCard(
    modifier: Modifier = Modifier,
    emphasized: Boolean = false,
    content: @Composable ColumnScope.() -> Unit,
) {
    DetailSurfaceCard(
        modifier = modifier,
        emphasized = emphasized,
        shape = ExpressiveShapes.large,
        content = content,
    )
}

@Composable
fun MorphingDetailSurfaceCard(
    morphExpanded: Boolean,
    modifier: Modifier = Modifier,
    emphasized: Boolean = false,
    compactRadii: MorphingCornerRadii = DetailCardRestRadii,
    expandedRadii: MorphingCornerRadii = DetailCardExpandedRadii,
    content: @Composable ColumnScope.() -> Unit,
) {
    val shape = rememberMorphingRoundedShape(
        target = if (morphExpanded) expandedRadii else compactRadii,
    )
    DetailSurfaceCard(
        modifier = modifier,
        emphasized = emphasized,
        shape = shape,
        content = content,
    )
}

@Composable
private fun DetailSurfaceCard(
    modifier: Modifier = Modifier,
    emphasized: Boolean = false,
    shape: Shape,
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
        shape = shape,
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
