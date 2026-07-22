package com.test.design.presentation.assistant.gallery

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.test.design.presentation.assistant.AssistantTokens

/** Shared semi-transparent surfaces for every gallery variant. */
internal object AssistantUiChrome {
    /** Blackish glass — high enough opacity that glyphs stay crisp. */
    val Glass = AssistantTokens.Surface
    val GlassLight = AssistantTokens.SurfaceTop
    val GlassEdge = Color.White.copy(alpha = 0.16f)
    /** Dims the world behind so chrome / face are the focus. */
    val Scrim = AssistantTokens.Scrim
    val Accent = AssistantTokens.Accent
    val AccentSoft = AssistantTokens.PanelGlow
    val OnGlass = AssistantTokens.OnSurface
    val OnGlassMuted = AssistantTokens.OnSurfaceVariant
}

@Composable
internal fun GlassSurface(
    modifier: Modifier = Modifier,
    corner: Dp = 28.dp,
    content: @Composable () -> Unit,
) {
    val shape = RoundedCornerShape(corner)
    Box(
        modifier = modifier
            .clip(shape)
            // Solid blackish base first, then a slight vertical wash.
            .background(AssistantUiChrome.Glass, shape)
            .background(
                Brush.verticalGradient(
                    listOf(
                        AssistantUiChrome.GlassLight.copy(alpha = 0.55f),
                        Color.Transparent,
                    ),
                ),
            )
            .border(1.dp, AssistantUiChrome.GlassEdge, shape),
        content = { content() },
    )
}
