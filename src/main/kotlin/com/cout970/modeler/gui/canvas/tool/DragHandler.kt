package com.cout970.modeler.gui.canvas.tool

import com.cout970.glutilities.device.Mouse
import com.cout970.modeler.gui.Gui
import com.cout970.vector.api.IVector2

interface IDragListener {

    fun onStart(startMousePos: IVector2) = Unit
    fun onTick(startMousePos: IVector2, endMousePos: IVector2) = Unit
    fun onEnd(startMousePos: IVector2, endMousePos: IVector2) = Unit
    fun onNoDrag() = Unit
}

class DragListenerCombinator(val a: IDragListener, val b: IDragListener) : IDragListener {

    override fun onStart(startMousePos: IVector2) {
        a.onStart(startMousePos)
        b.onStart(startMousePos)
    }

    override fun onTick(startMousePos: IVector2, endMousePos: IVector2) {
        a.onTick(startMousePos, endMousePos)
        b.onTick(startMousePos, endMousePos)
    }

    override fun onEnd(startMousePos: IVector2, endMousePos: IVector2) {
        a.onEnd(startMousePos, endMousePos)
        b.onEnd(startMousePos, endMousePos)
    }

    override fun onNoDrag() {
        a.onNoDrag()
        b.onNoDrag()
    }
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

data class MouseState(val mousePress: Boolean, val mousePos: IVector2) {


    companion object {

        fun from(gui: Gui): MouseState {
            val mousePos = gui.input.mouse.getMousePos()
            val click = gui.input.mouse.isButtonPressed(Mouse.BUTTON_LEFT)

            return MouseState(click, mousePos)
        }

    }
}