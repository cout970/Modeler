package com.cout970.modeler.gui.reactive

import com.cout970.modeler.core.log.Profiler
import com.cout970.modeler.gui.Gui
import com.cout970.modeler.util.isNotEmpty
import org.liquidengine.legui.component.Component

/**
 * Created by cout970 on 2017/09/24.
 */

class RContext(val root: Component, val gui: Gui, val virtualTree: () -> Component) {

    private val dirtyComponents: MutableList<RComponentWrapper<*, *, *>> = mutableListOf()

    fun update() {
        if (dirtyComponents.isNotEmpty()) {
            Profiler.startSection("guiUpdate")
            dirtyComponents.toList().forEach {
                RComponentRenderer.buildComponent(it)
            }
            dirtyComponents.clear()
            Profiler.endSection()
        }
    }

    fun <P : Any, S : Any> markDirty(comp: RComponent<P, S>) {
        findParent(comp, root)?.let {
            dirtyComponents.add(it)
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun findParent(child: RComponent<*, *>, tree: Component): RComponentWrapper<*, *, *>? {
        tree.childs.forEach {
            if (it is RComponentWrapper<*, *, *>) {
                if (it.component == child) return it
            }
            if (it.isNotEmpty) {
                findParent(child, it)?.let { return it }
            }
        }
        return null
    }
}