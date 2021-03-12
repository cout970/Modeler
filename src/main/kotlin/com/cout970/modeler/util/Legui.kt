package com.cout970.modeler.util

import com.cout970.reactive.core.Renderer
import com.cout970.vector.api.IVector2
import org.joml.Vector2f
import org.liquidengine.legui.component.*
import org.liquidengine.legui.event.Event
import org.liquidengine.legui.listener.EventListener
import org.liquidengine.legui.style.color.ColorConstants
import org.liquidengine.legui.system.context.Context

val Component.absolutePositionV: IVector2 get() = absolutePosition.toIVector()

var Frame.size: Vector2f
    get() = componentLayer.size
    set(value) {
        allLayers.forEach {
            it.size = value
        }
        componentLayer.size = value
    }


@Deprecated("Use the Reactive version", ReplaceWith("this.child(key)", "com.cout970.reactive.dsl.child"))
fun Component.child(key: String) = childComponents.find { it.metadata[Renderer.METADATA_KEY] == key }


fun Component.disable() {
    isEnabled = false
    if (this.isNotEmpty) {
        this.childComponents.forEach(Component::disable)
    }
}

fun Component.enable() {
    isEnabled = true
    if (this.isNotEmpty) {
        this.childComponents.forEach(Component::enable)
    }
}

var TextInput.text: String
    get() = textState.text
    set(value) {
        textState.text = value
    }

var TextArea.text: String
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
    when {
        this.isNotEmpty -> childComponents.forEach { it.forEachChild(func) }
        else -> func(this)
    }
}

@Suppress("UNCHECKED_CAST")
fun Component.forEachComponent(func: (Component) -> Unit) {
    when {
        this.isNotEmpty -> {
            func(this)
            childComponents.forEach { it.forEachComponent(func) }
        }
        else -> func(this)
    }
}

@Deprecated(message = "use borderless()",
        replaceWith = ReplaceWith("borderless()", "com.cout970.reactive.dsl.borderless"))
fun Component.setBorderless(): Component {
    style.border.isEnabled = false
    return this
}

@Deprecated(message = "use rectCorners()", replaceWith = ReplaceWith("rectCorners()", "com.cout970.reactive.dsl.rectCorners"))
fun Component.rectangularCorners(): Component {
    style.setBorderRadius(0f)
    return this
}

@Deprecated(message = "use transparent()",
        replaceWith = ReplaceWith("transparent()", "com.cout970.reactive.dsl.transparent"))
fun Component.setTransparent(): Component {
    style.background.color = ColorConstants.transparent()
    return this
}

val Component.isNotEmpty get() = !isEmpty

@Suppress("UNCHECKED_CAST")
fun Component.disableInput() {
    when {
        this is Button -> this.isEnabled = false
        this is CheckBox -> this.isEnabled = false
        this is TextInput -> {
            this.isEnabled = false
            this.isEditable = false
        }
        isNotEmpty -> childComponents.forEach { it.disableInput() }
    }
}