package com.test.design.presentation.demos.accessibility

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.test.design.component.components.CustomCard
import com.test.design.component.components.CustomSectionHeader
import com.test.design.component.core.DrivingUxState
import com.test.design.component.core.RestrictedComponentPolicy
import com.test.design.component.core.currentDrivingUxState
import com.test.design.component.core.currentTouchTarget
import com.test.design.component.theme.OemOnSurfaceVariant
import com.test.design.component.theme.OemSpacing
import com.test.design.component.theme.OemSuccess
import com.test.design.component.theme.OemWarning
import com.test.design.component.tokens.DesignTokens
import com.test.design.presentation.demos.shared.DemoScaffold

private enum class AuditStatus {
    Pass,
    ManualReview,
    Fail,
}

private data class AuditResult(
    val label: String,
    val detail: String,
    val status: AuditStatus,
)

@Composable
fun AccessibilityAuditDemo(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val drivingState = currentDrivingUxState()
    val touchTarget = currentTouchTarget().value.toInt()
    val minRequired = when (drivingState) {
        DrivingUxState.Parked -> DesignTokens.minTouchTargetDp
        DrivingUxState.Driving -> DesignTokens.drivingTouchTargetDp
        DrivingUxState.Restricted -> DesignTokens.restrictedTouchTargetDp
    }

    val results = listOf(
        AuditResult(
            label = "Touch target size",
            detail = "Current ${touchTarget}dp · Required ${minRequired}dp minimum",
            status = if (touchTarget >= minRequired) AuditStatus.Pass else AuditStatus.Fail,
        ),
        AuditResult(
            label = "Contrast ratio",
            detail = "Verify on device against ${DesignTokens.minContrastRatio}:1 AAOS minimum",
            status = AuditStatus.ManualReview,
        ),
        AuditResult(
            label = "Body text size",
            detail = "OemTypography bodyLarge meets ${DesignTokens.minBodyTextSp}sp minimum",
            status = AuditStatus.Pass,
        ),
        AuditResult(
            label = "Keyboard UXR policy",
            detail = RestrictedComponentPolicy.keyboardBlockedMessage(drivingState)
                ?: "Keyboard allowed while parked",
            status = if (drivingState == DrivingUxState.Parked || !RestrictedComponentPolicy.allowsKeyboardInput(drivingState)) {
                AuditStatus.Pass
            } else {
                AuditStatus.Fail
            },
        ),
        AuditResult(
            label = "Animation policy",
            detail = "Max ${RestrictedComponentPolicy.maxAnimationDurationMs(drivingState)}ms in ${drivingState.name}",
            status = if (RestrictedComponentPolicy.maxAnimationDurationMs(drivingState) <= DesignTokens.maxDrivingAnimationMs) {
                AuditStatus.Pass
            } else {
                AuditStatus.Fail
            },
        ),
        AuditResult(
            label = "Secondary action policy",
            detail = if (RestrictedComponentPolicy.allowsSecondaryActions(drivingState)) {
                "Secondary buttons allowed in ${drivingState.name}"
            } else {
                "Secondary actions hidden in Restricted UXR"
            },
            status = AuditStatus.Pass,
        ),
    )

    val passCount = results.count { it.status == AuditStatus.Pass }

    DemoScaffold(
        title = "Accessibility Audit",
        onBack = onBack,
        yellowContent = {},
    ) {
        CustomSectionHeader(
            title = "Live AAOS Audit",
            subtitle = "${drivingState.name} · $passCount automated checks passing",
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
            AuditStatusIcon(status = result.status)
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
private fun AuditStatusIcon(status: AuditStatus) {
    val icon: ImageVector = when (status) {
        AuditStatus.Pass -> Icons.Default.CheckCircle
        AuditStatus.ManualReview -> Icons.Default.Info
        AuditStatus.Fail -> Icons.Default.Warning
    }
    val tint = when (status) {
        AuditStatus.Pass -> OemSuccess
        AuditStatus.ManualReview -> OemOnSurfaceVariant
        AuditStatus.Fail -> OemWarning
    }
    Icon(
        imageVector = icon,
        contentDescription = status.name,
        tint = tint,
    )
}
