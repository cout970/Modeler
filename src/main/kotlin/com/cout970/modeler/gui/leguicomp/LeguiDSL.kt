package com.cout970.modeler.gui.leguicomp

import com.cout970.glutilities.structure.Timer
import com.cout970.modeler.controller.Dispatcher
import com.cout970.modeler.core.config.ColorPalette
import com.cout970.modeler.core.config.Config
import com.cout970.modeler.gui.event.EventGuiCommand
import com.cout970.modeler.util.forEachComponent
import com.cout970.modeler.util.toColor
import com.cout970.reactive.core.Listener
import com.cout970.reactive.core.RBuilder
import com.cout970.reactive.dsl.onClick
import com.cout970.reactive.dsl.posY
import com.cout970.reactive.dsl.sizeY
import com.cout970.vector.api.IVector3
import org.joml.Vector4f
import org.liquidengine.legui.component.Component
import org.liquidengine.legui.component.TextArea
import org.liquidengine.legui.component.TextComponent
import org.liquidengine.legui.component.TextInput
import org.liquidengine.legui.event.Event
import org.liquidengine.legui.event.MouseClickEvent
import org.liquidengine.legui.listener.ListenerMap
import org.liquidengine.legui.style.border.SimpleLineBorder
import org.liquidengine.legui.theme.Themes

/**
 * Created by cout970 on 2017/09/07.
 */

fun panel(func: Panel.() -> Unit): Panel {
    val panel = Panel()
    func(panel)
    return panel
}

val Component.key: String get() = metadata["key"].toString()

fun Component.printPaths(prefix: String = "") {
    println("$prefix/$key")
    forEachComponent {
        printPaths("$prefix/$key/${it.key}")
    }
}

fun spaces(amount: Int): String = buildString {
    (0 until amount).forEach { append(' ') }
}

fun Component.alignAsColumn(padding: Float, margin: Float = 0f) {
    var y = margin
    childComponents.forEach {
        it.posY = y
        y += it.sizeY + padding
    }
}

inline fun color(f: ColorPalette.() -> IVector3): Vector4f = Config.colorPalette.f().toColor()

inline fun Component.background(f: ColorPalette.() -> IVector3) {
    style.background.color = Config.colorPalette.f().toColor()
}

inline fun Component.border(size: Float = 1f, f: ColorPalette.() -> IVector3) {
    style.border = SimpleLineBorder(Config.colorPalette.f().toColor(), size)
}

fun Component.classes(vararg classes: String) {
    metadata["classes"] = if (metadata["classes"] is String) {
        (metadata["classes"] as String) + "," + classes.joinToString(",")
    } else {
        classes.joinToString(",")
    }
    Themes.getDefaultTheme().applyAll(this)
}

fun Component.onClick(func: (MouseClickEvent<*>) -> Unit) {
    listenerMap.addListener(MouseClickEvent::class.java) {
        if (it.action == MouseClickEvent.MouseClickAction.CLICK) {
            func(it)
        }
    }
}

fun RBuilder.onDoubleClick(time: Int = 500, func: (MouseClickEvent<*>) -> Unit) {
    var timer = Timer.miliTime
    onClick {
        if (Timer.miliTime - timer < time) {
            func(it)
        }
        timer = Timer.miliTime
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

fun debugPixelBorder() = PixelBorder().apply {
    enableBottom = true
    enableTop = true
    enableLeft = true
    enableRight = true
    color = Vector4f(1f, 0f, 0f, 1f)
}

fun Component.forEachRecursive(func: (Component) -> Unit) {
    func(this)
    childComponents.forEach { it.forEachRecursive(func) }
}

fun RBuilder.onCmd(cmd: String, func: (args: Map<String, Any>) -> Unit) {
    listeners.add(Listener(EventGuiCommand::class.java) { command ->
        if (command.command == cmd) {
            func(command.args)
        }
    })
}

fun <T : Event<*>> ListenerMap.clear(clazz: Class<T>) {
    getListeners(clazz).forEach { removeListener(clazz, it) }
}

fun Component.dispatch(str: String) {
    val dispatcher = metadata["Dispatcher"] as Dispatcher
    dispatcher.onEvent(str, this)
}