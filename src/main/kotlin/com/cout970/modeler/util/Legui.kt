package com.cout970.modeler.util

import com.cout970.vector.api.IVector2
import org.joml.Vector2f
import org.liquidengine.legui.component.Component

import org.liquidengine.legui.component.Frame
import org.liquidengine.legui.component.TextInput
import org.liquidengine.legui.event.Event
import org.liquidengine.legui.listener.EventListener
import org.liquidengine.legui.system.context.Context

val Component.absolutePositionV: IVector2 get() = absolutePosition.toIVector()

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
    if (this is Component) {
        this.childs.forEach(Component::disable)
    }
}

fun Component.enable() {
    isEnabled = true
    if (this is Component) {
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

inline fun <reified T : Event<Component>> Component.getListeners(): List<Pair<Component, EventListener<T>>> {
    val list = mutableListOf<Pair<Component, EventListener<T>>>()
    forEachComponent {
        list += it.listenerMap.getListeners(T::class.java).map { listener -> it to listener }
    }
    return list
}

@Suppress("UNCHECKED_CAST")
fun Component.forEachChild(func: (Component) -> Unit) {
    when (this) {
        is Component -> (this as Component).childs.forEach { it.forEachChild(func) }
        else -> func(this)
    }
}

@Suppress("UNCHECKED_CAST")
fun Component.forEachComponent(func: (Component) -> Unit) {
    when (this) {
        is Component -> {
            func(this)
            (this as Component).childs.forEach { it.forEachComponent(func) }
        }
        else -> func(this)
    }
}