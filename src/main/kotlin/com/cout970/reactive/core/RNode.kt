package com.cout970.reactive.core

import org.liquidengine.legui.component.Component
import org.liquidengine.legui.event.Event

data class Listener<E : Event<Component>>(val clazz: Class<E>, val handler: (E) -> Unit)

data class RNode(
    val key: String?,
    val componentDescriptor: RDescriptor,
    val children: List<RNode> = emptyList(),
    val deferred: ((Component) -> Unit)? = null,
    val listeners: List<Listener<*>> = emptyList()
) {

    val isEmpty: Boolean get() = children.isEmpty()
    val count: Int get() = children.size
    val isNotEmpty: Boolean get() = !children.isEmpty()

    fun withChildren(children: List<RNode>): RNode = copy(children = children)
}

val emptyNode = RNode("empty", EmptyDescriptor)