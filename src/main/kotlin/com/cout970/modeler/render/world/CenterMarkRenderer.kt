package com.cout970.modeler.render.world

import com.cout970.glutilities.structure.GLStateMachine
import com.cout970.glutilities.tessellator.DrawMode
import com.cout970.matrix.extensions.Matrix4
import com.cout970.modeler.core.config.Config
import com.cout970.modeler.render.tool.AutoCache
import com.cout970.modeler.render.tool.RenderContext
import com.cout970.modeler.util.toIMatrix
import com.cout970.vector.extensions.Vector3
import com.cout970.vector.extensions.div
import com.cout970.vector.extensions.vec2Of
import com.cout970.vector.extensions.vec3Of
import org.joml.Matrix4d

/**
 * Created by cout970 on 2017/07/21.
 */
class CenterMarkRenderer {

    var centerMark = AutoCache()

    fun renderCursor(ctx: RenderContext) {
        if (Config.keyBindings.moveCamera.check(ctx.gui.input) ||
            Config.keyBindings.rotateCamera.check(ctx.gui.input)) {

            val vao = centerMark.getOrCreate(ctx) {
                ctx.buffer.build(DrawMode.QUADS) {
                    val size = vec2Of(60, 60) / 2
                    val z = 0.5
                    add(vec3Of(-size.xd, -size.yd, z), vec2Of(0, 0), Vector3.ORIGIN, Vector3.ORIGIN)
                    add(vec3Of(+size.xd, -size.yd, z), vec2Of(1, 0), Vector3.ORIGIN, Vector3.ORIGIN)
                    add(vec3Of(+size.xd, +size.yd, z), vec2Of(1, 1), Vector3.ORIGIN, Vector3.ORIGIN)
                    add(vec3Of(-size.xd, +size.yd, z), vec2Of(0, 1), Vector3.ORIGIN, Vector3.ORIGIN)
                }
            }

            ctx.shader.apply {
                useColor.setInt(0)
                useLight.setInt(0)
                useTexture.setInt(1)

                matrixM.setMatrix4(Matrix4.IDENTITY)
                matrixVP.setMatrix4(Matrix4d().apply {
                    scale(1 / ctx.viewport.xd, 1 / ctx.viewport.yd, 1.0)
                }.toIMatrix())


                ctx.gui.resources.centerMarkTexture.bind()
                GLStateMachine.blend.enable()
                GLStateMachine.depthTest.disable()
                GLStateMachine.cullFace.disable()
                accept(vao)
                GLStateMachine.cullFace.enable()
                GLStateMachine.depthTest.enable()
                GLStateMachine.blend.disable()
            }
        }
    }
}