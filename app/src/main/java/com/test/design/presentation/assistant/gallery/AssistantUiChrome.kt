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

/** Shared semi-transparent surfaces for every gallery variant. */
internal object AssistantUiChrome {
    val Glass = Color(0xB3141820)
    val GlassLight = Color(0x991C222C)
    val GlassEdge = Color.White.copy(alpha = 0.14f)
    val Scrim = Color.Black.copy(alpha = 0.22f)
    val Accent = Color(0xFF8AB4F8)
    val AccentSoft = Color(0x668AB4F8)
    val OnGlass = Color(0xFFF1F3F4)
    val OnGlassMuted = Color(0xFF9AA0A6)
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
            .background(
                Brush.verticalGradient(
                    listOf(AssistantUiChrome.GlassLight, AssistantUiChrome.Glass),
                ),
            )
            .border(1.dp, AssistantUiChrome.GlassEdge, shape),
        content = { content() },
    )
}
