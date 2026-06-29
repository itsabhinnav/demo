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
            id = "driving-ux",
            title = "Driving UX & Restrictions",
            description = "Google Design for Driving — enlarged targets, UXR-blocked inputs, and glanceable restricted variants.",
            category = DemoCategory.DesignSystem,
            tagline = "Distraction-safe UI",
        ),
        FeatureDemo(
            id = "compose-basics",
            title = "Compose Basics",
            description = "State, recomposition, modifiers, and composable building blocks.",
            category = DemoCategory.Compose,
            tagline = "Jetpack Compose 101",
        ),
        FeatureDemo(
            id = "adaptive-layouts",
            title = "Adaptive Layouts",
            description = "Blue, green, and yellow zones that scale across 12.3\" to 15.3\" displays.",
            category = DemoCategory.Layouts,
            tagline = "Multi-display zones",
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
