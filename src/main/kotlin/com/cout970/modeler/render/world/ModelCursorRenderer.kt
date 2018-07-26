package com.cout970.modeler.render.world

import com.cout970.glutilities.tessellator.VAO
import com.cout970.matrix.api.IMatrix4
import com.cout970.modeler.core.model.TRSTransformation
import com.cout970.modeler.gui.canvas.cursor.CursorParameters
import com.cout970.modeler.gui.canvas.tool.CursorMode
import com.cout970.modeler.render.tool.AutoCache
import com.cout970.modeler.render.tool.RenderContext
import com.cout970.modeler.render.tool.createVao
import com.cout970.modeler.util.rotationTo
import com.cout970.modeler.util.transform
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

    fun render(ctx: RenderContext) {
        val cursor = ctx.gui.state.cursor

        if (!cursor.visible) return

        val params = CursorParameters.create(ctx.camera.zoom, ctx.viewport)

        val base = when (cursor.mode) {
            CursorMode.TRANSLATION, CursorMode.SCALE -> Vector3.X_AXIS
            else -> Vector3.Y_AXIS
        }

        val rotation = listOf(
                base rotationTo cursor.rotation.transform(Vector3.X_AXIS),
                base rotationTo cursor.rotation.transform(Vector3.Y_AXIS),
                base rotationTo cursor.rotation.transform(Vector3.Z_AXIS),
                base rotationTo cursor.rotation.transform(Vector3.NEG_X_AXIS),
                base rotationTo cursor.rotation.transform(Vector3.NEG_Y_AXIS),
                base rotationTo cursor.rotation.transform(Vector3.NEG_Z_AXIS)
        )

        val model = when (cursor.mode) {
            CursorMode.TRANSLATION -> translationArrow.getOrCreate(ctx) {
                ctx.gui.resources.translationArrow.createVao(ctx.buffer, vec3Of(1, 1, 1))
            }
            CursorMode.ROTATION -> rotationRing.getOrCreate(ctx) {
                ctx.gui.resources.rotationRing.createVao(ctx.buffer, vec3Of(1, 1, 1))
            }
            CursorMode.SCALE -> scaleArrow.getOrCreate(ctx) {
                ctx.gui.resources.scaleArrow.createVao(ctx.buffer, vec3Of(1, 1, 1))
            }
        }

        val parts = cursor.getParts().mapIndexed { index, cursorPart ->
            CursorPart(model, if (cursorPart.hovered) Vector3.ONE else cursorPart.color,
                    TRSTransformation(
                            translation = cursor.position,
                            rotation = rotation[index],
                            scale = vec3Of(params.length / 16f)
                    ).matrix
            )
        }

        ctx.shader.apply {
            useColor.setInt(1)
            useLight.setInt(0)
            useTexture.setInt(0)

            useGlobalColor.setBoolean(true)
            parts.forEachIndexed { index, p ->
                matrixM.setMatrix4(p.transform)
                globalColor.setVector3(p.color)
                accept(p.model)

//                val part = cursor.getParts()[index]
//                val vao = part.calculateHitbox(cursor, ctx.camera, ctx.viewport).createVao(ctx.buffer, vec3Of(1, 1, 1))
//
//                matrixM.setMatrix4(Matrix4.IDENTITY)
//                accept(vao)
//                vao.close()
            }

            globalColor.setVector3(Vector3.ONE)
            useGlobalColor.setBoolean(false)
        }
    }

    data class CursorPart(val model: VAO, val color: IVector3, val transform: IMatrix4)

    fun renderCursor(ctx: RenderContext) {

        render(ctx)
    }
}