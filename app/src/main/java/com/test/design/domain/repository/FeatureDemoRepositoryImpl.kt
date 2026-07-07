package com.test.design.domain.repository

import com.test.design.domain.model.DemoCategory
import com.test.design.domain.model.FeatureDemo

class FeatureDemoRepositoryImpl : FeatureDemoRepository {

    private val demos = listOf(
        FeatureDemo(
            id = "design-system",
            title = "Design System",
            description = "Oem color palette, typography, spacing, and shape tokens built for AAOS.",
            category = DemoCategory.DesignSystem,
            tagline = "OEM tokens & theming",
        ),
        FeatureDemo(
            id = "components-gallery",
            title = "Components Gallery",
            description = "Interactive showcase of buttons, cards, chips, lists, tabs, and images.",
            category = DemoCategory.Components,
            tagline = "Reusable UI kit",
        ),
        FeatureDemo(
            id = "expressive-motion",
            title = "Motion Physics",
            description = "OEM-tunable motion physics — list fling, progress fills, and M3 spring specs with live sidebar controls.",
            category = DemoCategory.DesignSystem,
            tagline = "OEM motion tuning",
        ),
        FeatureDemo(
            id = "component-playground",
            title = "Playground",
            description = "Drag components onto a canvas, customize props and layout, save, and export JSON.",
            category = DemoCategory.Components,
            tagline = "Runtime screen builder",
        ),
        FeatureDemo(
            id = "motion-easing-duration",
            title = "Easing & Duration",
            description = "Compare M3 easing curves and duration tiers for micro, component, and full-screen transitions.",
            category = DemoCategory.DesignSystem,
            tagline = "Timing in real use cases",
        ),
        FeatureDemo(
            id = "motion-transition-patterns",
            title = "Transition Patterns",
            description = "Forward/back, lateral movement, card-to-full-screen transform, enter/exit, and skeleton loader choreography.",
            category = DemoCategory.DesignSystem,
            tagline = "Screen and container transitions",
        ),
    )

    override fun getAll(): List<FeatureDemo> = demos

    override fun findById(id: String): FeatureDemo? = demos.find { it.id == id }

    override fun getCategories(): List<DemoCategory> {
        val usedCategories = demos.map { it.category }.toSet()
        return DemoCategory.entries.filter { it == DemoCategory.All || it in usedCategories }
    }
}
