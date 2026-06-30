package com.test.design.presentation.home

import com.test.design.domain.model.DemoCategory
import com.test.design.domain.model.FeatureDemo
import com.test.design.domain.repository.FeatureDemoRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Rule
import org.junit.Test
import com.test.design.core.test.MainDispatcherRule

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val repository = FakeFeatureDemoRepository()
    private val viewModel = HomeViewModel(repository)

    @Test
    fun load_populatesFeatures() = runTest {
        viewModel.onIntent(HomeIntent.Load)

        val state = viewModel.state.value
        assertFalse(state.isLoading)
        assertEquals(1, state.features.size)
        assertEquals(1, state.filteredFeatures.size)
        assertEquals("Components", state.features.first().title)
    }

    @Test
    fun categorySelected_filtersFeatures() = runTest {
        viewModel.onIntent(HomeIntent.Load)
        viewModel.onIntent(HomeIntent.CategorySelected(DemoCategory.Components))

        val state = viewModel.state.value
        assertEquals(DemoCategory.Components, state.selectedCategory)
        assertEquals(1, state.filteredFeatures.size)
        assertEquals(DemoCategory.Components, state.filteredFeatures.first().category)
    }

    @Test
    fun featureSelected_emitsNavigateEffect() = runTest {
        val effects = mutableListOf<HomeEffect>()
        val job = launch {
            viewModel.effect.collect { effects.add(it) }
        }

        viewModel.onIntent(HomeIntent.FeatureSelected("components-gallery"))
        advanceUntilIdle()

        assertEquals(1, effects.size)
        assertEquals(HomeEffect.NavigateToDemo("components-gallery"), effects.first())
        job.cancel()
    }

    private class FakeFeatureDemoRepository : FeatureDemoRepository {
        private val demos = listOf(
            FeatureDemo("components-gallery", "Components", "Gallery", DemoCategory.Components, "UI"),
        )

        override fun getAll(): List<FeatureDemo> = demos

        override fun findById(id: String): FeatureDemo? = demos.find { it.id == id }

        override fun getCategories(): List<DemoCategory> = DemoCategory.entries
    }
}
