package com.test.design.presentation.assistant

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.dp
import com.test.design.presentation.ivi.dashboard.components.FloatingChromeBottomSpace
import kotlin.math.roundToInt

/**
 * Gallery-style corner bubble — bottom-end glass chip with face + prompt.
 * Used for non-blocking listening before the immersive fullscreen morph.
 *
 * Sits above [FloatingChromeBottomSpace] so the in-app floating dock does not cover it.
 */
@Composable
fun AssistantCornerBubble(
    mood: AssistantMood,
    prompt: String,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    onBoundsInRoot: ((left: Int, top: Int, right: Int, bottom: Int) -> Unit)? = null,
) {
    Box(modifier.fillMaxSize(), contentAlignment = Alignment.BottomEnd) {
        CornerBubbleGlass(
            modifier = Modifier
                .padding(
                    start = 20.dp,
                    end = 20.dp,
                    top = 20.dp,
                    bottom = 20.dp + FloatingChromeBottomSpace,
                )
                .width(260.dp)
                .then(
                    if (onBoundsInRoot != null) {
                        Modifier.onGloballyPositioned { coords ->
                            val b = coords.boundsInRoot()
                            onBoundsInRoot(
                                b.left.roundToInt(),
                                b.top.roundToInt(),
                                b.right.roundToInt(),
                                b.bottom.roundToInt(),
                            )
                        }
                    } else {
                        Modifier
                    },
                )
                .then(
                    if (onClick != null) {
                        Modifier.clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = onClick,
                        )
                    } else {
                        Modifier
                    },
                ),
        ) {
            Row(
                Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                AssistantFace(mood = mood, modifier = Modifier.size(52.dp))
                Text(
                    text = prompt.ifBlank { "Listening…" },
                    color = AssistantTokens.OnSurface,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1f),
                    maxLines = 3,
                )
            }
        }
    }
}

@Composable
private fun CornerBubbleGlass(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val shape = RoundedCornerShape(22.dp)
    Box(
        modifier = modifier
            .clip(shape)
            .background(AssistantTokens.Surface, shape)
            .background(
                Brush.verticalGradient(
                    listOf(
                        AssistantTokens.SurfaceTop.copy(alpha = 0.55f),
                        Color.Transparent,
                    ),
                ),
            )
            .border(1.dp, Color.White.copy(alpha = 0.16f), shape),
        content = { content() },
    )
}
