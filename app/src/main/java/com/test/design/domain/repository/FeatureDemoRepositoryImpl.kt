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
            title = "Expressive Components",
            description = "M3 MotionScheme springs on individual Jetpack components — compare Standard vs Expressive in 1920×720 landscape.",
            category = DemoCategory.DesignSystem,
            tagline = "Component spring physics",
        ),
        FeatureDemo(
            id = "component-playground",
            title = "Playground",
            description = "Drag components onto a canvas, customize props and layout, save, and export JSON.",
            category = DemoCategory.Components,
            tagline = "Runtime screen builder",
        ),
        FeatureDemo(
            id = "motion-transition-patterns",
            title = "Transition Patterns",
            description = "Six M3 patterns for AAOS — container transform, forward/back, lateral, top level, enter/exit, and skeleton loaders with easing specs.",
            category = DemoCategory.DesignSystem,
            tagline = "Screen transitions & easing",
        ),
    )

    override fun getAll(): List<FeatureDemo> = demos

    override fun findById(id: String): FeatureDemo? = demos.find { it.id == id }

    override fun getCategories(): List<DemoCategory> {
        val usedCategories = demos.map { it.category }.toSet()
        return DemoCategory.entries.filter { it == DemoCategory.All || it in usedCategories }
    }
}
