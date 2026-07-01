package com.test.design.presentation.demos.tokens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.test.design.component.components.CustomCard
import com.test.design.component.components.CustomSectionHeader
import com.test.design.component.components.CustomSpacingSample
import com.test.design.component.theme.OemOnSurfaceVariant
import com.test.design.component.theme.OemSpacing
import com.test.design.component.tokens.DesignTokens
import com.test.design.presentation.demos.shared.DemoScaffold
import com.test.design.presentation.demos.shared.DemoTipsPanel

private data class TokenEntry(
    val name: String,
    val value: String,
    val usage: String,
)

@Composable
fun TokenBrowserDemo(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    DemoScaffold(
        title = "Token Browser",
        onBack = onBack,
        yellowContent = {
            DemoTipsPanel(
                tips = listOf(
                    "Tokens live in component/theme and DesignTokens.kt",
                    "Use consistent spacing scale — avoid arbitrary dp values",
                    "Layout weights define the green/yellow column split",
                ),
            )
        },
    ) {
        CustomSectionHeader(title = "Spacing Scale", subtitle = "OemSpacing xs through xl")
        CustomSpacingSample("xs", OemSpacing.xs)
        CustomSpacingSample("sm", OemSpacing.sm)
        CustomSpacingSample("md", OemSpacing.md)
        CustomSpacingSample("lg", OemSpacing.lg)
        CustomSpacingSample("xl", OemSpacing.xl)

        CustomSectionHeader(title = "AAOS Constants", subtitle = "Automotive-specific design rules")
        TokenCard(
            entries = listOf(
                TokenEntry("minContrastRatio", "${DesignTokens.minContrastRatio}:1", "Text on surfaces"),
                TokenEntry("minTouchTargetDp", "${DesignTokens.minTouchTargetDp}dp", "Parked baseline"),
                TokenEntry("drivingTouchTargetDp", "${DesignTokens.drivingTouchTargetDp}dp", "Driving UXR"),
                TokenEntry("restrictedTouchTargetDp", "${DesignTokens.restrictedTouchTargetDp}dp", "Strict UXR"),
                TokenEntry("minBodyTextSp", "${DesignTokens.minBodyTextSp}sp", "Body minimum"),
                TokenEntry("minCaptionTextSp", "${DesignTokens.minCaptionTextSp}sp", "Caption minimum"),
                TokenEntry("maxDrivingAnimationMs", "${DesignTokens.maxDrivingAnimationMs}ms", "Parked anim cap"),
                TokenEntry("restrictedAnimationMs", "${DesignTokens.restrictedAnimationMs}ms", "Driving anim"),
            ),
        )

        CustomSectionHeader(title = "Layout Tokens", subtitle = "Dashboard zone architecture")
        TokenCard(
            entries = listOf(
                TokenEntry("leftColumnWeight", "${DesignTokens.leftColumnWeight}", "Green zone width"),
                TokenEntry("rightColumnWeight", "${DesignTokens.rightColumnWeight}", "Yellow zone width"),
                TokenEntry("blueZoneHeightFraction", "${DesignTokens.blueZoneHeightFraction}", "Top bar height"),
            ),
        )

        CustomSectionHeader(title = "Component Categories", subtitle = "Playground and gallery groupings")
        Text(
            text = DesignTokens.componentCategories.joinToString(" · "),
            style = MaterialTheme.typography.bodyMedium,
            color = OemOnSurfaceVariant,
            modifier = Modifier.padding(vertical = OemSpacing.sm),
        )
    }
}

@Composable
private fun TokenCard(entries: List<TokenEntry>) {
    CustomCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = OemSpacing.sm),
    ) {
        Column {
            entries.forEach { entry ->
                Text(
                    text = entry.name,
                    style = MaterialTheme.typography.titleSmall,
                    modifier = Modifier.padding(top = OemSpacing.xs),
                )
                Text(
                    text = "${entry.value} — ${entry.usage}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = OemOnSurfaceVariant,
                )
            }
        }
    }
}
