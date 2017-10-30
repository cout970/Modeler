package com.cout970.modeler.gui.comp

import org.joml.Vector2f
import org.liquidengine.legui.component.Component
import org.liquidengine.legui.component.Tooltip

/**
 * Created by cout970 on 2017/07/17.
 */
class CTooltip(str: String) : Tooltip(str) {

    var lastRender = 0L
    var lastTimer = 0L
    var timer = 0

    constructor() : this("")

    override fun setComponent(component: Component?) {
        super.setComponent(component)
        if (component != null && getComponent() == component) {
            position.x = (component.size.x - size.x) / 2f
            if (super.getAbsolutePosition().x < 0f) {
                position.x -= super.getAbsolutePosition().x
            }
            position.y = component.size.y
            val state = super.getTextState()
            size = Vector2f(state.fontSize * state.length() / 2, 20f)
        }
    }

//    override fun getTextState(): TextState {
//        lastRender = System.currentTimeMillis()
//        val state = super.getTextState()
//        size = Vector2f(state.fontSize * state.length() / 2, 20f)
//        if (controller != null) {
//            position.x = (controller.size.x - size.x) / 2f
//            if (super.getScreenPosition().x < 0f) {
//                position.x -= super.getScreenPosition().x
//            }
//        }
//        return state
//    }
//
//    override fun setController(controller: Controller) {
//        super.setController(controller)
//        position.y = controller.size.y
//        position.x = (controller.size.x - size.x) / 2f
//    }
//
//    override fun getAbsolutePosition(): Vector2f {
//        if (System.currentTimeMillis() - lastRender > 500) {
//            timer = 0
//        } else {
//            timer += (System.currentTimeMillis() - lastTimer).toInt()
//        }
//        lastTimer = System.currentTimeMillis()
//        if (timer > 1000) {
//            return super.getAbsolutePosition()
//        }
//        return Vector2f(-9999f, -9999f)
//    }
}