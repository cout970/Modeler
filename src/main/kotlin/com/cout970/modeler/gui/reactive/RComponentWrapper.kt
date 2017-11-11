package com.cout970.modeler.gui.reactive

import org.joml.Vector2f
import org.joml.Vector4f
import org.liquidengine.legui.border.Border
import org.liquidengine.legui.color.ColorConstants.transparent
import org.liquidengine.legui.component.Component
import org.liquidengine.legui.component.Panel

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

    fun buildSubTree(ctx: RBuildContext): Component {
        return component.build(ctx)
    }

    fun onUpdateChild() {
        position = subTree.position
        size = subTree.size
        subTree.position = Vector2f()
    }


    override fun getBackgroundColor(): Vector4f {
        return transparent()
    }

    override fun getFocusedStrokeColor(): Vector4f {
        return transparent()
    }

    override fun getCornerRadius(): Float {
        return subTree.cornerRadius
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

    override fun setVisible(visible: Boolean) {
        subTree.isVisible = visible
    }

    override fun getMetadata(): MutableMap<String, Any> {
        return subTree.metadata
    }

    override fun setBorder(border: Border?) {
        subTree.border = border
    }

    override fun setCornerRadius(cornerRadius: Float) {
        subTree.cornerRadius = cornerRadius
    }

    override fun setHovered(hovered: Boolean) {
        subTree.isHovered = hovered
    }

    override fun setFocused(focused: Boolean) {
        subTree.isFocused = focused
    }

    override fun setFocusedStrokeColor(r: Float, g: Float, b: Float, a: Float) {
        subTree.setFocusedStrokeColor(r, g, b, a)
    }

    override fun setPressed(pressed: Boolean) {
        subTree.isPressed = pressed
    }

    override fun setSize(width: Float, height: Float) {
        subTree.setSize(width, height)
    }
}