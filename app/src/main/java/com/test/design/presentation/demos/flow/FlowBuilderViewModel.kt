package com.test.design.presentation.demos.flow

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.test.design.core.export.DesignExportHelper
import com.test.design.core.export.ExportResult
import com.test.design.core.feedback.DesignFeedback
import com.test.design.presentation.demos.playground.PlaygroundCatalog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class FlowBuilderUiState(
    val flow: FlowDesignSnapshot = FlowDesignStore.defaultFlow(),
    val currentScreenIndex: Int = 0,
    val selectedPickerComponentId: String? = null,
    val isSaving: Boolean = false,
    val lastSavedAtMillis: Long? = null,
)

class FlowBuilderViewModel(
    private val store: FlowDesignStore,
) : ViewModel() {

    private val _state = MutableStateFlow(FlowBuilderUiState())
    val state: StateFlow<FlowBuilderUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            val loaded = withContext(Dispatchers.IO) { store.load() }
            if (loaded != null) {
                _state.update { it.copy(flow = loaded) }
            }
        }
    }

    fun selectScreen(index: Int) {
        _state.update {
            it.copy(currentScreenIndex = index.coerceIn(0, it.flow.screens.lastIndex.coerceAtLeast(0)))
        }
    }

    fun goToPreviousScreen() {
        _state.update { it.copy(currentScreenIndex = (it.currentScreenIndex - 1).coerceAtLeast(0)) }
    }

    fun goToNextScreen() {
        _state.update {
            it.copy(currentScreenIndex = (it.currentScreenIndex + 1).coerceAtMost(it.flow.screens.lastIndex))
        }
    }

    fun updateFlowTitle(title: String) {
        _state.update { current ->
            current.copy(flow = current.flow.copy(title = title.ifBlank { "Untitled flow" }))
        }
        persist()
    }

    fun addScreen() {
        _state.update { current ->
            val nextIndex = current.flow.screens.size + 1
            val newScreen = FlowScreenDefinition(
                id = "screen-$nextIndex",
                title = "Screen $nextIndex",
                componentIds = listOf("button-primary"),
            )
            val updatedFlow = current.flow.copy(screens = current.flow.screens + newScreen)
            current.copy(
                flow = updatedFlow,
                currentScreenIndex = updatedFlow.screens.lastIndex,
            )
        }
        persist()
    }

    fun removeCurrentScreen() {
        _state.update { current ->
            if (current.flow.screens.size <= 1) return@update current
            val updatedScreens = current.flow.screens.toMutableList().apply {
                removeAt(current.currentScreenIndex)
            }
            current.copy(
                flow = current.flow.copy(screens = updatedScreens),
                currentScreenIndex = (current.currentScreenIndex - 1).coerceAtLeast(0),
            )
        }
        persist()
    }

    fun addComponentToCurrentScreen(componentId: String) {
        if (PlaygroundCatalog.findById(componentId) == null) return
        _state.update { current ->
            val screens = current.flow.screens.toMutableList()
            val screen = screens[current.currentScreenIndex]
            if (componentId in screen.componentIds) return@update current
            screens[current.currentScreenIndex] = screen.copy(
                componentIds = screen.componentIds + componentId,
            )
            current.copy(flow = current.flow.copy(screens = screens))
        }
        persist()
    }

    fun removeComponentFromCurrentScreen(componentId: String) {
        _state.update { current ->
            val screens = current.flow.screens.toMutableList()
            val screen = screens[current.currentScreenIndex]
            screens[current.currentScreenIndex] = screen.copy(
                componentIds = screen.componentIds.filterNot { it == componentId },
            )
            current.copy(flow = current.flow.copy(screens = screens))
        }
        persist()
    }

    fun saveNow() {
        persist(showSaving = true)
    }

    fun export(context: Context): ExportResult {
        val snapshot = _state.value.flow
        val json = store.exportJson(snapshot)
        val result = DesignExportHelper.shareJson(
            context = context,
            fileName = "flow-design.json",
            json = json,
            chooserTitle = "Export flow design",
        )
        DesignFeedback.showExportResult(context, result)
        return result
    }

    private fun persist(showSaving: Boolean = false) {
        val snapshot = _state.value.flow
        viewModelScope.launch {
            if (showSaving) {
                _state.update { it.copy(isSaving = true) }
            }
            withContext(Dispatchers.IO) { store.save(snapshot) }
            _state.update {
                it.copy(
                    isSaving = false,
                    lastSavedAtMillis = System.currentTimeMillis(),
                )
            }
        }
    }
}

class FlowBuilderViewModelFactory(
    private val store: FlowDesignStore,
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FlowBuilderViewModel::class.java)) {
            return FlowBuilderViewModel(store) as T
        }
        throw IllegalArgumentException("Unknown ViewModel: ${modelClass.name}")
    }
}
