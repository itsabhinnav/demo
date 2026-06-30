package com.test.design.presentation.demos

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.test.design.presentation.demos.adaptive.AdaptiveLayoutsDemo
import com.test.design.presentation.demos.compose.ComposeBasicsDemo
import com.test.design.presentation.demos.components.ComponentsGalleryDemo
import com.test.design.presentation.demos.designsystem.DesignSystemDemo
import com.test.design.presentation.demos.lists.ListsGridsDemo
import com.test.design.presentation.demos.restricted.RestrictedUxDemo
import com.test.design.presentation.demos.tabs.TabsDemo
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
        "components-gallery" -> ComponentsGalleryDemo(onBack = onNavigateBack, modifier = modifier)
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
