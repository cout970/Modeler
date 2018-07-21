package com.cout970.modeler.gui.canvas.tool

import com.cout970.modeler.gui.Gui
import com.cout970.modeler.gui.canvas.input.MouseState
import com.cout970.vector.api.IVector2

interface IDragListener {

    fun onStart(startMousePos: IVector2) = Unit
    fun onTick(startMousePos: IVector2, endMousePos: IVector2) = Unit
    fun onEnd(startMousePos: IVector2, endMousePos: IVector2) = Unit
    fun onNoDrag() = Unit
}

class DragHandler(val listener: IDragListener) {

    var startMousePos: IVector2? = null

    fun tick(gui: Gui) {
        val mouse = MouseState.from(gui)

        when {
            !isDragging() && mouse.mousePress -> {
                startMousePos = mouse.mousePos
                listener.onStart(mouse.mousePos)
            }
            isDragging() && !mouse.mousePress -> {
                listener.onEnd(startMousePos!!, mouse.mousePos)
                startMousePos = null
            }
        }
        if (isDragging()) {
            listener.onTick(startMousePos!!, mouse.mousePos)
        } else {
            listener.onNoDrag()
        }
    }

    fun isDragging() = startMousePos != null
}