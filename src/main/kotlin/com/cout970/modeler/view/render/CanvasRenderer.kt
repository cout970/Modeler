package com.cout970.modeler.view.render

import com.cout970.modeler.util.FloatArrayList
import com.cout970.modeler.util.absolutePosition
import com.cout970.modeler.util.toIVector
import com.cout970.modeler.view.gui.GuiUpdater
import com.cout970.modeler.view.gui.canvas.Canvas
import com.cout970.vector.extensions.vec2Of
import com.cout970.vector.extensions.yf

/**
 * Created by cout970 on 2017/05/16.
 */
class CanvasRenderer(val renderManager: RenderManager) {

    fun render(updater: GuiUpdater) {

        val windowHandler = renderManager.windowHandler

        updater.canvasContainer.canvas.forEach { canvas ->
            val viewportPos = vec2Of(
                    canvas.absolutePosition.x,
                    windowHandler.window.size.yf - (canvas.absolutePosition.yf + canvas.size.y)
            )
            windowHandler.saveViewport(viewportPos, canvas.size.toIVector()) {
                renderCanvas(canvas)
            }
        }
    }

    fun renderCanvas(canvas: Canvas) {
        val list = FloatArrayList()
    }
}