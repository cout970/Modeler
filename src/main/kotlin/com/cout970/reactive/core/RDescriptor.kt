package com.cout970.reactive.core

import org.liquidengine.legui.component.Component
import org.liquidengine.legui.component.Panel
import org.liquidengine.legui.style.Style

interface RDescriptor {
    fun mapToComponent(): Component
}

object EmptyDescriptor : RDescriptor {
    override fun mapToComponent(): Component = Panel().apply {
        style.display = Style.DisplayType.MANUAL
        isEnabled = false
    }
}

object FragmentDescriptor : RDescriptor {
    override fun mapToComponent(): Component = Panel()
}

