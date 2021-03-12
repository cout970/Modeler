package com.cout970.reactive.dsl

import org.liquidengine.legui.component.Component
import org.liquidengine.legui.style.Style


var Component.posX
    get() = position.x
    set(x) {
        position.x = x
    }

var Component.posY
    get() = position.y
    set(y) {
        position.y = y
    }

var Component.sizeX
    get() = size.x
    set(x) {
        size.x = x
    }

var Component.sizeY
    get() = size.y
    set(y) {
        size.y = y
    }


var Component.width
    get() = size.x
    set(x) {
        size.x = x
    }

var Component.height
    get() = size.y
    set(y) {
        size.y = y
    }


fun Component.enable() {
    isEnabled = true
}

fun Component.disable() {
    isEnabled = false
}

fun Component.displayBlock() {
    style.display = Style.DisplayType.MANUAL
}

fun Component.displayNone() {
    style.display = Style.DisplayType.NONE
}

fun Component.displayFlex() {
    style.display = Style.DisplayType.FLEX
}

fun Component.hide() {
    isEnabled = false
    displayNone()
}

fun Component.show() {
    isEnabled = true
    displayBlock()
}

fun Component.floatTop(padding: Float, margin: Float = 0f) {
    var y = margin
    childComponents.forEach {
        it.posY = y
        y += it.sizeY + padding
    }
}

fun Component.floatBottom(padding: Float, margin: Float = 0f) {
    var y = parent.sizeY - margin
    childComponents.forEach {
        it.posY = y - it.sizeY
        y -= it.sizeY + padding
    }
}


fun Component.floatLeft(padding: Float, margin: Float = 0f) {
    var x = margin
    childComponents.forEach {
        it.posX = x
        x += it.sizeX + padding
    }
}

fun Component.floatRight(padding: Float, margin: Float = 0f) {
    var x = parent.sizeX - margin
    childComponents.forEach {
        it.posX = x - it.sizeX
        x -= it.sizeX + padding
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
