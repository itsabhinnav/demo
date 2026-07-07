package com.test.design.presentation.demos.motion

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AssistChip
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.test.design.component.components.CustomSectionHeader
import com.test.design.component.theme.OemSpacing
import com.test.design.presentation.demos.shared.DemoScaffold
import com.test.design.template.preview.AutomotivePreviews

@Composable
fun AaosTransitionShowcaseDemo(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var pattern by remember { mutableStateOf(TransitionPattern.ContainerTransform) }
    var replayKey by remember { mutableIntStateOf(0) }

    DemoScaffold(
        title = "Transition Patterns",
        onBack = onBack,
        modifier = modifier,
        yellowContent = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(bottom = OemSpacing.md),
                verticalArrangement = Arrangement.spacedBy(OemSpacing.sm),
            ) {
                Text(
                    text = "Six M3 transition patterns for 1920×720 AAOS. Interact in the main area; easing and duration follow M3 specs.",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(horizontal = OemSpacing.md),
                )
                TransitionPattern.entries.forEach { item ->
                    AssistChip(
                        onClick = { pattern = item },
                        label = { Text(item.label()) },
                        modifier = Modifier.padding(horizontal = OemSpacing.md),
                    )
                }
                Text(
                    text = pattern.motionSpec(),
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(horizontal = OemSpacing.md),
                )
                FilledTonalButton(
                    onClick = { replayKey++ },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = OemSpacing.md),
                ) {
                    Text("Replay pattern")
                }
            }
        },
    ) {
        CustomSectionHeader(
            title = pattern.label(),
            subtitle = "Tap and navigate inside the stage — motion uses M3 emphasized easing for screen-scale transitions.",
        )
        AaosTransitionPatternContent(
            pattern = pattern,
            replayKey = replayKey,
            modifier = Modifier
                .fillMaxWidth()
                .height(480.dp)
                .padding(horizontal = OemSpacing.md),
        )
    }
}

@AutomotivePreviews
@Composable
private fun AaosTransitionShowcaseDemoPreview() {
    com.test.design.component.theme.OemTheme {
        AaosTransitionShowcaseDemo(onBack = {})
    }
}
