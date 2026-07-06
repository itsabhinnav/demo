package com.test.design.presentation.demos.motion

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.test.design.component.components.CustomSectionHeader
import com.test.design.component.core.RestrictedComponentPolicy
import com.test.design.component.core.currentDrivingUxState
import com.test.design.component.motion.OemMotionPhysicsConfig
import com.test.design.component.motion.toMotionScheme
import com.test.design.presentation.demos.shared.DemoScaffold

@Composable
fun ExpressiveMotionDemo(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var config by remember { mutableStateOf(OemMotionPhysicsConfig.Default) }
    val drivingState = currentDrivingUxState()
    val animationsEnabled = RestrictedComponentPolicy.maxAnimationDurationMs(drivingState) > 0

    DemoScaffold(
        title = "Motion Physics",
        onBack = onBack,
        modifier = modifier,
        yellowContent = {
            MotionPhysicsConfigPanel(
                config = config,
                onConfigChange = { config = it },
                animationsEnabled = animationsEnabled,
            )
        },
    ) {
        MaterialTheme(
            colorScheme = MaterialTheme.colorScheme,
            typography = MaterialTheme.typography,
            shapes = MaterialTheme.shapes,
            motionScheme = config.toMotionScheme(),
        ) {
            CustomSectionHeader(
                title = "Motion components",
                subtitle = "Tap, toggle, and scroll — physics come from MotionScheme (tune in sidebar)",
            )
            MotionPhysicsComponentsSection(animationsEnabled = animationsEnabled)
        }
    }
}
