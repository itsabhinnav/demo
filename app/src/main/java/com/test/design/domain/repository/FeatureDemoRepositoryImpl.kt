package com.test.design.domain.repository

import com.test.design.domain.model.DemoCategory
import com.test.design.domain.model.FeatureDemo

class FeatureDemoRepositoryImpl : FeatureDemoRepository {

    private val demos = listOf(
        FeatureDemo(
            id = "components-gallery",
            title = "Design System Components",
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
            title = "Component Playground",
            description = "Drag components onto a canvas, customize props and layout, save, and export JSON.",
            category = DemoCategory.Components,
            tagline = "Runtime screen builder",
        ),
    )

    override fun getAll(): List<FeatureDemo> = demos

    override fun findById(id: String): FeatureDemo? = demos.find { it.id == id }

    override fun getCategories(): List<DemoCategory> {
        val usedCategories = demos.map { it.category }.toSet()
        return DemoCategory.entries.filter { it == DemoCategory.All || it in usedCategories }
    }
}
