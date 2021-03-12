package com.cout970.reactive.dsl

import org.joml.Vector4f
import org.liquidengine.legui.component.Component
import org.liquidengine.legui.component.TextComponent
import org.liquidengine.legui.component.optional.align.HorizontalAlign
import org.liquidengine.legui.component.optional.align.VerticalAlign
import org.liquidengine.legui.icon.Icon
import org.liquidengine.legui.style.border.SimpleLineBorder
import org.liquidengine.legui.style.color.ColorConstants
import org.liquidengine.legui.style.length.LengthType.pixel

fun Component.backgroundColor(func: () -> Vector4f) {
    style.background.color = func()
}

fun Component.backgroundIcon(func: () -> Icon) {
    style.background.icon = func()
}

fun Component.transparent() {
    style.background.color = ColorConstants.transparent()
}

fun Component.borderless() {
    style.border = null
}

fun Component.rectCorners() {
    style.setBorderRadius(0f)
}

var Component.borderSize: Float
    get() = (style.border as? SimpleLineBorder)?.thickness ?: 0f
    set(value) {
        (style.border as? SimpleLineBorder)?.thickness = value
    }

fun Component.borderColor(func: () -> Vector4f) {
    (style.border as? SimpleLineBorder)?.color = func()
}

fun Component.borderRadius(amount: Float) {
    style.setBorderBottomLeftRadius(amount)
    style.setBorderBottomRightRadius(amount)
    style.setBorderTopLeftRadius(amount)
    style.setBorderTopRightRadius(amount)
}

fun Component.padding(amount: Float) {
    padding(amount, amount, amount, amount)
}

fun Component.padding(left: Float, top: Float, right: Float, bottom: Float) {
    style.paddingTop = pixel(top)
    style.paddingLeft = pixel(left)
    style.paddingRight = pixel(right)
    style.paddingBottom = pixel(bottom)
}

fun Component.paddingTop(amount: Float) {
    style.paddingTop = pixel(amount)
}

fun Component.paddingBottom(amount: Float) {
    style.paddingBottom = pixel(amount)
}

fun Component.paddingLeft(amount: Float) {
    style.paddingLeft = pixel(amount)
}

fun Component.paddingRight(amount: Float) {
    style.paddingRight = pixel(amount)
}

var TextComponent.fontSize: Float
    get() = (this as Component).style.fontSize
    set(value) {
        (this as Component).style.fontSize = value
    }

var TextComponent.font: String
    get() = (this as Component).style.font
    set(value) {
        (this as Component).style.font = value
    }

var TextComponent.horizontalAlign: HorizontalAlign
    get() = (this as Component).style.horizontalAlign
    set(value) {
        (this as Component).style.horizontalAlign = value
    }

var TextComponent.verticalAlign: VerticalAlign
    get() = (this as Component).style.verticalAlign
    set(value) {
        (this as Component).style.verticalAlign = value
    }

fun TextComponent.textColor(func: () -> Vector4f) {
    (this as Component).style.textColor = func()
}

fun TextComponent.highlightColor(func: () -> Vector4f) {
    (this as Component).style.highlightColor = func()
}