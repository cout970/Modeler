package com.cout970.modeler.gui.react.leguicomp

import com.cout970.modeler.util.getListeners
import org.liquidengine.legui.component.misc.listener.component.TooltipCursorEnterListener
import org.liquidengine.legui.event.CursorEnterEvent
import org.liquidengine.legui.component.Component as LeguiComponent
import org.liquidengine.legui.component.Panel as LeguiPanel

/**
 * Created by cout970 on 2017/09/07.
 */

open class Panel : LeguiPanel() {

    var width: Float
        get() = size.x
        set(x) {
            size.x = x
        }

    var height: Float
        get() = size.y
        set(y) {
            size.y = y
        }

    var posX: Float
        get() = position.x
        set(x) {
            position.x = x
        }

    var posY: Float
        get() = position.y
        set(y) {
            position.y = y
        }

    override fun toString(): String {
        return "Panel(${childs.joinToString { it.toString() }})"
    }
}