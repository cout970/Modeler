package com.cout970.modeler.gui.canvas.helpers

import com.cout970.modeler.core.config.Config
import com.cout970.modeler.gui.canvas.Canvas
import com.cout970.modeler.gui.canvas.ITranslatable
import com.cout970.modeler.gui.canvas.SceneSpaceContext
import com.cout970.modeler.input.event.IInput
import com.cout970.modeler.util.MatrixUtils
import com.cout970.modeler.util.toIVector
import com.cout970.modeler.util.toJOML
import com.cout970.vector.api.IVector2
import com.cout970.vector.api.IVector3
import com.cout970.vector.extensions.*
import org.joml.Matrix4d

/**
 * Created by cout970 on 2017/04/08.
 */
object TranslationHelper {

    fun getOffset(obj: ITranslatable, canvas: Canvas, input: IInput, oldContext: SceneSpaceContext,
                  newContext: SceneSpaceContext): Float {
        return getOffset(obj.translationAxis, canvas, input, oldContext, newContext)
    }

    fun getOffset(direction: IVector3, canvas: Canvas, input: IInput, oldContext: SceneSpaceContext,
                  newContext: SceneSpaceContext): Float {

        val matrix = canvas.cameraHandler.camera.getMatrix(canvas.size.toIVector()).toJOML()
        val axis = getTranslationAxis(matrix, direction)
        val viewportSize = canvas.size.toIVector()

        val oldMousePos = ((oldContext.mousePos / viewportSize) * 2 - 1).run { vec2Of(x, -yd) }
        val newMousePos = ((newContext.mousePos / viewportSize) * 2 - 1).run { vec2Of(x, -yd) }

        val old = axis.project(oldMousePos * viewportSize)
        val new = axis.project(newMousePos * viewportSize)

        val move = (new - old) * canvas.cameraHandler.camera.zoom / Config.cursorArrowsSpeed

        // Move using increments of 1, 1/4, 1/16
        val offset: Float

        offset = when {
            Config.keyBindings.disableGridMotion.check(input) -> Math.round(move * 16) / 16f
            Config.keyBindings.disablePixelGridMotion.check(input) -> Math.round(move * 4) / 4f
            else -> Math.round(move).toFloat()
        }
        return offset
    }

    fun getTranslationAxis(matrix: Matrix4d, axis: IVector3): IVector2 {
        val diff = MatrixUtils.projectAxis(matrix, axis)

        return diff.second - diff.first
    }
}