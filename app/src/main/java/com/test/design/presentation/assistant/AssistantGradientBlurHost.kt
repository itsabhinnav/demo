package com.test.design.presentation.assistant

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Max blur at the bottom of the host UI under the immersive assistant.
 * Top stays sharp (mask fades blur to zero).
 */
val AssistantBackdropBlur = 36.dp

/**
 * Host content stays sharp on top; a second pass is blurred and masked so
 * blur strength ramps from none (top) → full (bottom).
 */
@Composable
fun AssistantGradientBlurHost(
    blurred: Boolean,
    modifier: Modifier = Modifier,
    maxBlur: Dp = AssistantBackdropBlur,
    content: @Composable () -> Unit,
) {
    Box(modifier = modifier.fillMaxSize()) {
        content()
        if (blurred) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }
                    .drawWithContent {
                        drawContent()
                        drawRect(
                            brush = Brush.verticalGradient(
                                colorStops = arrayOf(
                                    0.00f to Color.Transparent,
                                    0.40f to Color.Transparent,
                                    0.62f to Color.White.copy(alpha = 0.35f),
                                    0.82f to Color.White.copy(alpha = 0.85f),
                                    1.00f to Color.White,
                                ),
                            ),
                            blendMode = BlendMode.DstIn,
                        )
                    }
                    .blur(
                        radius = maxBlur,
                        edgeTreatment = BlurredEdgeTreatment.Unbounded,
                    ),
            ) {
                content()
            }
        }
    }
}
