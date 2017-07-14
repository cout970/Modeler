package com.cout970.modeler.view.gui.comp.canvas

import com.cout970.modeler.view.gui.comp.CPanel
import com.cout970.modeler.view.gui.comp.canvas.layout.*

/**
 * Created by cout970 on 2017/05/03.
 */
class CanvasContainer(val panel: CPanel) {

    val layoutOne = LayoutOne(this)
    val layoutTwo = LayoutTwo(this)
    val layoutThree = LayoutThree(this)
    val layoutFourth = LayoutFourth(this)

    var layout: ICanvasLayout = layoutOne
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
        refreshCanvas()
    }

    fun removeCanvas(index: Int) {
        val canvas = canvas.removeAt(index)
        canvasBuffer.add(canvas)
        refreshCanvas()
    }

    fun selectLayout() {
        layout = when (canvas.size) {
            0, 1 -> layoutOne
            2 -> layoutTwo
            3 -> layoutThree
            else -> layoutFourth
        }
        refreshCanvas()
    }

    fun refreshCanvas() {
        val nonCanvas = panel.childs.filter { it !is Canvas }
        panel.clearChilds()
        nonCanvas.forEach { panel.add(it) }
        canvas.forEach { panel.add(it) }
        selectedCanvas = canvas.firstOrNull()
    }
}