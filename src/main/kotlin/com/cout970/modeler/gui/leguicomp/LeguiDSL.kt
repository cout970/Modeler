package com.cout970.modeler.gui.leguicomp

import com.cout970.modeler.core.log.Level
import com.cout970.modeler.core.log.log
import com.cout970.modeler.gui.event.EventSelectionUpdate
import com.cout970.modeler.gui.reactive.RBuildContext
import com.cout970.modeler.gui.reactive.RComponentWrapper
import com.cout970.modeler.util.isNotEmpty
import org.joml.Vector4f
import org.liquidengine.legui.component.Component

/**
 * Created by cout970 on 2017/09/07.
 */

fun panel(func: Panel.() -> Unit): Panel {
    val panel = Panel()
    func(panel)
    return panel
}

fun Component.printTree(prefix: String = "") {
    val flag = this.listenerMap.getListeners(EventSelectionUpdate::class.java).isNotEmpty()

    if (this.isNotEmpty) {
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

fun spaces(amount: Int): String = buildString {
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

fun debugPixelBorder() = PixelBorder().apply {
    enableBottom = true
    enableTop = true
    enableLeft = true
    enableRight = true
    color = Vector4f(1f, 0f, 0f, 1f)
}