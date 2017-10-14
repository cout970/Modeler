package com.cout970.modeler.gui.react.core

import com.cout970.modeler.gui.Gui
import org.liquidengine.legui.component.Component
import org.liquidengine.legui.component.Container

/**
 * Created by cout970 on 2017/09/24.
 */

class RContext(val root: Container<Component>, val gui: Gui, val virtualTree: () -> Component) {

    fun <P : Any, S : Any> markDirty(comp: RComponent<P, S>) {
//        log(Level.DEBUG) {
//            "RComponent(state=${comp.state}, props=${comp.props}, type=${comp.javaClass})"
//        }
        findParent(comp, root)?.let {
            RComponentRenderer.buildComponent(it)
        }
    }

    @Suppress("UNCHECKED_CAST")
    fun findParent(child: RComponent<*, *>, tree: Container<Component>): RComponentWrapper<*, *, *>? {
        tree.childs.forEach {
            if (it is RComponentWrapper<*, *, *>) {
                if (it.component == child) return it
            }
            if (it is Container<*>) {
                findParent(child, it as Container<Component>)?.let { return it }
            }
        }
        return null
    }
}