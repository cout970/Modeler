package com.cout970.modeler.view.render.world

import com.cout970.matrix.extensions.Matrix4
import com.cout970.modeler.core.model.AABB
import com.cout970.modeler.util.RenderUtil
import com.cout970.modeler.view.canvas.cursor.CursorPartTranslate
import com.cout970.modeler.view.render.tool.AutoCache
import com.cout970.modeler.view.render.tool.CacheFrags
import com.cout970.modeler.view.render.tool.RenderContext
import org.lwjgl.opengl.GL11

/**
 * Created by cout970 on 2017/06/15.
 */
class CursorRenderer {

    var translationArrow = AutoCache()
    var cursorCache = AutoCache(CacheFrags.MODEL, CacheFrags.SELECTION_MODEL)

    fun renderCursor(ctx: RenderContext) {

//        if (ctx.gui.selectionHandler.getSelection() == null) {
//            return
//        }
//        val cursor = ctx.gui.selector.cursor
//        val parameters = cursor.getCursorParameters(ctx.camera, ctx.viewport)
//
//        val vao = translationArrow.getOrCreate(ctx) {
//            ctx.gui.resources.translationArrow.createVao(ctx.buffer, vec3Of(1, 1, 1))
//        }
//        ctx.shader.apply {
//            useColor.setInt(1)
//            useLight.setInt(0)
//            useTexture.setInt(0)
//            val hovered = ctx.gui.state.hoveredObject
//            val hold = ctx.gui.state.holdingSelection
//            cursor.getSelectableParts(ctx.gui, ctx.camera, ctx.viewport).forEach { part ->
//                val selected = hovered == part || hold == part
//                if (part is ITranslatable) {
//
//                    val scale = vec3Of(parameters.length / 16f) + if (selected)
//                        vec3Of(0, 1, 1) * parameters.length / 2 * 1 / 16f
//                    else
//                        vec3Of(0f)
//
//                    matrixM.setMatrix4(TRSTransformation(
//                            translation = cursor.center,
//                            rotation = Vector3.X_AXIS rotationTo part.translationAxis,
//                            scale = scale
//                    ).matrix)
//                    globalColor.setVector3(part.translationAxis)
//                    accept(vao)
//                    globalColor.setVector3(Vector3.ONE)
//                }
//            }
//        }
//        renderDebugHitbox(ctx)
    }

    fun renderDebugHitbox(ctx: RenderContext) {
        val vao = cursorCache.getOrCreate(ctx) {
            val cursor = ctx.gui.selector.cursor
            ctx.buffer.build(GL11.GL_LINES, false) {
                cursor.getSelectableParts(ctx.gui, ctx.camera, ctx.viewport).forEach { part ->
                    if (part is CursorPartTranslate) {
                        val (start, end) = part.calculateHitbox()
                        val aabb = AABB(start, end)
                        RenderUtil.appendAABB(this, aabb, part.translationAxis)
                    }
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
    }
}