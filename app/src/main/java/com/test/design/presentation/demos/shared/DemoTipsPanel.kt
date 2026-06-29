package com.test.design.presentation.demos.shared

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.test.design.component.components.CustomCard
import com.test.design.component.components.CustomSectionHeader
import com.test.design.component.theme.OemOnSurfaceVariant
import com.test.design.component.theme.OemSpacing

@Composable
fun DemoTipsPanel(
    tips: List<String>,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        CustomSectionHeader(title = "Tips")
        CustomCard(modifier = Modifier.padding(top = OemSpacing.sm)) {
            tips.forEach { tip ->
                Text(
                    text = "• $tip",
                    style = MaterialTheme.typography.bodyMedium,
                    color = OemOnSurfaceVariant,
                    modifier = Modifier.padding(vertical = OemSpacing.xs),
                )
            }
        }
    }
}
