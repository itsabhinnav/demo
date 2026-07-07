package com.test.design.presentation.demos.motion

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MotionScheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.test.design.component.components.CustomSectionHeader
import com.test.design.component.core.RestrictedComponentPolicy
import com.test.design.component.core.currentDrivingUxState
import com.test.design.component.theme.OemSpacing
import com.test.design.presentation.demos.shared.DemoScaffold
import com.test.design.template.preview.AutomotivePreviews

@Composable
fun AaosExpressiveComponentsDemo(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var expressive by remember { mutableStateOf(true) }
    val drivingState = currentDrivingUxState()
    val animationsEnabled = RestrictedComponentPolicy.maxAnimationDurationMs(drivingState) > 0

    DemoScaffold(
        title = "Expressive Components",
        onBack = onBack,
        modifier = modifier,
        yellowContent = {
            MotionSchemeTogglePanel(
                expressive = expressive,
                onExpressiveChange = { expressive = it },
                title = "M3 MotionScheme",
                subtitle = "Tap components in the main area. Springs come from MaterialTheme.motionScheme.",
            )
            if (!animationsEnabled) {
                Text(
                    text = "Animations disabled while driving (AAOS policy). Park to preview motion.",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(OemSpacing.md),
                )
            }
        },
    ) {
        MaterialTheme(
            colorScheme = MaterialTheme.colorScheme,
            typography = MaterialTheme.typography,
            shapes = MaterialTheme.shapes,
            motionScheme = if (expressive) MotionScheme.expressive() else MotionScheme.standard(),
        ) {
            CustomSectionHeader(
                title = "Individual M3 components",
                subtitle = "1920×720 landscape · buttons, toggles, chips, tabs, progress, and cards use expressive spring physics when parked.",
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(520.dp)
                    .padding(horizontal = OemSpacing.md),
                horizontalArrangement = Arrangement.spacedBy(OemSpacing.md),
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(OemSpacing.xs),
                ) {
                    AaosMotionComponentColumn(
                        animationsEnabled = animationsEnabled,
                        column = MotionComponentColumn.Primary,
                    )
                }
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(OemSpacing.xs),
                ) {
                    AaosMotionComponentColumn(
                        animationsEnabled = animationsEnabled,
                        column = MotionComponentColumn.Secondary,
                    )
                }
            }
        }
    }
}

private enum class MotionComponentColumn { Primary, Secondary }

@Composable
private fun AaosMotionComponentColumn(
    animationsEnabled: Boolean,
    column: MotionComponentColumn,
) {
    when (column) {
        MotionComponentColumn.Primary -> {
            MotionPhysicsComponentsSection(
                animationsEnabled = animationsEnabled,
                sections = MotionPhysicsSectionSet.Primary,
            )
        }
        MotionComponentColumn.Secondary -> {
            MotionPhysicsComponentsSection(
                animationsEnabled = animationsEnabled,
                sections = MotionPhysicsSectionSet.Secondary,
            )
        }
    }
}

@AutomotivePreviews
@Composable
private fun AaosExpressiveComponentsDemoPreview() {
    com.test.design.component.theme.OemTheme {
        AaosExpressiveComponentsDemo(onBack = {})
    }
}
