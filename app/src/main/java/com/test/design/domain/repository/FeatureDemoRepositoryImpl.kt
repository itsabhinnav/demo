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
            description = "Drag and drop components onto a live canvas to prototype screens at runtime. Hide the palette for full-screen preview.",
            category = DemoCategory.Components,
            tagline = "Runtime screen builder",
        ),
        FeatureDemo(
            id = "driving-ux",
            title = "Driving UX & Restrictions",
            description = "Google Design for Driving — enlarged targets, UXR-blocked inputs, and glanceable restricted variants.",
            category = DemoCategory.DesignSystem,
            tagline = "Distraction-safe UI",
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
        FeatureDemo(
            id = "ev-dashboard",
            title = "EV Dashboard",
            description = "Battery state, range metrics, drive modes, and charging actions for electric vehicles.",
            category = DemoCategory.Vehicle,
            tagline = "Battery & charging",
        ),
        FeatureDemo(
            id = "software-update",
            title = "Software Update",
            description = "OTA download progress, release notes, and install scheduling for in-vehicle firmware.",
            category = DemoCategory.Vehicle,
            tagline = "OTA updates",
        ),
        FeatureDemo(
            id = "telematics",
            title = "Telematics",
            description = "Live vehicle data, trip history, and alerts using connected-car telematics feeds.",
            category = DemoCategory.Vehicle,
            tagline = "Connected vehicle",
        ),
    )

    override fun getAll(): List<FeatureDemo> = demos

    override fun findById(id: String): FeatureDemo? = demos.find { it.id == id }

    override fun getCategories(): List<DemoCategory> = DemoCategory.entries
}
