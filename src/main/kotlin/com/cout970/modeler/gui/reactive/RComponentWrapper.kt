package com.cout970.modeler.gui.reactive

import com.cout970.modeler.gui.leguicomp.childs
import org.joml.Vector2f
import org.liquidengine.legui.component.Component
import org.liquidengine.legui.component.Panel
import org.liquidengine.legui.style.Style

/**
 * Created by cout970 on 2017/09/24.
 */

class RComponentWrapper<out C : RComponent<P, S>, P : Any, S : Any>(
        val props: P,
        val spec: () -> C
) : Component() {

    var initialized = false
    lateinit var component: RComponent<P, S>

    val subTree: Component
        get() = try {
            childs.firstOrNull() ?: Panel()
        } catch (e: Exception) {
            Panel()
        }

    fun init(ctx: RContext) {
        component = spec()
        component.transferProps(props)
        component.context = ctx
        component.seal()
        initialized = true
    }

    fun buildSubTree(ctx: RBuilder): Component {
        return component.build(ctx)
    }

    fun onUpdateChild() {
        position = subTree.position
        size = subTree.size
        subTree.position = Vector2f()
    }

    override fun isEnabled(): Boolean {
        return subTree.isEnabled
    }

    override fun isVisible(): Boolean {
        return subTree.isVisible
    }

    override fun isHovered(): Boolean {
        return subTree.isHovered
    }

    override fun isFocused(): Boolean {
        return subTree.isFocused
    }

    override fun isPressed(): Boolean {
        return subTree.isPressed
    }

    override fun setEnabled(enabled: Boolean) {
        subTree.isEnabled = enabled
    }

    override fun getMetadata(): MutableMap<String, Any> {
        return subTree.metadata
    }

    override fun setHovered(hovered: Boolean) {
        subTree.isHovered = hovered
    }

    override fun setFocused(focused: Boolean) {
        subTree.isFocused = focused
    }

    override fun setPressed(pressed: Boolean) {
        subTree.isPressed = pressed
    }

    override fun setSize(width: Float, height: Float) {
        subTree.setSize(width, height)
    }

    override fun getStyle(): Style {
        return subTree.getStyle()
    }

    override fun setStyle(style: Style?) {
        subTree.setStyle(style)
    }
}