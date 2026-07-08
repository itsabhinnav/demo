package com.test.design.theme

import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class MorphingCornerRadii(
    val topStart: Dp,
    val topEnd: Dp,
    val bottomEnd: Dp,
    val bottomStart: Dp,
) {
    companion object {
        fun uniform(radius: Dp) = MorphingCornerRadii(radius, radius, radius, radius)
    }
}

@Composable
fun rememberMorphingRoundedShape(
    target: MorphingCornerRadii,
    animationSpec: FiniteAnimationSpec<Dp> = MaterialTheme.motionScheme.defaultSpatialSpec(),
): Shape {
    val topStart by animateDpAsState(target.topStart, animationSpec = animationSpec, label = "morph_ts")
    val topEnd by animateDpAsState(target.topEnd, animationSpec = animationSpec, label = "morph_te")
    val bottomEnd by animateDpAsState(target.bottomEnd, animationSpec = animationSpec, label = "morph_be")
    val bottomStart by animateDpAsState(target.bottomStart, animationSpec = animationSpec, label = "morph_bs")
    return RoundedCornerShape(topStart, topEnd, bottomEnd, bottomStart)
}

val ClimateDialCompactRadii = MorphingCornerRadii.uniform(56.dp)
val ClimateDialExpandedRadii = MorphingCornerRadii(
    topStart = 72.dp,
    topEnd = 32.dp,
    bottomEnd = 72.dp,
    bottomStart = 32.dp,
)

val MediaAlbumCompactRadii = MorphingCornerRadii(
    topStart = 48.dp,
    topEnd = 24.dp,
    bottomEnd = 48.dp,
    bottomStart = 24.dp,
)
val MediaAlbumExpandedRadii = MorphingCornerRadii(
    topStart = 32.dp,
    topEnd = 48.dp,
    bottomEnd = 32.dp,
    bottomStart = 48.dp,
)
