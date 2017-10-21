package com.cout970.modeler.util

import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.newSingleThreadContext

/**
 * Created by cout970 on 2017/10/20.
 */
class StateHolder<S, E>(initialState: S, val reducer: suspend (S, E) -> S) {

    companion object {
        val context = newSingleThreadContext("StateHolderCoroutines")
    }

    private var state: S = initialState
    private var onChange: ((S) -> Unit)? = null

    fun dispatch(event: E) {
        launch(context) {
            state = reducer(state, event)
            onChange?.invoke(state)
        }
    }

    fun onChange(listener: (S) -> Unit) {
        onChange = listener
    }

    fun getState(): S = state
}