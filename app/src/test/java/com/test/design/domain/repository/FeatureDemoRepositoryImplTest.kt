package com.test.design.domain.repository

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FeatureDemoRepositoryImplTest {

    private val repository = FeatureDemoRepositoryImpl()

    private val routedDemoIds = setOf(
        "design-system",
        "theming-lab",
        "token-browser",
        "accessibility-audit",
        "figma-checklist",
        "expressive-motion",
        "components-gallery",
        "component-state-matrix",
        "component-specs",
        "component-playground",
        "flow-builder",
        "input-modality",
        "lists-grids",
        "tabs-demo",
        "adaptive-layouts",
        "driving-ux",
        "ev-dashboard",
        "software-update",
        "telematics",
    )

    @Test
    fun getAll_returnsUniqueIds() {
        val ids = repository.getAll().map { it.id }
        assertEquals(ids.size, ids.toSet().size)
    }

    @Test
    fun getAll_everyRegisteredDemoHasRouterEntry() {
        repository.getAll().forEach { demo ->
            assertTrue(
                "Missing router entry for ${demo.id}",
                demo.id in routedDemoIds,
            )
        }
    }

    @Test
    fun getAll_coversAllCategoriesExceptAll() {
        val categories = repository.getAll().map { it.category }.toSet()
        assertTrue(categories.contains(com.test.design.domain.model.DemoCategory.Layouts))
        assertTrue(categories.contains(com.test.design.domain.model.DemoCategory.Vehicle))
    }
}
