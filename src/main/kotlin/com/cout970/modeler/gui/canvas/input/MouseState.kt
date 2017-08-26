package com.cout970.modeler.gui.canvas.input

import com.cout970.glutilities.device.Mouse
import com.cout970.modeler.gui.Gui
import com.cout970.vector.api.IVector2

/**
 * Created by cout970 on 2017/08/16.
 */

data class MouseState(val mousePress: Boolean, val mousePos: IVector2){


    companion object {

        fun from(gui: Gui): MouseState {
            val mousePos = gui.input.mouse.getMousePos()
            val click = gui.input.mouse.isButtonPressed(Mouse.BUTTON_LEFT)

            return MouseState(click, mousePos)
        }

    }
}

