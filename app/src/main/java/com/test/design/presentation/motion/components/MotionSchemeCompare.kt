package com.test.design.presentation.motion.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.test.design.core.motion.AppMotionScheme
import com.test.design.core.motion.LocalEffectiveMotionScheme
import com.test.design.theme.CarDesignTokens
import com.test.design.theme.ExpressiveShapes
import com.test.design.theme.toMotionScheme
import kotlin.math.roundToInt

@Composable
fun MotionSchemeCompare(modifier: Modifier = Modifier) {
    var trigger by remember { mutableIntStateOf(0) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(CarDesignTokens.ContentPadding),
        verticalArrangement = Arrangement.spacedBy(CarDesignTokens.SectionSpacing),
    ) {
        Text(
            text = "A/B compare",
            style = MaterialTheme.typography.headlineMedium,
        )
        Text(
            text = "Standard vs Expressive side-by-side. Tap Run to animate both panels with the same gesture.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Button(
            onClick = { trigger++ },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Run comparison", style = MaterialTheme.typography.labelLarge)
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(CarDesignTokens.TouchTargetSpacing),
        ) {
            ComparePanel(
                label = AppMotionScheme.Standard.label,
                scheme = AppMotionScheme.Standard,
                trigger = trigger,
                modifier = Modifier.weight(1f),
            )
            ComparePanel(
                label = AppMotionScheme.Expressive.label,
                scheme = AppMotionScheme.Expressive,
                trigger = trigger,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun ComparePanel(
    label: String,
    scheme: AppMotionScheme,
    trigger: Int,
    modifier: Modifier = Modifier,
) {
    CompositionLocalProvider(LocalEffectiveMotionScheme provides scheme) {
        androidx.compose.material3.MaterialTheme(
            colorScheme = MaterialTheme.colorScheme,
            typography = MaterialTheme.typography,
            shapes = MaterialTheme.shapes,
            motionScheme = scheme.toMotionScheme(),
        ) {
            val offset by animateDpAsState(
                targetValue = if (trigger % 2 == 0) 8.dp else 120.dp,
                animationSpec = MaterialTheme.motionScheme.defaultSpatialSpec(),
                label = "compare_$label",
            )
            Card(
                modifier = modifier.height(200.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                ),
                shape = ExpressiveShapes.medium,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(CarDesignTokens.TouchTargetSpacing),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(label, style = MaterialTheme.typography.titleMedium)
                    Text(
                        "Spatial spring",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .clip(ExpressiveShapes.small)
                            .background(MaterialTheme.colorScheme.surface),
                    ) {
                        Box(
                            modifier = Modifier
                                .offset { IntOffset(offset.roundToPx(), 24.dp.roundToPx()) }
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary),
                        )
                    }
                }
            }
        }
    }
}
