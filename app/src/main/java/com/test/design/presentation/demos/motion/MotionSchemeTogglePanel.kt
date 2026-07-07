package com.test.design.presentation.demos.motion

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.test.design.component.motion.OemMotionScheme
import com.test.design.component.theme.OemSpacing

@Composable
fun MotionSchemeTogglePanel(
    expressive: Boolean,
    onExpressiveChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    title: String = "Motion scheme",
    subtitle: String = "Compare Standard vs Expressive spring physics on M3 components.",
) {
    Column(
        modifier = modifier.padding(horizontal = OemSpacing.md),
        verticalArrangement = Arrangement.spacedBy(OemSpacing.sm),
    ) {
        Text(text = title, style = MaterialTheme.typography.titleSmall)
        Text(text = subtitle, style = MaterialTheme.typography.bodySmall)
        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            listOf("Standard", "Expressive").forEachIndexed { index, label ->
                SegmentedButton(
                    selected = expressive == (index == 1),
                    onClick = { onExpressiveChange(index == 1) },
                    shape = SegmentedButtonDefaults.itemShape(index = index, count = 2),
                    icon = {},
                    label = { Text(label) },
                )
            }
        }
        val spatial = if (expressive) {
            OemMotionScheme.expressivePhysics(OemMotionScheme.SpringToken.DefaultSpatial)
        } else {
            OemMotionScheme.standardPhysics(OemMotionScheme.SpringToken.DefaultSpatial)
        }
        val effects = if (expressive) {
            OemMotionScheme.expressivePhysics(OemMotionScheme.SpringToken.DefaultEffects)
        } else {
            OemMotionScheme.standardPhysics(OemMotionScheme.SpringToken.DefaultEffects)
        }
        Text(
            text = "Spatial: ${spatial.label()}",
            style = MaterialTheme.typography.labelMedium,
        )
        Text(
            text = "Effects: ${effects.label()}",
            style = MaterialTheme.typography.labelMedium,
        )
    }
}
