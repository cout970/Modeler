package com.cout970.reactive.nodes

import com.cout970.reactive.core.RBuilder
import com.cout970.reactive.core.RDescriptor
import org.liquidengine.legui.component.Component

class ComponentDescriptor(val component: Component) : RDescriptor {
    override fun mapToComponent(): Component = component
}

class ComponentBuilder<T : Component>(var component: T) : RBuilder() {

    override fun toDescriptor(): RDescriptor = ComponentDescriptor(component)
}

fun <T : Component> ComponentBuilder<T>.style(func: T.() -> Unit) {
    component.func()
}

fun <T : Component> RBuilder.comp(comp: T, key: String? = null, block: ComponentBuilder<T>.() -> Unit = {}) =
    +ComponentBuilder(comp).apply(block).build(key)
