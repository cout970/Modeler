package com.cout970.modeler.gui.leguicomp

import com.cout970.glutilities.structure.Timer
import com.cout970.modeler.controller.Dispatch
import com.cout970.modeler.gui.CSSTheme
import com.cout970.modeler.gui.EventGuiCommand
import com.cout970.modeler.util.forEachComponent
import com.cout970.modeler.util.toColor
import com.cout970.reactive.core.Listener
import com.cout970.reactive.core.RBuilder
import com.cout970.reactive.dsl.*
import org.joml.Vector4f
import org.liquidengine.legui.component.Component
import org.liquidengine.legui.component.TextArea
import org.liquidengine.legui.component.TextComponent
import org.liquidengine.legui.event.Event
import org.liquidengine.legui.event.MouseClickEvent
import org.liquidengine.legui.listener.ListenerMap
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
    repeat((0 until amount).count()) { append(' ') }
}

fun Component.alignAsColumn(padding: Float, margin: Float = 0f) {
    var y = margin
    childComponents.forEach {
        it.posY = y
        y += it.sizeY + padding
    }
}

fun Component.alignAsRowFromFixedSize(margin: Float = 0f) {
    if (childComponents.isEmpty()) return
    val spaceLeft = sizeX - childComponents.sumBy { it.sizeX.toInt() }
    val separation = if (childComponents.size == 1) 0f else spaceLeft / (childComponents.size - 1)
    var x = margin

    childComponents.forEach {
        it.posX = x
        x += it.sizeY + separation
    }
}

fun Component.alignAsRowFromFlexibleSize(padding: Float = 5f, margin: Float = 0f) {
    if (childComponents.isEmpty()) return
    val space = sizeX - margin * 2
    val emptySpace = if (childComponents.size == 1) space else space - padding * (childComponents.size - 1)
    val itemSize = emptySpace / childComponents.size
    var x = margin

    childComponents.forEach {
        it.posX = x
        it.sizeX = itemSize
        x += itemSize + padding
    }
}

fun Component.classes(vararg classes: String) {
    metadata["classes"] = if (metadata["classes"] is String) {
        (metadata["classes"] as String) + "," + classes.joinToString(",")
    } else {
        classes.joinToString(",")
    }
    Themes.getDefaultTheme().applyAll(this)
}

fun Component.onMouse(func: (MouseClickEvent<*>) -> Unit) {
    listenerMap.addListener(MouseClickEvent::class.java) {
        func(it)
    }
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
    textState.textColor = CSSTheme.getColor("text").toColor()
}

fun TextComponent.fontSize(size: Float = 16f) {
    textState.fontSize = size
}


fun TextArea.defaultTextColor() {
    textState.textColor = CSSTheme.getColor("text").toColor()
}

fun Component.debugPixelBorder() {
    style.border = PixelBorder().apply {
        enableBottom = true
        enableTop = true
        enableLeft = true
        enableRight = true
        color = Vector4f(1f, 0f, 0f, 1f)
    }
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
    Dispatch.run(str, this)
}