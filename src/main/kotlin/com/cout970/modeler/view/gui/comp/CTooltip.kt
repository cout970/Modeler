package com.cout970.modeler.view.gui.comp

import org.joml.Vector2f
import org.liquidengine.legui.component.Controller
import org.liquidengine.legui.component.Tooltip
import org.liquidengine.legui.component.optional.TextState

/**
 * Created by cout970 on 2017/07/17.
 */
class CTooltip(str: String) : Tooltip(str) {

    var lastRender = 0L
    var lastTimer = 0L
    var timer = 0

    override fun getTextState(): TextState {
        lastRender = System.currentTimeMillis()
        val state = super.getTextState()
        size = Vector2f(state.fontSize * state.length() / 2, 20f)
        if (controller != null) {
            position.x = (controller.size.x - size.x) / 2f
            if (super.getScreenPosition().x < 0f) {
                position.x -= super.getScreenPosition().x
            }
        }
        return state
    }

    override fun setController(controller: Controller) {
        super.setController(controller)
        position.y = controller.size.y
        position.x = (controller.size.x - size.x) / 2f
    }

    override fun getScreenPosition(): Vector2f {
        if (System.currentTimeMillis() - lastRender > 500) {
            timer = 0
        } else {
            timer += (System.currentTimeMillis() - lastTimer).toInt()
        }
        lastTimer = System.currentTimeMillis()
        if (timer > 1000) {
            return super.getScreenPosition()
        }
        return Vector2f(-9999f, -9999f)
    }
}