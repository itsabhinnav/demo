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

/** Dashboard widget card + detail surface corners for climate. */
val ClimateCardRestRadii = MorphingCornerRadii(
    topStart = 40.dp,
    topEnd = 16.dp,
    bottomEnd = 40.dp,
    bottomStart = 16.dp,
)
val ClimateCardActiveRadii = MorphingCornerRadii(
    topStart = 56.dp,
    topEnd = 20.dp,
    bottomEnd = 48.dp,
    bottomStart = 28.dp,
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

/** Dashboard widget card + detail surface corners for media. */
val MediaCardRestRadii = MorphingCornerRadii(
    topStart = 40.dp,
    topEnd = 16.dp,
    bottomEnd = 40.dp,
    bottomStart = 16.dp,
)
val MediaCardPlayingRadii = MorphingCornerRadii(
    topStart = 20.dp,
    topEnd = 52.dp,
    bottomEnd = 20.dp,
    bottomStart = 52.dp,
)

val DetailCardRestRadii = MorphingCornerRadii.uniform(36.dp)
val DetailCardExpandedRadii = MorphingCornerRadii(
    topStart = 44.dp,
    topEnd = 28.dp,
    bottomEnd = 44.dp,
    bottomStart = 28.dp,
)

@Composable
fun rememberClimateCardShape(active: Boolean): Shape = rememberMorphingRoundedShape(
    target = if (active) ClimateCardActiveRadii else ClimateCardRestRadii,
)

@Composable
fun rememberMediaCardShape(playing: Boolean): Shape = rememberMorphingRoundedShape(
    target = if (playing) MediaCardPlayingRadii else MediaCardRestRadii,
)

@Composable
fun rememberClimateDialShape(acEnabled: Boolean): Shape = rememberMorphingRoundedShape(
    target = if (acEnabled) ClimateDialExpandedRadii else ClimateDialCompactRadii,
)

@Composable
fun rememberMediaAlbumShape(playing: Boolean): Shape = rememberMorphingRoundedShape(
    target = if (playing) MediaAlbumExpandedRadii else MediaAlbumCompactRadii,
)
