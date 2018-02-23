package com.cout970.modeler.util

/**
 * Created by cout970 on 2017/10/20.
 */
class StateHolder<S, E>(initialState: S, val reducer: (S, E) -> S) {

    private var state: S = initialState
    private var onChange: ((S) -> Unit)? = null

    fun dispatch(event: E) {
        state = reducer(state, event)
        onChange?.invoke(state)
    }

    fun onChange(listener: (S) -> Unit) {
        onChange = listener
    }

    fun getState(): S = state
}