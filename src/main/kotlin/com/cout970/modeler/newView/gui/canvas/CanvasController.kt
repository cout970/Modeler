package com.cout970.modeler.newView.gui.canvas

/**
 * Created by cout970 on 2017/05/02.
 */
abstract class CanvasController {

    val state = CanvasState()
    abstract val canvas: Canvas
    abstract val rendererProvider: IRendererProvider

    open fun update() {}
}