package com.cout970.modeler.gui.canvas

import com.cout970.glutilities.event.EnumKeyState
import com.cout970.glutilities.event.EventMouseClick
import com.cout970.modeler.core.config.Config
import com.cout970.modeler.gui.Gui
import com.cout970.modeler.util.*

/**
 * Created by cout970 on 2017/07/22.
 */
class CanvasManager {

    lateinit var gui: Gui
    var lastClick = 0L


    fun updateSelectedCanvas() {
        val mousePos = gui.input.mouse.getMousePos()
        gui.canvasContainer.canvas.forEach { canvas ->
            if (mousePos.isInside(canvas.absolutePositionV, canvas.size.toIVector())) {
                gui.canvasContainer.selectedCanvas = canvas
            }
        }
    }

    fun onMouseClick(e: EventMouseClick): Boolean {
        if (gui.state.popup != null) return false

        if (e.keyState == EnumKeyState.PRESS) {
            when {
                Config.keyBindings.selectModel.check(e) -> return selectPart()
                Config.keyBindings.jumpCameraToCursor.check(e) -> return moveCamera()
            }
        }
        return false
    }

    fun getCanvasUnderTheMouse(): Nullable<Canvas> {
        val pos = gui.input.mouse.getMousePos()
        val canvas = gui.canvasContainer.canvas
        val affectedCanvas = canvas.filter { pos.isInside(it.absolutePositionV, it.size.toIVector()) }

        return affectedCanvas.firstOrNull().asNullable()
    }

    fun selectPart(): Boolean {
        val canvas = getCanvasUnderTheMouse()
        canvas.ifNotNull {
            gui.dispatcher.onEvent("canvas.select.model", it)
            return true
        }
        return false
    }

    fun moveCamera(): Boolean {
        val canvas = getCanvasUnderTheMouse()
        canvas.ifNotNull {
            lastClick = if (System.currentTimeMillis() - lastClick < 500) {
                gui.dispatcher.onEvent("canvas.jump.camera", it); 0L
            } else {
                System.currentTimeMillis()
            }
            return true
        }
        return false
    }
}