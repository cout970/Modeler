package com.cout970.modeler.gui.canvas.layout

import com.cout970.glutilities.event.EventKeyUpdate
import com.cout970.modeler.gui.Gui
import com.cout970.modeler.gui.canvas.CanvasContainer

/**
 * Created by cout970 on 2017/05/03.
 */
interface ICanvasLayout {

    val container: CanvasContainer

    fun updateCanvas()

    fun onEvent(gui: Gui, e: EventKeyUpdate): Boolean {
        return false
    }

    fun runAction(action: String)
}