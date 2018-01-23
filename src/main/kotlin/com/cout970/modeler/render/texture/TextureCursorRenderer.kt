package com.cout970.modeler.render.texture

import com.cout970.glutilities.tessellator.DrawMode
import com.cout970.matrix.extensions.Matrix4
import com.cout970.modeler.controller.usecases.getTexturePolygons
import com.cout970.modeler.core.model.TRSTransformation
import com.cout970.modeler.gui.canvas.IRotable
import com.cout970.modeler.gui.canvas.IScalable
import com.cout970.modeler.gui.canvas.ITranslatable
import com.cout970.modeler.gui.canvas.helpers.CanvasHelper
import com.cout970.modeler.render.tool.AutoCache
import com.cout970.modeler.render.tool.RenderContext
import com.cout970.modeler.render.tool.createVao
import com.cout970.modeler.util.rotationTo
import com.cout970.vector.api.IVector3
import com.cout970.vector.extensions.Vector2
import com.cout970.vector.extensions.Vector3
import com.cout970.vector.extensions.toVector3
import com.cout970.vector.extensions.vec3Of

/**
 * Created by cout970 on 2017/06/15.
 */
class TextureCursorRenderer {

    var translationArrow = AutoCache()
    var rotationRing = AutoCache()
    var scaleArrow = AutoCache()

    fun renderCursor(ctx: RenderContext) {
        ctx.gui.modelAccessor.textureSelectionHandler.getSelection().ifNull { return }

        val cursor = ctx.gui.cursorManager.textureCursor ?: return
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

            cursor.getSelectablePartsTexture(ctx.gui, ctx.camera, ctx.viewport).forEach { part ->
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
//        renderDebugHitbox(ctx)
    }

    fun renderDebugHitbox(ctx: RenderContext) {
//        val cursor = ctx.gui.cursorManager.textureCursor ?: return

        val vao = run {
            ctx.buffer.build(DrawMode.LINES, false) {
                //                val polygons = cursor.getSelectablePartsTexture(ctx.gui, ctx.camera, ctx.viewport)
//                        .mapNotNull { part -> (part as? CursorPartRotateTexture)?.polygons }
//                        .flatten()

                val type = ctx.gui.state.selectionType
                val selection = ctx.gui.modelAccessor.modelSelection
                val model = ctx.gui.state.tmpModel ?: ctx.gui.modelAccessor.model
                val materialRef = ctx.gui.state.selectedMaterial
                val material = model.getMaterial(materialRef)
                val polygons = model.getTexturePolygons(selection, type, materialRef, material)
                        .map { it.first }


                polygons.flatMap { it.getEdges() }
                        .map {
                            CanvasHelper.fromMaterialToRender(it.first, material) to
                                    CanvasHelper.fromMaterialToRender(it.second, material)
                        }
                        .forEach {
                            val color = if (ctx.gui.cursorManager.cursorDrag.hovered == null) vec3Of(0.3) else vec3Of(
                                    1.0)
                            add(it.first.toVector3(0.0), Vector2.ZERO, Vector3.ORIGIN, color)
                            add(it.second.toVector3(0.0), Vector2.ZERO, Vector3.ORIGIN, color)
                        }
            }
        }

        ctx.shader.apply {
            useColor.setInt(1)
            useLight.setInt(0)
            useTexture.setInt(0)
            matrixM.setMatrix4(Matrix4.IDENTITY)
            accept(vao)
        }
        vao.close()
    }
}