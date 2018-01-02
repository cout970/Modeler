package com.cout970.modeler.gui.leguicomp

import com.cout970.modeler.core.config.ColorPalette
import com.cout970.modeler.core.config.Config
import com.cout970.modeler.core.log.Level
import com.cout970.modeler.core.log.log
import com.cout970.modeler.gui.event.EventSelectionUpdate
import com.cout970.modeler.gui.reactive.RBuilder
import com.cout970.modeler.gui.reactive.RComponentWrapper
import com.cout970.modeler.util.isNotEmpty
import com.cout970.modeler.util.toColor
import com.cout970.vector.api.IVector3
import org.joml.Vector4f
import org.liquidengine.legui.border.SimpleLineBorder
import org.liquidengine.legui.component.Component
import org.liquidengine.legui.component.TextArea
import org.liquidengine.legui.component.TextComponent
import org.liquidengine.legui.component.TextInput
import org.liquidengine.legui.event.MouseClickEvent
import org.liquidengine.legui.event.ScrollEvent

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
            log(Level.DEBUG) { "$prefix${component.javaClass.simpleName}($flag)" }
        } else {
            log(Level.DEBUG) { "$prefix${this.javaClass.simpleName}($flag)" }
        }
        this.childs.forEach {
            it.printTree(prefix + "|   ")
        }
    } else {
        log(Level.DEBUG) { "$prefix${this.javaClass.simpleName}($flag)" }
    }
}

fun spaces(amount: Int): String = buildString {
    (0 until amount).forEach { append(' ') }
}

var Component.width: Float
    get() = size.x
    set(x) {
        size.x = x
    }

var Component.height: Float
    get() = size.y
    set(y) {
        size.y = y
    }

var Component.posX: Float
    get() = position.x
    set(x) {
        position.x = x
    }

var Component.posY: Float
    get() = position.y
    set(y) {
        position.y = y
    }

fun Component.fill(ctx: RBuilder) {
    size.x = ctx.parentSize.xf
    size.y = ctx.parentSize.yf
}

fun Component.fillX(ctx: RBuilder) {
    size.x = ctx.parentSize.xf
}

fun Component.fillY(ctx: RBuilder) {
    size.y = ctx.parentSize.yf
}

fun Component.marginX(ctx: RBuilder, margin: Float) {
    size.x = ctx.parentSize.xf - margin * 2
    position.x = margin
}

fun Component.marginY(ctx: RBuilder, margin: Float) {
    size.y = ctx.parentSize.yf - margin * 2
    position.y = margin
}

fun Component.center(ctx: RBuilder) {
    position.x = (ctx.parentSize.xf - size.x) * 0.5f
    position.y = (ctx.parentSize.yf - size.y) * 0.5f
}

fun Component.centerX(ctx: RBuilder) {
    position.x = (ctx.parentSize.xf - size.x) * 0.5f
}

fun Component.centerY(ctx: RBuilder) {
    position.y = (ctx.parentSize.yf - size.y) * 0.5f
}

inline fun Component.background(f: ColorPalette.() -> IVector3) {
    backgroundColor = Config.colorPalette.f().toColor()
}

inline fun Component.border(size: Float = 1f, f: ColorPalette.() -> IVector3) {
    border = SimpleLineBorder(Config.colorPalette.f().toColor(), size)
}

fun Component.onClick(func: (MouseClickEvent<*>) -> Unit) {
    listenerMap.addListener(MouseClickEvent::class.java) {
        if (it.action == MouseClickEvent.MouseClickAction.CLICK) {
            func(it)
        }
    }
}

fun TextComponent.defaultTextColor() {
    textState.textColor = Config.colorPalette.textColor.toColor()
}

fun TextComponent.fontSize(size: Float = 16f){
    textState.fontSize = size
}

inline fun TextInput.textColor(f: ColorPalette.() -> IVector3) {
    textState.textColor = Config.colorPalette.f().toColor()
}

inline fun TextInput.highlightColor(f: ColorPalette.() -> IVector3) {
    textState.highlightColor = Config.colorPalette.f().toColor()
}

inline fun TextInput.focusedStrokeColor(f: ColorPalette.() -> IVector3) {
    focusedStrokeColor = Config.colorPalette.f().toColor()
}

fun TextArea.defaultTextColor() {
    textState.textColor = Config.colorPalette.textColor.toColor()
}

inline fun TextArea.textColor(f: ColorPalette.() -> IVector3) {
    textState.textColor = Config.colorPalette.f().toColor()
}

inline fun TextArea.highlightColor(f: ColorPalette.() -> IVector3) {
    textState.highlightColor = Config.colorPalette.f().toColor()
}

inline fun TextArea.focusedStrokeColor(f: ColorPalette.() -> IVector3) {
    focusedStrokeColor = Config.colorPalette.f().toColor()
}

var Component.onScroll: ((ScrollEvent<*>) -> Unit)?
    get() = null
    set(value) {
        listenerMap.addListener(ScrollEvent::class.java, value)
    }

fun debugPixelBorder() = PixelBorder().apply {
    enableBottom = true
    enableTop = true
    enableLeft = true
    enableRight = true
    color = Vector4f(1f, 0f, 0f, 1f)
}

fun Component.forEachRecursive(func: (Component) -> Unit) {
    func(this)
    childs.forEach { it.forEachRecursive(func) }
}