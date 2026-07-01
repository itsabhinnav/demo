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
            id = "component-playground",
            title = "Component Playground",
            description = "Drag components onto a canvas, select each instance to customize labels, states, and layout, then save your screen design.",
            category = DemoCategory.Components,
            tagline = "Runtime screen builder",
        ),
        FeatureDemo(
            id = "lists-grids",
            title = "Lists & Grids",
            description = "Automotive-optimized scrolling lists and responsive grid layouts.",
            category = DemoCategory.Components,
            tagline = "Scrolling content",
        ),
        FeatureDemo(
            id = "tabs-demo",
            title = "Tabs & Navigation",
            description = "Top tab patterns, scrollable tabs, and in-car navigation UX.",
            category = DemoCategory.Components,
            tagline = "Navigation patterns",
        ),
    )

    override fun getAll(): List<FeatureDemo> = demos

    override fun findById(id: String): FeatureDemo? = demos.find { it.id == id }

    override fun getCategories(): List<DemoCategory> = DemoCategory.entries
}
