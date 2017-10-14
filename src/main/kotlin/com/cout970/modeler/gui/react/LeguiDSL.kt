package com.cout970.modeler.gui.react

import com.cout970.modeler.core.log.Level
import com.cout970.modeler.core.log.log
import com.cout970.modeler.gui.react.core.RComponentWrapper
import com.cout970.modeler.gui.react.event.EventSelectionUpdate
import com.cout970.modeler.gui.react.leguicomp.Panel
import org.liquidengine.legui.component.Component
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

fun Panel.panel(func: Panel.() -> Unit) {
    val panel = Panel()
    func(panel)
    add(panel)
}

fun Component.printTree(prefix: String = "") {
    val flag = this.listenerMap.getListeners(EventSelectionUpdate::class.java).isNotEmpty()

    if (this is Container<*>) {
        if (this is RComponentWrapper<*, *, *>) {
            log(Level.DEBUG) { "$prefix${component.javaClass}($flag)" }
        } else {
            log(Level.DEBUG) { "$prefix${this.javaClass}($flag)" }
        }
        this.childs.forEach {
            it.printTree(prefix + "|   ")
        }
    } else {
        log(Level.DEBUG) { "$prefix${this.javaClass}($flag)" }
    }
}

private fun spaces(amount: Int): String = buildString {
    (0 until amount).forEach { append(' ') }
}