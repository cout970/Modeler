package com.cout970.modeler.gui.react

import com.cout970.modeler.gui.react.leguicomp.Panel
import org.liquidengine.legui.component.Container

/**
 * Created by cout970 on 2017/09/07.
 */

fun panel(func: Panel.() -> Unit): Panel {
    val panel = Panel()
    func(panel)
    return panel
}

fun Container<Panel>.panel(func: Panel.() -> Unit) {
    val panel = Panel()
    func(panel)
    add(panel)
}