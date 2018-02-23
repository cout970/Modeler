package com.cout970.modeler.gui.leguicomp

import com.cout970.modeler.util.rectangularCorners
import org.liquidengine.legui.component.Component as LeguiComponent
import org.liquidengine.legui.component.Panel as LeguiPanel

/**
 * Created by cout970 on 2017/09/07.
 */

open class Panel : LeguiComponent() {

    var name: String? = null

    init {
        rectangularCorners()
    }

    operator fun LeguiComponent.unaryPlus() {
        this@Panel.add(this)
    }

    override fun toString(): String {
        return "Panel(${childs.joinToString { it.toString() }})"
    }
}