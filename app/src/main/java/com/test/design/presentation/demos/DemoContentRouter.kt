package com.test.design.presentation.demos

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.test.design.presentation.demos.accessibility.AccessibilityAuditDemo
import com.test.design.presentation.demos.adaptive.AdaptiveLayoutsDemo
import com.test.design.presentation.demos.checklist.FigmaChecklistDemo
import com.test.design.presentation.demos.compose.ComposeBasicsDemo
import com.test.design.presentation.demos.components.ComponentsGalleryDemo
import com.test.design.presentation.demos.flow.FlowBuilderDemo
import com.test.design.presentation.demos.input.InputModalityDemo
import com.test.design.presentation.demos.matrix.ComponentStateMatrixDemo
import com.test.design.presentation.demos.playground.ComponentPlaygroundDemo
import com.test.design.presentation.demos.designsystem.DesignSystemDemo
import com.test.design.presentation.demos.lists.ListsGridsDemo
import com.test.design.presentation.demos.motion.ExpressiveMotionDemo
import com.test.design.presentation.demos.restricted.RestrictedUxDemo
import com.test.design.presentation.demos.specs.ComponentSpecsDemo
import com.test.design.presentation.demos.tabs.TabsDemo
import com.test.design.presentation.demos.theming.ThemingLabDemo
import com.test.design.presentation.demos.tokens.TokenBrowserDemo
import com.test.design.presentation.demos.vehicle.EvDemo
import com.test.design.presentation.demos.vehicle.SoftwareUpdateDemo
import com.test.design.presentation.demos.vehicle.TelematicsDemo

@Composable
fun DemoContentRouter(
    demoId: String,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    when (demoId) {
        "design-system" -> DesignSystemDemo(onBack = onNavigateBack, modifier = modifier)
        "theming-lab" -> ThemingLabDemo(onBack = onNavigateBack, modifier = modifier)
        "token-browser" -> TokenBrowserDemo(onBack = onNavigateBack, modifier = modifier)
        "accessibility-audit" -> AccessibilityAuditDemo(onBack = onNavigateBack, modifier = modifier)
        "figma-checklist" -> FigmaChecklistDemo(onBack = onNavigateBack, modifier = modifier)
        "expressive-motion" -> ExpressiveMotionDemo(onBack = onNavigateBack, modifier = modifier)
        "components-gallery" -> ComponentsGalleryDemo(onBack = onNavigateBack, modifier = modifier)
        "component-state-matrix" -> ComponentStateMatrixDemo(onBack = onNavigateBack, modifier = modifier)
        "component-specs" -> ComponentSpecsDemo(onBack = onNavigateBack, modifier = modifier)
        "component-playground" -> ComponentPlaygroundDemo(onBack = onNavigateBack, modifier = modifier)
        "flow-builder" -> FlowBuilderDemo(onBack = onNavigateBack, modifier = modifier)
        "input-modality" -> InputModalityDemo(onBack = onNavigateBack, modifier = modifier)
        "compose-basics" -> ComposeBasicsDemo(onBack = onNavigateBack, modifier = modifier)
        "adaptive-layouts" -> AdaptiveLayoutsDemo(onBack = onNavigateBack, modifier = modifier)
        "lists-grids" -> ListsGridsDemo(onBack = onNavigateBack, modifier = modifier)
        "tabs-demo" -> TabsDemo(onBack = onNavigateBack, modifier = modifier)
        "driving-ux" -> RestrictedUxDemo(onBack = onNavigateBack, modifier = modifier)
        "ev-dashboard" -> EvDemo(onBack = onNavigateBack, modifier = modifier)
        "software-update" -> SoftwareUpdateDemo(onBack = onNavigateBack, modifier = modifier)
        "telematics" -> TelematicsDemo(onBack = onNavigateBack, modifier = modifier)
        else -> DesignSystemDemo(onBack = onNavigateBack, modifier = modifier)
    }
}
