package com.cout970.modeler.gui.canvas

import com.cout970.modeler.gui.canvas.layout.*
import com.cout970.modeler.gui.leguicomp.Panel
import com.cout970.modeler.gui.leguicomp.defaultTextColor
import com.cout970.reactive.dsl.posX
import com.cout970.reactive.dsl.posY
import org.liquidengine.legui.component.Label

/**
 * Created by cout970 on 2017/05/03.
 */
class CanvasContainer(var panel: Panel) {

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
            canvas.add(Canvas().also { c ->
                c.add(Label("Canvas ${canvas.size + 1}").also { l ->
                    l.defaultTextColor()
                    l.posX = 10f
                    l.posY = 8f
                    l.style.fontSize = 20f
                })
            })
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
        val nonCanvas = panel.childComponents.filter { it !is Canvas }
        panel.clearChildComponents()
        nonCanvas.forEach { panel.add(it) }
        canvas.forEach { panel.add(it) }
        selectedCanvas = canvas.firstOrNull()
    }
}