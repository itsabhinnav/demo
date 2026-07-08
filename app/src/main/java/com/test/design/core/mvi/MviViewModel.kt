package com.test.design.core.mvi

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Lightweight MVI base for unidirectional data flow.
 * UI dispatches [Event]s; ViewModel reduces them into [State].
 */
abstract class MviViewModel<State, Event>(
    initialState: State,
) : ViewModel() {

    private val _state = MutableStateFlow(initialState)
    val state: StateFlow<State> = _state.asStateFlow()

    protected fun setState(reducer: State.() -> State) {
        _state.update(reducer)
    }

    protected fun currentState(): State = _state.value

    abstract fun onEvent(event: Event)
}
