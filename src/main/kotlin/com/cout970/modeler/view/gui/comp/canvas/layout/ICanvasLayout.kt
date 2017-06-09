package com.cout970.modeler.view.gui.comp.canvas.layout

import com.cout970.glutilities.event.EventKeyUpdate
import com.cout970.modeler.view.GuiState
import com.cout970.modeler.view.gui.comp.canvas.CanvasContainer

/**
 * Created by cout970 on 2017/05/03.
 */
interface ICanvasLayout {

    val container: CanvasContainer

    fun updateCanvas()

    fun onEvent(guiState: GuiState, e: EventKeyUpdate): Boolean {
        return false
    }
}