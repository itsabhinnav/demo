package com.test.design.presentation.demo

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.test.design.component.components.ButtonStyle
import com.test.design.component.components.CustomButton
import com.test.design.component.components.CustomSectionHeader
import com.test.design.component.components.CustomTopBar
import com.test.design.component.theme.OemOnSurfaceVariant
import com.test.design.component.theme.OemSpacing
import com.test.design.template.AutomotiveDashboardTemplate

@Composable
fun UnknownDemoScreen(
    demoId: String,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AutomotiveDashboardTemplate(
        modifier = modifier,
        blueZone = {
            CustomTopBar(
                title = "Demo Not Found",
                showBack = true,
                onBackClick = onNavigateBack,
            )
        },
        greenZone = {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(OemSpacing.md),
            ) {
                CustomSectionHeader(
                    title = "Unknown demo",
                    subtitle = "No registered feature matches this deep link ID.",
                )
                Text(
                    text = "ID: $demoId",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(vertical = OemSpacing.md),
                )
                Text(
                    text = "Check the home catalog or verify the oemdesign://demo/{id} link.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = OemOnSurfaceVariant,
                )
                CustomButton(
                    text = "Back to home",
                    onClick = onNavigateBack,
                    style = ButtonStyle.Primary,
                    modifier = Modifier.padding(top = OemSpacing.lg),
                )
            }
        },
        yellowZone = {},
    )
}
