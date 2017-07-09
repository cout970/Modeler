package com.cout970.modeler.view.render.world

import com.cout970.glutilities.tessellator.VAO
import com.cout970.matrix.extensions.Matrix4
import com.cout970.modeler.controller.World
import com.cout970.modeler.controller.selector.Cursor
import com.cout970.modeler.controller.selector.ITranslatable
import com.cout970.modeler.core.model.AABB
import com.cout970.modeler.core.model.TRSTransformation
import com.cout970.modeler.util.RenderUtil
import com.cout970.modeler.util.rotationTo
import com.cout970.modeler.view.render.tool.RenderContext
import com.cout970.modeler.view.render.tool.createVao
import com.cout970.vector.extensions.*
import org.lwjgl.opengl.GL11

/**
 * Created by cout970 on 2017/06/15.
 */
class CursorRenderer {

    var translationArrowVao: VAO? = null

    fun renderCursor(ctx: RenderContext, world: World) {

        if (translationArrowVao == null) {
            translationArrowVao = ctx.resources.translationArrow.createVao(ctx.buffer, vec3Of(1, 1, 1))
        }

        if (ctx.gui.selectionHandler.getSelection() == null) {
            return
        }
        val cursor = world.cursor
        val parameters = cursor.getCursorParameters(ctx.camera, ctx.viewport)

        translationArrowVao?.let {
            ctx.shader.apply {
                useColor.setInt(1)
                useLight.setInt(0)
                useTexture.setInt(0)
                val hovered = ctx.gui.state.hoveredObject
                val hold = ctx.gui.state.holdingSelection
                cursor.getSelectableParts(ctx.gui, ctx.camera, ctx.viewport).forEach { part ->
                    val selected = hovered == part || hold == part
                    if (part is ITranslatable) {

                        val scale = vec3Of(parameters.length / 16f) + if (selected)
                            vec3Of(0, 1, 1) * parameters.length / 2 * 1 / 16f
                        else
                            vec3Of(0f)

                        matrixM.setMatrix4(TRSTransformation(
                                translation = cursor.center,
                                rotation = Vector3.X_AXIS rotationTo part.translationAxis,
                                scale = scale
                        ).matrix)
                        globalColor.setVector3(part.translationAxis)
                        accept(it)
                        globalColor.setVector3(Vector3.ONE)
                    }
                }
            }
        }
    }

    fun renderDebugHitbox(ctx: RenderContext, world: World) {
        val cursor = world.cursor

        val vao = ctx.buffer.build(GL11.GL_LINES, false) {
            cursor.getSelectableParts(ctx.gui, ctx.camera, ctx.viewport).forEach { part ->
                if (part is Cursor.CursorPartTranslate) {
                    val (start, end) = part.calculateHitbox()
                    val aabb = AABB(start, end)
                    RenderUtil.appendAABB(this, aabb, part.translationAxis)
                }
            }
        }
        vao.let {
            ctx.shader.apply {
                useColor.setInt(1)
                useLight.setInt(0)
                useTexture.setInt(0)
                matrixM.setMatrix4(Matrix4.IDENTITY)
                accept(it)
            }
        }
        vao.close()
    }
}