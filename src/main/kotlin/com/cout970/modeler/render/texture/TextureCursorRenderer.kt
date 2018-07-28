package com.cout970.modeler.render.texture

import com.cout970.glutilities.structure.GLStateMachine
import com.cout970.glutilities.tessellator.DrawMode
import com.cout970.modeler.core.model.TRSTransformation
import com.cout970.modeler.core.model.mesh.MeshFactory
import com.cout970.modeler.gui.canvas.tool.Cursor2DTransformHelper
import com.cout970.modeler.gui.canvas.tool.CursorMode
import com.cout970.modeler.render.tool.AutoCache
import com.cout970.modeler.render.tool.RenderContext
import com.cout970.modeler.render.tool.createVao
import com.cout970.modeler.render.tool.useBlend
import com.cout970.vector.extensions.*
import org.lwjgl.opengl.GL11

/**
 * Created by cout970 on 2017/06/15.
 */
class TextureCursorRenderer {

    var clickBox = AutoCache()
    var rotationCenter = AutoCache()

    fun renderCursor(ctx: RenderContext) {
        when (ctx.gui.state.cursor.mode) {
            CursorMode.SCALE -> renderScaleHelper(ctx)
            CursorMode.ROTATION -> renderRotationCenter(ctx)
            else -> Unit
        }
    }

    private fun renderRotationCenter(ctx: RenderContext) {
        val sel = ctx.gui.programState.textureSelectionHandler.getSelection().getOrNull() ?: return
        val model = ctx.gui.state.tmpModel ?: ctx.gui.state.model
        val materialRef = ctx.gui.state.selectedMaterial
        val center = Cursor2DTransformHelper.getTextureSelectionCenter(model, sel, materialRef) ?: return
        val scale = ctx.camera.zoom.toFloat() / 32f

        val rotationCenter = rotationCenter.getOrCreate(ctx) {
            MeshFactory.createPlaneZ(Vector2.ONE).createVao(ctx.buffer, vec3Of(1, 1, 1))
        }

        GLStateMachine.useBlend(0.5f) {
            ctx.shader.apply {
                useColor.setInt(1)
                useLight.setInt(0)
                useTexture.setInt(0)
                useGlobalColor.setBoolean(true)

                globalColor.setVector3(Vector3.ONE)

                run {
                    val size = vec2Of(1 * scale)
                    val pos = center - size / 2
                    matrixM.setMatrix4(TRSTransformation(
                            translation = vec3Of(pos.xd, pos.yd, 0),
                            scale = vec3Of(size.xd, size.yd, 1)
                    ).matrix)
                    accept(rotationCenter)

                    matrixM.setMatrix4(TRSTransformation(
                            translation = vec3Of(pos.xd, pos.yd, 0),
                            scale = vec3Of(size.xd, size.yd, 1)
                    ).matrix)
                    accept(rotationCenter)
                }

                globalColor.setVector3(Vector3.ZERO)

                run {
                    val size = vec2Of(1.2 * scale)
                    val pos = center - size / 2
                    matrixM.setMatrix4(TRSTransformation(
                            translation = vec3Of(pos.xd, pos.yd, 0),
                            scale = vec3Of(size.xd, size.yd, 1)
                    ).matrix)
                    accept(rotationCenter)
                }

                GL11.glLineWidth(1f)
                useGlobalColor.setBoolean(false)
            }
        }
    }

    private fun renderScaleHelper(ctx: RenderContext) {
        val sel = ctx.gui.programState.textureSelectionHandler.getSelection().getOrNull() ?: return
        val model = ctx.gui.state.tmpModel ?: ctx.gui.state.model
        val materialRef = ctx.gui.state.selectedMaterial
        val boxes = Cursor2DTransformHelper.getBoxes(model, sel, materialRef, ctx.camera)
        val hover = ctx.gui.state.cursor.scaleBoxIndex

        val clickBox = clickBox.getOrCreate(ctx) {
            ctx.buffer.build(DrawMode.LINE_LOOP) {
                add(vec3Of(0, 0, 0), Vector2.ORIGIN, Vector3.Z_AXIS, vec3Of(1, 1, 1))
                add(vec3Of(0, 1, 0), Vector2.ORIGIN, Vector3.Z_AXIS, vec3Of(1, 1, 1))
                add(vec3Of(1, 1, 0), Vector2.ORIGIN, Vector3.Z_AXIS, vec3Of(1, 1, 1))
                add(vec3Of(1, 0, 0), Vector2.ORIGIN, Vector3.Z_AXIS, vec3Of(1, 1, 1))
            }
        }

        ctx.shader.apply {
            useColor.setInt(1)
            useLight.setInt(0)
            useTexture.setInt(0)
            useGlobalColor.setBoolean(true)


            globalColor.setVector3(Vector3.ONE)

            boxes.forEachIndexed { index, (pos, size) ->
                if (hover == index) GL11.glLineWidth(4f) else GL11.glLineWidth(2f)

                matrixM.setMatrix4(TRSTransformation(
                        translation = vec3Of(pos.xd, pos.yd, 0),
                        scale = vec3Of(size.xd, size.yd, 1)
                ).matrix)
                accept(clickBox)
            }


            globalColor.setVector3(Vector3.ZERO)

            boxes.forEachIndexed { index, (pos, size) ->
                if (hover == index) GL11.glLineWidth(6f) else GL11.glLineWidth(4f)

                matrixM.setMatrix4(TRSTransformation(
                        translation = vec3Of(pos.xd, pos.yd, 0),
                        scale = vec3Of(size.xd, size.yd, 1)
                ).matrix)
                accept(clickBox)
            }

            GL11.glLineWidth(1f)
            useGlobalColor.setBoolean(false)
        }
    }
}