package com.cout970.modeler.gui.react.tests

import org.joml.Vector2f
import org.joml.Vector4f
import org.liquidengine.legui.border.Border
import org.liquidengine.legui.component.Component
import org.liquidengine.legui.component.Panel
import org.liquidengine.legui.intersection.Intersector
import org.liquidengine.legui.listener.ListenerMap

/**
 * Created by cout970 on 2017/09/24.
 */

class RComponentWrapper<out C : RComponent<P, S>, P : Any, S : Any>(
        val props: P,
        val spec: RComponentSpec<C, P, S>
) : Panel<Component>() {

    lateinit var component: RComponent<P, S>

    val subTree: Component
        get() = try {
            childs.firstOrNull() ?: Panel<Component>()
        } catch (e: Exception) {
            Panel<Component>()
        }

    fun init(ctx: RContext) {
        component = spec.build(props)
        component.context = ctx
        component.seal()
    }

    fun buildSubTree(ctx: RBuildContext): Component {
        return component.build(ctx)
    }

    override fun getSize(): Vector2f {
        return subTree.size
    }

    override fun getPosition(): Vector2f {
        return subTree.position
    }

    override fun getBorder(): Border {
        return subTree.border
    }

    override fun getListenerMap(): ListenerMap {
        return subTree.listenerMap
    }

    override fun getBackgroundColor(): Vector4f {
        return subTree.backgroundColor
    }

    override fun getFocusedStrokeColor(): Vector4f {
        return subTree.focusedStrokeColor
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

    override fun getIntersector(): Intersector {
        return subTree.intersector
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

    override fun setListenerMap(listenerMap: ListenerMap?) {
        subTree.listenerMap = listenerMap
    }

    override fun setPosition(position: Vector2f?) {
        subTree.position = position
    }

    override fun setSize(size: Vector2f?) {
        subTree.size = size
    }

    override fun setBackgroundColor(backgroundColor: Vector4f?) {
        subTree.backgroundColor = backgroundColor
    }

    override fun setBackgroundColor(r: Float, g: Float, b: Float, a: Float) {
        subTree.setBackgroundColor(r, g, b, a)
    }

    override fun setFocusedStrokeColor(focusedStrokeColor: Vector4f?) {
        subTree.focusedStrokeColor = focusedStrokeColor
    }

    override fun setEnabled(enabled: Boolean) {
        subTree.isEnabled = enabled
    }

    override fun setVisible(visible: Boolean) {
        subTree.isVisible = visible
    }

    override fun intersects(point: Vector2f?): Boolean {
        return subTree.intersects(point)
    }

    override fun setIntersector(intersector: Intersector?) {
        subTree.intersector = intersector
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

    override fun setPosition(x: Float, y: Float) {
        subTree.setPosition(x, y)
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