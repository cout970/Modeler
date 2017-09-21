package com.cout970.modeler.gui.react

import com.cout970.modeler.gui.Gui
import com.cout970.modeler.gui.react.leguicomp.LeguiComponentBridge
import com.cout970.modeler.util.toIVector
import com.cout970.vector.api.IVector2
import org.liquidengine.legui.component.Component
import org.liquidengine.legui.component.Container
import org.liquidengine.legui.component.Frame as LeguiFrame

/**
 * Created by cout970 on 2017/09/07.
 */

object ReactRenderer {

    fun render(gui: Gui, base: Container<Component>, func: () -> Component) {
        val controller = ReactContext(gui, base, func)
        controller.render()
    }

    @Suppress("UNCHECKED_CAST")
    fun recursiveUnwrapping(c: Component, ctx: ReactContext, parentSize: IVector2): Component {
        if (c is Container<*>) {
            val unwrapped = c.childs.map { recursiveUnwrapping(it, ctx, c.size.toIVector()) }
            c.clearChilds()
            (c as Container<Component>).addAll(unwrapped)
        }
        if (c is LeguiComponentBridge<*, *, *>) {
            val comp = (c.factory as IComponentFactory<Any, Any, *>).build(c.props as Any)
            comp.context = ctx
            return recursiveUnwrapping(comp.render(parentSize), ctx, parentSize)
        }
        return c
    }
}