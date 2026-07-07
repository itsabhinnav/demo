package com.test.design.presentation.demos.motion

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.test.design.component.components.CustomSectionHeader
import com.test.design.component.theme.OemSpacing
import com.test.design.presentation.demos.shared.DemoScaffold

private val EmphasizedDecelerate = CubicBezierEasing(0.05f, 0.7f, 0.1f, 1f)
private val EmphasizedAccelerate = CubicBezierEasing(0.3f, 0f, 0.8f, 0.15f)

@Composable
fun EasingDurationDemo(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var replayKey by remember { mutableIntStateOf(0) }
    var showDetails by remember { mutableStateOf(false) }

    DemoScaffold(
        title = "Easing & Duration",
        onBack = onBack,
        modifier = modifier,
        yellowContent = {
            Text(
                text = "Use shorter durations for local changes and longer for full-screen changes. Emphasized easing highlights major transitions.",
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(horizontal = OemSpacing.md),
            )
        },
    ) {
        CustomSectionHeader(
            title = "Applying easing and duration",
            subtitle = "Replay to compare micro, medium, and full-screen timing tiers.",
        )
        FilledTonalButton(
            onClick = { replayKey++ },
            modifier = Modifier.padding(horizontal = OemSpacing.md, vertical = OemSpacing.sm),
        ) {
            Text("Replay timing examples")
        }

        EasingBar(
            label = "Micro interaction · 120ms · emphasized accelerate",
            trigger = replayKey,
            durationMs = 120,
            easing = EmphasizedAccelerate,
            color = MaterialTheme.colorScheme.primary,
        )
        EasingBar(
            label = "Component transition · 260ms · standard",
            trigger = replayKey,
            durationMs = 260,
            easing = LinearOutSlowInEasing,
            color = MaterialTheme.colorScheme.tertiary,
        )
        EasingBar(
            label = "Full-screen transition · 420ms · emphasized decelerate",
            trigger = replayKey,
            durationMs = 420,
            easing = EmphasizedDecelerate,
            color = MaterialTheme.colorScheme.secondary,
        )

        CustomSectionHeader(
            title = "Enter and exit choreography",
            subtitle = "Details panel enters with emphasized deceleration and exits faster with acceleration.",
        )
        FilledTonalButton(
            onClick = { showDetails = !showDetails },
            modifier = Modifier.padding(horizontal = OemSpacing.md, vertical = OemSpacing.sm),
        ) {
            Text(if (showDetails) "Hide details panel" else "Show details panel")
        }
        AnimatedVisibility(
            visible = showDetails,
            enter = androidx.compose.animation.fadeIn(
                animationSpec = tween(durationMillis = 360, easing = EmphasizedDecelerate),
            ),
            exit = androidx.compose.animation.fadeOut(
                animationSpec = tween(durationMillis = 180, easing = FastOutLinearInEasing),
            ),
            modifier = Modifier.padding(horizontal = OemSpacing.md),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(20.dp))
                    .background(MaterialTheme.colorScheme.surfaceContainerHigh)
                    .padding(OemSpacing.md),
                verticalArrangement = Arrangement.spacedBy(OemSpacing.xs),
            ) {
                Text("Route preview", style = MaterialTheme.typography.titleSmall)
                Text(
                    "Forward navigation should open slower than back/close to preserve hierarchy and reduce visual noise while driving.",
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
}

@Composable
private fun EasingBar(
    label: String,
    trigger: Int,
    durationMs: Int,
    easing: androidx.compose.animation.core.Easing,
    color: Color,
) {
    var expanded by remember { mutableStateOf(false) }
    LaunchedEffect(trigger) {
        expanded = false
        expanded = true
    }
    val widthFactor by animateFloatAsState(
        targetValue = if (expanded) 1f else 0.1f,
        animationSpec = tween(durationMillis = durationMs, easing = easing),
        label = label,
    )

    Column(
        modifier = Modifier.padding(horizontal = OemSpacing.md, vertical = OemSpacing.sm),
        verticalArrangement = Arrangement.spacedBy(OemSpacing.xs),
    ) {
        Text(label, style = MaterialTheme.typography.bodyMedium)
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .height(16.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceContainer),
        ) {
            Box(
                modifier = Modifier
                    .size(width = maxWidth * widthFactor, height = 16.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(color),
            )
        }
    }
}
