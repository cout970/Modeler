package com.cout970.modeler.util

import com.cout970.vector.api.IVector2
import com.cout970.vector.extensions.plus
import org.joml.Vector2f
import org.liquidengine.legui.component.Component
import org.liquidengine.legui.component.Container
import org.liquidengine.legui.component.Frame
import org.liquidengine.legui.component.TextInput
import org.liquidengine.legui.event.MouseClickEvent
import org.liquidengine.legui.system.context.Context

val Component.absolutePosition: IVector2 get() {
    var sum = this.position.toIVector()
    var parent = this.parent
    while (parent != null) {
        sum += parent.position.toIVector()
        parent = parent.parent
    }
    return sum
}

inline fun <T : Component> T.onClick(id: Int, crossinline func: (Int) -> Unit): T {
    listenerMap.addListener(MouseClickEvent::class.java, {
        if (it.action == MouseClickEvent.MouseClickAction.PRESS) {
            func(id)
        }
    })
    return this
}

var Frame.size: Vector2f
    get() = componentLayer.container.size
    set(value) {
        allLayers.forEach {
            it.container.size = value
        }
        componentLayer.container.size = value
    }

fun Component.hide() {
    isEnabled = false
    isVisible = false
}

fun Component.show() {
    isEnabled = true
    isVisible = true
}

fun Component.disable() {
    isEnabled = false
    if (this is Container<*>) {
        this.childs.forEach(Component::disable)
    }
}

fun Component.enable() {
    isEnabled = true
    if (this is Container<*>) {
        this.childs.forEach(Component::enable)
    }
}

var TextInput.text: String
    get() = textState.text
    set(value) {
        textState.text = value
    }

fun Context.focus(component: Component) {
    val focusedGui = focusedGui
    if (component != focusedGui) component.isFocused = false

    this.focusedGui = component
    component.isFocused = true
    component.isPressed = true
}

fun Context.unfocus() {
    focusedGui.isFocused = false
    this.focusedGui = null
}

inline fun <reified T> Component?.parent(): T? = this?.parent as? T