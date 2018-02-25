package com.cout970.modeler.gui.leguicomp

import com.cout970.modeler.controller.Dispatcher
import com.cout970.modeler.core.config.ColorPalette
import com.cout970.modeler.core.config.Config
import com.cout970.modeler.core.log.Level
import com.cout970.modeler.core.log.log
import com.cout970.modeler.gui.event.EventGuiCommand
import com.cout970.modeler.gui.event.EventSelectionUpdate
import com.cout970.modeler.gui.reactive.RComponentWrapper
import com.cout970.modeler.util.forEachComponent
import com.cout970.modeler.util.isNotEmpty
import com.cout970.modeler.util.toColor
import com.cout970.reactive.core.Listener
import com.cout970.reactive.dsl.posY
import com.cout970.reactive.dsl.sizeY
import com.cout970.reactive.nodes.ComponentBuilder
import com.cout970.reactive.nodes.comp
import com.cout970.vector.api.IVector3
import org.joml.Vector4f
import org.liquidengine.legui.component.Component
import org.liquidengine.legui.component.TextArea
import org.liquidengine.legui.component.TextComponent
import org.liquidengine.legui.component.TextInput
import org.liquidengine.legui.event.Event
import org.liquidengine.legui.event.MouseClickEvent
import org.liquidengine.legui.event.ScrollEvent
import org.liquidengine.legui.listener.ListenerMap
import org.liquidengine.legui.style.border.SimpleLineBorder

/**
 * Created by cout970 on 2017/09/07.
 */

fun panel(func: Panel.() -> Unit): Panel {
    val panel = Panel()
    func(panel)
    return panel
}

val Component.name: String
    get() = if (this is Panel) {
        name ?: throw IllegalStateException("$this")
    } else javaClass.simpleName

fun Component.printPaths(prefix: String = "") {
    println("$prefix/$name")
    forEachComponent {
        printPaths("$prefix/$name/${it.name}")
    }
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

fun Component.alignAsColumn(padding: Float) {
    var y = 0f
    childs.forEach {
        it.posY = y
        y += it.sizeY + padding
    }
}

fun Component.fill() {
    size.x = parent.size.x
    size.y = parent.size.y
}

fun Component.fillX() {
    size.x = parent.size.x
}

fun Component.fillY() {
    size.y = parent.size.y
}

fun Component.marginX(margin: Float) {
    size.x = parent.size.x - margin * 2
    position.x = margin
}

fun Component.marginY(margin: Float) {
    size.y = parent.size.y - margin * 2
    position.y = margin
}

fun Component.center() {
    position.x = (parent.size.x - size.x) * 0.5f
    position.y = (parent.size.y - size.y) * 0.5f
}

fun Component.centerX() {
    position.x = (parent.size.x - size.x) * 0.5f
}

fun Component.centerY() {
    position.y = (parent.size.y - size.y) * 0.5f
}

inline fun color(f: ColorPalette.() -> IVector3): Vector4f = Config.colorPalette.f().toColor()

inline fun Component.background(f: ColorPalette.() -> IVector3) {
    style.background.color = Config.colorPalette.f().toColor()
}

inline fun Component.border(size: Float = 1f, f: ColorPalette.() -> IVector3) {
    style.border = SimpleLineBorder(Config.colorPalette.f().toColor(), size)
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

fun TextComponent.fontSize(size: Float = 16f) {
    textState.fontSize = size
}

inline fun TextInput.textColor(f: ColorPalette.() -> IVector3) {
    textState.textColor = Config.colorPalette.f().toColor()
}

inline fun TextInput.highlightColor(f: ColorPalette.() -> IVector3) {
    textState.highlightColor = Config.colorPalette.f().toColor()
}

inline fun TextInput.focusedStrokeColor(f: ColorPalette.() -> IVector3) {
    style.focusedStrokeColor = Config.colorPalette.f().toColor()
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
    style.focusedStrokeColor = Config.colorPalette.f().toColor()
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

fun com.cout970.reactive.core.RBuilder.onCmd(cmd: String, func: (args: Map<String, Any>) -> Unit) {
    listeners.add(Listener(EventGuiCommand::class.java) { command ->
        if (command.command == cmd) {
            func(command.args)
        }
    })
}

fun com.cout970.reactive.core.RBuilder.postMount(func: Component.() -> Unit) {
    val oldDeferred = this.deferred
    this.deferred = {
        it.metadata["postMount"] = func
        oldDeferred?.invoke(it)
    }
}

fun <T : Event<*>> ListenerMap.clear(clazz: Class<T>) {
    getListeners(clazz).forEach { removeListener(clazz, it) }
}

fun Component.dispatch(str: String) {
    val dispatcher = metadata["Dispatcher"] as Dispatcher
    dispatcher.onEvent(str, this)
}

fun <T : Component> ComponentBuilder<T>.childrenAsNodes() {
    this.component.childs.forEach {
        comp(it) {
            if (it.isNotEmpty) {
                childrenAsNodes()
            }
        }
    }
}