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
            id = "theming-lab",
            title = "Theming Lab",
            description = "Live spacing and typography scale preview to explore token changes before committing.",
            category = DemoCategory.DesignSystem,
            tagline = "Live token preview",
        ),
        FeatureDemo(
            id = "token-browser",
            title = "Token Browser",
            description = "Browse spacing, touch targets, layout weights, and AAOS constants with usage notes.",
            category = DemoCategory.DesignSystem,
            tagline = "Design token catalog",
        ),
        FeatureDemo(
            id = "accessibility-audit",
            title = "Accessibility Audit",
            description = "Live checks for touch targets, contrast minimums, and text size against AAOS guidelines.",
            category = DemoCategory.DesignSystem,
            tagline = "AAOS compliance checks",
        ),
        FeatureDemo(
            id = "figma-checklist",
            title = "Figma vs Android",
            description = "Per-component checklist of behaviors Figma cannot simulate — UXR, motion, input modality.",
            category = DemoCategory.DesignSystem,
            tagline = "What Figma can't show",
        ),
        FeatureDemo(
            id = "expressive-motion",
            title = "Expressive Motion",
            description = "Material 3 MotionScheme spring physics — standard vs expressive, spatial and effects specs.",
            category = DemoCategory.DesignSystem,
            tagline = "MotionScheme physics",
        ),
        FeatureDemo(
            id = "components-gallery",
            title = "Components Gallery",
            description = "Interactive showcase of buttons, cards, chips, lists, tabs, and images.",
            category = DemoCategory.Components,
            tagline = "Reusable UI kit",
        ),
        FeatureDemo(
            id = "component-state-matrix",
            title = "Component State Matrix",
            description = "Every key component across Parked, Driving, and Restricted states in one grid.",
            category = DemoCategory.Components,
            tagline = "Variants × UXR states",
        ),
        FeatureDemo(
            id = "component-specs",
            title = "Component Spec Sheets",
            description = "Props, variants, driving restrictions, and deep links for each Custom component.",
            category = DemoCategory.Components,
            tagline = "Design ↔ eng alignment",
        ),
        FeatureDemo(
            id = "component-playground",
            title = "Component Playground",
            description = "Drag components onto a canvas, customize props and layout, save, and export JSON.",
            category = DemoCategory.Components,
            tagline = "Runtime screen builder",
        ),
        FeatureDemo(
            id = "flow-builder",
            title = "Flow Builder",
            description = "Compose multi-screen flows with transitions, save locally, and export for handoff.",
            category = DemoCategory.Components,
            tagline = "Multi-screen flows",
        ),
        FeatureDemo(
            id = "input-modality",
            title = "Input Modality Lab",
            description = "Touch target scaling, rotary focus order, keyboard blocking, and voice search patterns.",
            category = DemoCategory.Components,
            tagline = "Touch & rotary UX",
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
            id = "adaptive-layouts",
            title = "Adaptive Layouts",
            description = "Blue, green, and yellow zone architecture with live display profile metrics.",
            category = DemoCategory.Layouts,
            tagline = "Zone architecture",
        ),
        FeatureDemo(
            id = "driving-ux",
            title = "Driving UX & Restrictions",
            description = "Simulate Parked, Driving, and Restricted UXR with live component behavior.",
            category = DemoCategory.Vehicle,
            tagline = "CarUxRestrictions",
        ),
        FeatureDemo(
            id = "ev-dashboard",
            title = "EV Dashboard",
            description = "Battery, drive modes, and charging scenario composed with OEM components.",
            category = DemoCategory.Vehicle,
            tagline = "EV scenario",
        ),
        FeatureDemo(
            id = "software-update",
            title = "Software Update",
            description = "OTA update flow with progress, confirmation dialog, and restricted-mode behavior.",
            category = DemoCategory.Vehicle,
            tagline = "OTA scenario",
        ),
        FeatureDemo(
            id = "telematics",
            title = "Telematics",
            description = "Trips, alerts, and live metrics in an automotive dashboard composition.",
            category = DemoCategory.Vehicle,
            tagline = "Connected vehicle",
        ),
    )

    override fun getAll(): List<FeatureDemo> = demos

    override fun findById(id: String): FeatureDemo? = demos.find { it.id == id }

    override fun getCategories(): List<DemoCategory> = DemoCategory.entries
}
