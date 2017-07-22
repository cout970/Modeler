package com.cout970.modeler.view.canvas.layout

import com.cout970.glutilities.event.EventKeyUpdate
import com.cout970.modeler.view.Gui
import com.cout970.modeler.view.canvas.CanvasContainer

/**
 * Created by cout970 on 2017/05/03.
 */
interface ICanvasLayout {

    val container: CanvasContainer

    fun updateCanvas()

    fun onEvent(gui: Gui, e: EventKeyUpdate): Boolean {
        return false
    }
}