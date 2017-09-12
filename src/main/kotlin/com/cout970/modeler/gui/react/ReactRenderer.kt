package com.cout970.modeler.gui.react

import com.cout970.modeler.gui.react.leguicomp.LeguiComponentBridge
import org.liquidengine.legui.component.Component
import org.liquidengine.legui.component.Container
import org.liquidengine.legui.component.Frame as LeguiFrame

/**
 * Created by cout970 on 2017/09/07.
 */

object ReactRenderer {

    fun render(base: Container<Component>, comp: Component? = null, func: () -> Component) {
        val subTree = recursiveUnwrapping(func())
        if (comp == null) {
            base.clearChilds()
            base.add(subTree)
        } else {
            base.remove(comp)
            base.add(subTree)
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun recursiveUnwrapping(c: Component): Component {
        if (c is Container<*>) {
            val unwrapped = c.childs.map { recursiveUnwrapping(it) }
            c.clearChilds()
            (c as Container<Component>).addAll(unwrapped)
        }
        if (c is LeguiComponentBridge<*, *, *>) {
            val comp = (c.factory as IComponentFactory<Any, Any, *>).build(c.props as Any)
            return comp.render()
        }
        return c
    }
}