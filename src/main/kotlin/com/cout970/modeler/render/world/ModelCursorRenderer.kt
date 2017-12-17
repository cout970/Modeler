package com.cout970.modeler.render.world

import com.cout970.modeler.core.model.TRSTransformation
import com.cout970.modeler.gui.canvas.IRotable
import com.cout970.modeler.gui.canvas.IScalable
import com.cout970.modeler.gui.canvas.ITranslatable
import com.cout970.modeler.render.tool.AutoCache
import com.cout970.modeler.render.tool.RenderContext
import com.cout970.modeler.render.tool.createVao
import com.cout970.modeler.util.rotationTo
import com.cout970.vector.api.IVector3
import com.cout970.vector.extensions.Vector3
import com.cout970.vector.extensions.vec3Of

/**
 * Created by cout970 on 2017/06/15.
 */
class ModelCursorRenderer {

    var translationArrow = AutoCache()
    var rotationRing = AutoCache()
    var scaleArrow = AutoCache()

    fun renderCursor(ctx: RenderContext) {

        ctx.gui.modelAccessor.modelSelectionHandler.getSelection().ifNull { return }

        val cursor = ctx.gui.cursorManager.modelCursor ?: return
        val parameters = cursor.getCursorParameters(ctx.camera, ctx.viewport)

        val translationArrow = translationArrow.getOrCreate(ctx) {
            ctx.gui.resources.translationArrow.createVao(ctx.buffer, vec3Of(1, 1, 1))
        }
        val rotationRing = rotationRing.getOrCreate(ctx) {
            ctx.gui.resources.rotationRing.createVao(ctx.buffer, vec3Of(1, 1, 1))
        }
        val scaleArrow = scaleArrow.getOrCreate(ctx) {
            ctx.gui.resources.scaleArrow.createVao(ctx.buffer, vec3Of(1, 1, 1))
        }
        ctx.shader.apply {
            useColor.setInt(1)
            useLight.setInt(0)
            useTexture.setInt(0)
            val hovered = ctx.gui.state.hoveredObject

            cursor.getSelectablePartsModel(ctx.gui, ctx.camera, ctx.viewport).forEach { part ->
                val selected = hovered == part

                val scale = vec3Of(parameters.length / 16f)
                val colorFunc = { col: IVector3 -> if (selected) vec3Of(1) else col }

                when (part) {
                    is ITranslatable -> {

                        matrixM.setMatrix4(TRSTransformation(
                                translation = cursor.center,
                                rotation = Vector3.X_AXIS rotationTo part.translationAxis,
                                scale = scale
                        ).matrix)
                        globalColor.setVector3(colorFunc(part.translationAxis))
                        accept(translationArrow)
                        globalColor.setVector3(Vector3.ONE)
                    }
                    is IRotable -> {
                        matrixM.setMatrix4(TRSTransformation(
                                translation = cursor.center,
                                rotation = Vector3.Y_AXIS rotationTo part.tangent,
                                scale = scale
                        ).matrix)
                        globalColor.setVector3(colorFunc(part.tangent))
                        accept(rotationRing)
                        globalColor.setVector3(Vector3.ONE)
                    }
                    is IScalable -> {
                        matrixM.setMatrix4(TRSTransformation(
                                translation = cursor.center,
                                rotation = Vector3.X_AXIS rotationTo part.scaleAxis,
                                scale = scale
                        ).matrix)
                        globalColor.setVector3(colorFunc(part.scaleAxis))
                        accept(scaleArrow)
                        globalColor.setVector3(Vector3.ONE)
                    }
                }
            }
        }
    }
}