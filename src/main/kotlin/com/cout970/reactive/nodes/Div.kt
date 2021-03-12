package com.cout970.reactive.nodes

import com.cout970.reactive.core.RBuilder
import com.cout970.reactive.core.RDescriptor
import org.joml.Vector2f
import org.liquidengine.legui.component.Component
import org.liquidengine.legui.component.Panel

class DivDescriptor : RDescriptor {
    override fun mapToComponent(): Component = Panel().apply { size = Vector2f(100f, 100f) }
}

class DivBuilder : RBuilder() {
    override fun toDescriptor(): RDescriptor = DivDescriptor()
}

fun RBuilder.div(key: String? = null, block: DivBuilder.() -> Unit = {}) = +DivBuilder().apply(block).build(key)

fun DivBuilder.style(func: Panel.() -> Unit) {
    deferred = { (it as Panel).func() }
}

