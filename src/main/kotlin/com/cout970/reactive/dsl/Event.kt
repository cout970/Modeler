@file:Suppress("UNCHECKED_CAST")

package com.cout970.reactive.dsl

import com.cout970.reactive.core.Listener
import com.cout970.reactive.core.RBuilder
import org.liquidengine.legui.component.Component
import org.liquidengine.legui.event.*

fun RBuilder.onClick(func: (MouseClickEvent<Component>) -> Unit) {
    listeners.add(Listener(MouseClickEvent::class.java as Class<MouseClickEvent<Component>>) {
        if (it.isClick()) func(it)
    })
}

fun RBuilder.onPress(func: (MouseClickEvent<Component>) -> Unit) {
    listeners.add(Listener(MouseClickEvent::class.java as Class<MouseClickEvent<Component>>) {
        if (it.isPress()) func(it)
    })
}

fun RBuilder.onRelease(func: (MouseClickEvent<Component>) -> Unit) {
    listeners.add(Listener(MouseClickEvent::class.java as Class<MouseClickEvent<Component>>) {
        if (it.isRelease()) func(it)
    })
}

fun RBuilder.onCharTyped(func: (CharEvent<Component>) -> Unit) {
    listeners.add(Listener(CharEvent::class.java as Class<CharEvent<Component>>, func))
}

fun RBuilder.onCursorEnter(func: (CursorEnterEvent<Component>) -> Unit) {
    listeners.add(Listener(CursorEnterEvent::class.java as Class<CursorEnterEvent<Component>>, func))
}

fun RBuilder.onFocus(func: (FocusEvent<Component>) -> Unit) {
    listeners.add(Listener(FocusEvent::class.java as Class<FocusEvent<Component>>, func))
}

fun RBuilder.onKey(func: (KeyEvent<Component>) -> Unit) {
    listeners.add(Listener(KeyEvent::class.java as Class<KeyEvent<Component>>, func))
}

fun RBuilder.onDrag(func: (MouseDragEvent<Component>) -> Unit) {
    listeners.add(Listener(MouseDragEvent::class.java as Class<MouseDragEvent<Component>>, func))
}

fun RBuilder.onScroll(func: (ScrollEvent<Component>) -> Unit) {
    listeners.add(Listener(ScrollEvent::class.java as Class<ScrollEvent<Component>>, func))
}


fun Component.onClick(func: (MouseClickEvent<Component>) -> Unit) {
    listenerMap.addListener(MouseClickEvent::class.java as Class<MouseClickEvent<Component>>) {
        if (it.isClick()) func(it)
    }
}

fun Component.onPress(func: (MouseClickEvent<Component>) -> Unit) {
    listenerMap.addListener(MouseClickEvent::class.java as Class<MouseClickEvent<Component>>) {
        if (it.isPress()) func(it)
    }
}

fun Component.onRelease(func: (MouseClickEvent<Component>) -> Unit) {
    listenerMap.addListener(MouseClickEvent::class.java as Class<MouseClickEvent<Component>>) {
        if (it.isRelease()) func(it)
    }
}

fun Component.onCharTyped(func: (CharEvent<Component>) -> Unit) {
    listenerMap.addListener(CharEvent::class.java as Class<CharEvent<Component>>, func)
}

fun Component.onCursorEnter(func: (CursorEnterEvent<Component>) -> Unit) {
    listenerMap.addListener(CursorEnterEvent::class.java as Class<CursorEnterEvent<Component>>, func)
}

fun Component.onFocus(func: (FocusEvent<Component>) -> Unit) {
    listenerMap.addListener(FocusEvent::class.java as Class<FocusEvent<Component>>, func)
}

fun Component.onKey(func: (KeyEvent<Component>) -> Unit) {
    listenerMap.addListener(KeyEvent::class.java as Class<KeyEvent<Component>>, func)
}

fun Component.onDrag(func: (MouseDragEvent<Component>) -> Unit) {
    listenerMap.addListener(MouseDragEvent::class.java as Class<MouseDragEvent<Component>>, func)
}

fun Component.onScroll(func: (ScrollEvent<Component>) -> Unit) {
    listenerMap.addListener(ScrollEvent::class.java as Class<ScrollEvent<Component>>, func)
}

inline fun MouseClickEvent<*>.isClick() = action == MouseClickEvent.MouseClickAction.CLICK
inline fun MouseClickEvent<*>.isPress() = action == MouseClickEvent.MouseClickAction.PRESS
inline fun MouseClickEvent<*>.isRelease() = action == MouseClickEvent.MouseClickAction.RELEASE