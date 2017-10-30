package com.cout970.modeler.gui.react

import com.cout970.modeler.core.log.Level
import com.cout970.modeler.core.log.log
import com.cout970.modeler.gui.react.core.RBuildContext
import com.cout970.modeler.gui.react.core.RComponentWrapper
import com.cout970.modeler.gui.react.event.EventSelectionUpdate
import com.cout970.modeler.gui.react.leguicomp.Panel
import org.liquidengine.legui.component.Component


/**
 * Created by cout970 on 2017/09/07.
 */

fun panel(func: Panel.() -> Unit): Panel {
    val panel = Panel()
    func(panel)
    return panel
}

fun Component.panel(func: Panel.() -> Unit) {
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

    if (this is Component) {
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

fun Component.fillX(ctx: RBuildContext) {
    size.x = ctx.parentSize.xf
}

fun Component.fillY(ctx: RBuildContext) {
    size.y = ctx.parentSize.yf
}

fun Component.marginX(ctx: RBuildContext, margin: Float) {
    size.x = ctx.parentSize.xf - margin * 2
    position.x = margin
}

fun Component.marginY(ctx: RBuildContext, margin: Float) {
    size.y = ctx.parentSize.yf - margin * 2
    position.y = margin
}

fun Component.centerX(ctx: RBuildContext) {
    position.x = (ctx.parentSize.xf - size.x) * 0.5f
}

fun Component.centerY(ctx: RBuildContext) {
    position.y = (ctx.parentSize.yf - size.y) * 0.5f
}