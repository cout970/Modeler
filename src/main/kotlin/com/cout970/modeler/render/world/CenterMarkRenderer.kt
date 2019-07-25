package com.cout970.modeler.render.world

import com.cout970.glutilities.structure.GLStateMachine
import com.cout970.glutilities.tessellator.DrawMode
import com.cout970.modeler.core.config.Config
import com.cout970.modeler.core.model.TRSTransformation
import com.cout970.modeler.render.tool.AutoCache
import com.cout970.modeler.render.tool.RenderContext
import com.cout970.modeler.util.MatrixUtils
import com.cout970.vector.extensions.Vector3
import com.cout970.vector.extensions.vec2Of
import com.cout970.vector.extensions.vec3Of

/**
 * Created by cout970 on 2017/07/21.
 */
class CenterMarkRenderer {

    var centerMark = AutoCache()

    fun renderCursor(ctx: RenderContext) {
        if (Config.keyBindings.moveCamera.check(ctx.gui.input) ||
            Config.keyBindings.rotateCamera.check(ctx.gui.input)) {

            val vao = centerMark.getOrCreate(ctx) {
                ctx.buffer.build(DrawMode.TRIANGLES) {
                    add(vec3Of(-0.5, 0.5, -1.0), vec2Of(0, 0), Vector3.Z_AXIS, Vector3.ONE)
                    add(vec3Of(0.5, 0.5, -1.0), vec2Of(1, 0), Vector3.Z_AXIS, Vector3.ONE)
                    add(vec3Of(0.5, -0.5, -1.0), vec2Of(1, 1), Vector3.Z_AXIS, Vector3.ONE)

                    add(vec3Of(-0.5, 0.5, -1.0), vec2Of(0, 0), Vector3.Z_AXIS, Vector3.ONE)
                    add(vec3Of(0.5, -0.5, -1.0), vec2Of(1, 1), Vector3.Z_AXIS, Vector3.ONE)
                    add(vec3Of(-0.5, -0.5, -1.0), vec2Of(0, 1), Vector3.Z_AXIS, Vector3.ONE)
                }
            }

            ctx.shader.apply {
                useColor.setInt(0)
                useLight.setInt(0)
                useTexture.setInt(1)

                val transform = TRSTransformation(scale = vec3Of(60.0 / ctx.viewport.xd, 60.0 / ctx.viewport.yd, 1.0))
                matrixM.setMatrix4(transform.matrix)
                matrixVP.setMatrix4(MatrixUtils.createOrthoMatrix(vec2Of(1, 1)))

                ctx.gui.resources.centerMarkTexture.bind()
                GLStateMachine.blend.enable()
                accept(vao)
                GLStateMachine.blend.disable()
            }
        }
    }
}