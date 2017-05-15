package com.cout970.modeler.view.gui.canvas

import com.cout970.modeler.view.gui.canvas.layout.ColumnLayout
import com.cout970.modeler.view.gui.canvas.layout.ICanvasLayout
import org.liquidengine.legui.component.Panel

/**
 * Created by cout970 on 2017/05/03.
 */
class CanvasContainer(val panel: Panel) {

    var layout: ICanvasLayout = ColumnLayout(this)
    val canvas = mutableListOf<Canvas>()
    var selectedCanvas: Canvas? = null

    private val canvasBuffer = mutableListOf<Canvas>()

    fun newCanvas() {
        if (canvasBuffer.isEmpty()) {
            canvas.add(Canvas())
        } else {
            val last = canvasBuffer.removeAt(canvasBuffer.size - 1)
            canvas.add(last)
        }
        refreshCanvass()
    }

    fun removeCanvas(index: Int) {
        val canvas = canvas.removeAt(index)
        canvasBuffer.add(canvas)
        refreshCanvass()
    }

    fun refreshCanvass() {
        panel.clearComponents()
        for (canvas in canvas) {
            panel.addComponent(canvas)
        }
        selectedCanvas = canvas.firstOrNull()
    }
}