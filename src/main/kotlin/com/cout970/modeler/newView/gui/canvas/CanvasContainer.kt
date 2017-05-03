package com.cout970.modeler.newView.gui.canvas

import org.liquidengine.legui.component.Panel

/**
 * Created by cout970 on 2017/05/03.
 */
class CanvasContainer(val panel: Panel) {

    var layout = CanvasLayout()
    val canvas = mutableListOf<Canvas>()
    var selectedCanvas: Canvas? = null

    private val canvasBuffer = mutableListOf<Canvas>()

    fun addCanvas(func: () -> Canvas) {
        if (canvasBuffer.isEmpty()) {
            canvas.add(func())
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