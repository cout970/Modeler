package com.cout970.modeler.gui.canvas.helpers

import com.cout970.modeler.core.config.Config
import com.cout970.modeler.gui.canvas.Canvas
import com.cout970.modeler.gui.canvas.IScalable
import com.cout970.modeler.gui.canvas.SceneSpaceContext
import com.cout970.modeler.input.event.IInput
import com.cout970.modeler.util.MatrixUtils
import com.cout970.modeler.util.toIVector
import com.cout970.modeler.util.toJOML
import com.cout970.vector.api.IVector2
import com.cout970.vector.extensions.*
import org.joml.Matrix4d

/**
 * Created by cout970 on 2017/04/08.
 */
object ScaleHelper {

    fun getOffset(obj: IScalable, canvas: Canvas, input: IInput, oldContext: SceneSpaceContext,
                  newContext: SceneSpaceContext): Float {

        val matrix = canvas.cameraHandler.camera.getMatrix(canvas.size.toIVector()).toJOML()
        val axis = getScaleAxis(matrix, obj)
        val viewportSize = canvas.size.toIVector()

        val oldMousePos = ((oldContext.mousePos / viewportSize) * 2 - 1).run { vec2Of(x, -yd) }
        val newMousePos = ((newContext.mousePos / viewportSize) * 2 - 1).run { vec2Of(x, -yd) }

        val old = axis.project(oldMousePos * viewportSize)
        val new = axis.project(newMousePos * viewportSize)

        val move = (new - old) * canvas.cameraHandler.camera.zoom / Config.cursorArrowsSpeed

        // Move using increments of 1, 1/4, 1/16
        val offset: Float

        if (Config.keyBindings.disableGridMotion.check(input)) {
            offset = Math.round(move * 16) / 16f
        } else if (Config.keyBindings.disablePixelGridMotion.check(input)) {
            offset = Math.round(move * 4) / 4f
        } else {
            offset = Math.round(move).toFloat()
        }
        return offset
    }

    fun getScaleAxis(matrix: Matrix4d, obj: IScalable): IVector2 {
        val diff = projectAxis(matrix, obj)

        return diff.second - diff.first
    }

    fun projectAxis(matrix: Matrix4d, obj: IScalable): Pair<IVector2, IVector2> {
        return MatrixUtils.projectAxis(matrix, obj.scaleAxis)
    }
}