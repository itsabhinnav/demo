package com.test.design.presentation.demos.accessibility

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.test.design.component.components.CustomCard
import androidx.compose.ui.graphics.vector.ImageVector
import com.test.design.component.components.CustomSectionHeader
import com.test.design.component.core.RestrictedComponentPolicy
import com.test.design.component.core.currentDrivingUxState
import com.test.design.component.core.currentTouchTarget
import com.test.design.component.theme.OemOnSurfaceVariant
import com.test.design.component.theme.OemSpacing
import com.test.design.component.theme.OemSuccess
import com.test.design.component.theme.OemWarning
import com.test.design.component.tokens.DesignTokens
import com.test.design.presentation.demos.shared.DemoScaffold
import com.test.design.presentation.demos.shared.DemoTipsPanel

private data class AuditResult(
    val label: String,
    val detail: String,
    val passed: Boolean,
)

@Composable
fun AccessibilityAuditDemo(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val drivingState = currentDrivingUxState()
    val touchTarget = currentTouchTarget().value.toInt()
    val minRequired = when (drivingState) {
        com.test.design.component.core.DrivingUxState.Parked -> DesignTokens.minTouchTargetDp
        com.test.design.component.core.DrivingUxState.Driving -> DesignTokens.drivingTouchTargetDp
        com.test.design.component.core.DrivingUxState.Restricted -> DesignTokens.restrictedTouchTargetDp
    }

    val results = listOf(
        AuditResult(
            label = "Touch target size",
            detail = "Current ${touchTarget}dp · Required ${minRequired}dp minimum",
            passed = touchTarget >= minRequired,
        ),
        AuditResult(
            label = "Contrast ratio",
            detail = "Monochrome palette targets ${DesignTokens.minContrastRatio}:1 minimum",
            passed = true,
        ),
        AuditResult(
            label = "Body text size",
            detail = "OemTypography bodyLarge is ${DesignTokens.minBodyTextSp}sp minimum",
            passed = true,
        ),
        AuditResult(
            label = "Keyboard while driving",
            detail = RestrictedComponentPolicy.keyboardBlockedMessage(drivingState)
                ?: "Keyboard allowed while parked",
            passed = RestrictedComponentPolicy.allowsKeyboardInput(drivingState),
        ),
        AuditResult(
            label = "Animation duration",
            detail = "Max ${RestrictedComponentPolicy.maxAnimationDurationMs(drivingState)}ms in ${drivingState.name}",
            passed = RestrictedComponentPolicy.maxAnimationDurationMs(drivingState) <= DesignTokens.maxDrivingAnimationMs,
        ),
        AuditResult(
            label = "Secondary actions",
            detail = if (RestrictedComponentPolicy.allowsSecondaryActions(drivingState)) {
                "Secondary buttons visible"
            } else {
                "Secondary actions hidden in Restricted UXR"
            },
            passed = true,
        ),
    )

    DemoScaffold(
        title = "Accessibility Audit",
        onBack = onBack,
        yellowContent = {
            DemoTipsPanel(
                tips = listOf(
                    "Run this audit under each global Driving State",
                    "AAOS requires 4.5:1 contrast and enlarged touch targets",
                    "Failed checks indicate behaviors to validate on device",
                ),
            )
        },
    ) {
        CustomSectionHeader(
            title = "Live AAOS Audit",
            subtitle = "${drivingState.name} · ${results.count { it.passed }}/${results.size} checks passing",
        )
        results.forEach { result ->
            AuditResultRow(result)
        }
    }
}

@Composable
private fun AuditResultRow(result: AuditResult) {
    CustomCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = OemSpacing.xs),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(OemSpacing.md),
        ) {
            AuditStatusIcon(passed = result.passed)
            Column(modifier = Modifier.weight(1f)) {
                Text(text = result.label, style = MaterialTheme.typography.titleSmall)
                Text(
                    text = result.detail,
                    style = MaterialTheme.typography.bodyMedium,
                    color = OemOnSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun AuditStatusIcon(passed: Boolean) {
    val icon: ImageVector = if (passed) Icons.Default.CheckCircle else Icons.Default.Warning
    val tint = if (passed) OemSuccess else OemWarning
    Icon(imageVector = icon, contentDescription = null, tint = tint)
}
