package com.cout970.reactive.dsl

import com.cout970.reactive.core.RBuilder
import com.cout970.reactive.core.Renderer
import com.cout970.reactive.nodes.ComponentBuilder
import com.cout970.reactive.nodes.comp
import org.liquidengine.legui.component.Component
import org.liquidengine.legui.event.Event
import org.liquidengine.legui.listener.EventListener
import org.liquidengine.legui.listener.ListenerMap

/**
 * This function converts all the children of this component to RNodes so they are not ignored/removed by
 * the reconciliation algorithm
 */
fun <T : Component> ComponentBuilder<T>.childrenAsNodes() {
    this.component.childComponents.forEach {
        comp(it) {
            if (!it.isEmpty) {
                childrenAsNodes()
            }
        }
    }
}

/**
 * This stores a function in a component, this function will be called after the component is mounted in
 * the component tree.
 *
 * The order of execution of this function is the following:
 * - The parent function gets called if exist
 * - Then for every child, it's function is called if exists, this follows the childComponents order in the parent
 */
fun RBuilder.postMount(func: Component.() -> Unit) {
    val oldDeferred = this.deferred
    this.deferred = {

        val oldFunc = it.metadata[Renderer.METADATA_POST_MOUNT]

        // Preserve the old postMount
        val finalFunc = if (oldFunc != null) {

            val comb: Component.() -> Unit = {
                (oldFunc as? (Component.() -> Unit))?.invoke(this)
                func()
            }

            comb
        } else {
            func
        }

        it.metadata[Renderer.METADATA_POST_MOUNT] = finalFunc

        oldDeferred?.invoke(it)
    }
}

/**
 * Given a key, finds a child of this component with it or returns null
 */
fun Component.child(key: String): Component? {
    return childComponents.find { it.metadata[Renderer.METADATA_KEY] == key }
}

fun <E : Event<*>> ListenerMap.replaceListener(eventClass: Class<E>, listener: EventListener<E>) {
    getListeners<E>(eventClass).firstOrNull()?.let { removeListener(eventClass, it) }
    getListeners<E>(eventClass).add(listener)
}