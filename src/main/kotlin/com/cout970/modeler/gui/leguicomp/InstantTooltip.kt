package com.cout970.modeler.gui.leguicomp

import org.joml.Vector2f
import org.liquidengine.legui.component.Component
import org.liquidengine.legui.component.Tooltip

/**
 * Created by cout970 on 2017/10/29.
 */
class InstantTooltip(str: String) : Tooltip(str) {

    init {
        size = Vector2f(str.length * textState.textWidth * 0.5f, textState.textHeight)
    }

    override fun setComponent(component: Component?) {
        super.setComponent(component)
        component?.let { comp ->
            position = Vector2f(comp.size.x * 0.5f - size.x / 2, comp.size.y)
        }
    }
}