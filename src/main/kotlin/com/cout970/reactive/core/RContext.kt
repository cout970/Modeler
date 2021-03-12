package com.cout970.reactive.core

import org.liquidengine.legui.component.Component

data class RContext(val mountPoint: Component, val app: RNode) {

    internal val unmountedComponents = mutableSetOf<RComponent<*, *>>()
    internal val mountedComponents = mutableSetOf<RComponent<*, *>>()

    internal val updateListeners: MutableList<(Pair<Component, RNode>) -> Unit> = mutableListOf()

    fun registerUpdateListener(func: (Pair<Component, RNode>) -> Unit) {
        updateListeners.add(func)
    }
}