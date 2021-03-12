package com.cout970.reactive.core

import com.cout970.reactive.nodes.ComponentDescriptor
import org.liquidengine.legui.component.Component
import org.liquidengine.legui.event.Event

open class RBuilder {

    protected val children = mutableListOf<RNode>()
    val listeners = mutableListOf<Listener<*>>()
    var deferred: ((Component) -> Unit)? = null

    @Suppress("UNCHECKED_CAST")
    inline fun <reified T : Event<*>> on(noinline handler: (T) -> Unit) {
        listeners.add(Listener(T::class.java as Class<Event<Component>>, handler as (Event<Component>) -> Unit))
    }

    operator fun RNode.unaryPlus() {
        this@RBuilder.children.add(this)
    }

    operator fun Component.unaryPlus() {
        this@RBuilder.children.add(RNode(null, ComponentDescriptor(this), emptyList(), null, emptyList()))
    }

    protected open fun toDescriptor(): RDescriptor = FragmentDescriptor

    open fun build(key: String? = null): RNode = RNode(key, toDescriptor(), children, deferred, listeners)

    fun buildList() = children.toList()
}

fun buildNode(block: RBuilder.() -> Unit): RNode {
    val builder = RBuilder()
    builder.block()
    val node = builder.build()
    return if (node.count == 1) node.children.first() else node
}

fun buildNodeList(block: RBuilder.() -> Unit): List<RNode> {
    val builder = RBuilder()
    builder.block()
    return builder.buildList()
}