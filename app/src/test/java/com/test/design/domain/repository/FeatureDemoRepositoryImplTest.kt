package com.test.design.domain.repository

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class FeatureDemoRepositoryImplTest {

    private val repository = FeatureDemoRepositoryImpl()

    private val routedDemoIds = setOf(
        "components-gallery",
        "expressive-motion",
        "component-playground",
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
    fun getAll_coversActiveCategories() {
        val categories = repository.getAll().map { it.category }.toSet()
        assertTrue(categories.contains(com.test.design.domain.model.DemoCategory.Components))
        assertTrue(categories.contains(com.test.design.domain.model.DemoCategory.DesignSystem))
    }

    @Test
    fun getCategories_returnsOnlyCategoriesWithDemos() {
        val categories = repository.getCategories()
        assertEquals(
            listOf(
                com.test.design.domain.model.DemoCategory.All,
                com.test.design.domain.model.DemoCategory.DesignSystem,
                com.test.design.domain.model.DemoCategory.Components,
            ),
            categories,
        )
    }

    @Test
    fun getAll_returnsThreeMotionPhysicsDemos() {
        assertEquals(3, repository.getAll().size)
    }
}
