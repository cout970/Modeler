package com.cout970.modeler.newView.selector

import com.cout970.modeler.config.Config
import com.cout970.modeler.event.IInput
import com.cout970.modeler.model.Model
import com.cout970.modeler.newView.Scene
import com.cout970.modeler.util.MatrixUtils
import com.cout970.modeler.util.toIVector
import com.cout970.modeler.util.toJOML
import com.cout970.modeler.view.controller.SceneSpaceContext
import com.cout970.modeler.view.controller.selection.ITranslatable
import com.cout970.vector.api.IVector2
import com.cout970.vector.extensions.*
import org.joml.Matrix4d

/**
 * Created by cout970 on 2017/04/08.
 */
object TranslationHelper {

    fun applyTranslation(model: Model, scene: Scene, obj: ITranslatable, input: IInput,
                         context: Pair<SceneSpaceContext, SceneSpaceContext>): Model {

        val axis = getTranslationAxis(scene.getMatrixMVP().toJOML(), obj)
        val offset = getOffset(axis, scene, input, context.first, context.second)
        return obj.applyTranslation(offset, model)
    }

    fun getOffset(axis: IVector2, scene: Scene, input: IInput, oldContext: SceneSpaceContext,
                  newContext: SceneSpaceContext): Float {

        val viewportSize = scene.size.toIVector()

        val oldMousePos = ((oldContext.mousePos / viewportSize) * 2 - 1).run { vec2Of(x, -yd) }
        val newMousePos = ((newContext.mousePos / viewportSize) * 2 - 1).run { vec2Of(x, -yd) }

        val old = axis.project(oldMousePos * viewportSize)
        val new = axis.project(newMousePos * viewportSize)

        val move = (new - old) * scene.cameraHandler.camera.zoom / Config.cursorArrowsSpeed

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

    fun getTranslationAxis(matrix: Matrix4d, obj: ITranslatable): IVector2 {
        val diff = projectAxis(matrix, obj)

        return diff.second - diff.first
    }

    fun projectAxis(matrix: Matrix4d, obj: ITranslatable): Pair<IVector2, IVector2> {
        return MatrixUtils.projectAxis(matrix, obj.translationAxis)
    }
}