package com.cout970.modeler.newView.gui.canvas.layout

import com.cout970.modeler.newView.gui.canvas.CanvasContainer

/**
 * Created by cout970 on 2017/05/03.
 */
interface ICanvasLayout {

    val container: CanvasContainer

    fun updateCanvas()
}