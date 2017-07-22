package com.cout970.modeler.view.canvas

import com.cout970.glutilities.event.EnumKeyState
import com.cout970.glutilities.event.EventMouseClick
import com.cout970.modeler.core.config.Config
import com.cout970.modeler.util.absolutePosition
import com.cout970.modeler.util.isInside
import com.cout970.modeler.util.toIVector
import com.cout970.modeler.view.Gui
import org.funktionale.option.Option
import org.funktionale.option.firstOption

/**
 * Created by cout970 on 2017/07/22.
 */
class CanvasManager {

    lateinit var gui: Gui
    var lastClick = 0L

    fun onMouseClick(e: EventMouseClick): Boolean {

        if (e.keyState == EnumKeyState.PRESS) {
            when {
                Config.keyBindings.selectModelControls.check(e) -> return selectPart()
                Config.keyBindings.jumpCameraToCursor.check(e) -> return moveCamera()
            }
        }

        return false
    }

    private fun getCanvasUnderTheMouse(): Option<Canvas> {
        val pos = gui.input.mouse.getMousePos()
        val canvas = gui.canvasContainer.canvas
        val affectedCanvas = canvas.filter { pos.isInside(it.absolutePosition, it.size.toIVector()) }

        return affectedCanvas.firstOption()
    }

    fun selectPart(): Boolean {
        val canvas = getCanvasUnderTheMouse()
        canvas.forEach { gui.dispatcher.onEvent("canvas.select", it) }
        return canvas.isDefined()
    }

    fun moveCamera(): Boolean {
        val canvas = getCanvasUnderTheMouse()
        if (canvas.isDefined()) {
            if (System.currentTimeMillis() - lastClick < 500) {
                canvas.forEach { gui.dispatcher.onEvent("canvas.jump.camera", it) }
                lastClick = 0L
            } else {
                lastClick = System.currentTimeMillis()
            }
        }
        return canvas.isDefined()
    }
}