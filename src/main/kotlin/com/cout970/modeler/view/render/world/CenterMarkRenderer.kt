package com.cout970.modeler.view.render.world

import com.cout970.glutilities.structure.GLStateMachine
import com.cout970.glutilities.tessellator.VAO
import com.cout970.modeler.core.config.Config
import com.cout970.modeler.core.model.TRSTransformation
import com.cout970.modeler.util.toIMatrix
import com.cout970.modeler.view.render.tool.RenderContext
import com.cout970.vector.extensions.*
import org.joml.Matrix4d
import org.lwjgl.opengl.GL11

/**
 * Created by cout970 on 2017/07/21.
 */
class CenterMarkRenderer {

    var centerMark: VAO? = null

    fun renderCursor(ctx: RenderContext) {
        if (centerMark == null) {
            centerMark = ctx.buffer.build(GL11.GL_QUADS) {
                val size = vec2Of(60, 60) / 2
                val z = 0.5
                add(vec3Of(-size.xd, -size.yd, z), vec2Of(0, 0), Vector3.ORIGIN, Vector3.ORIGIN)
                add(vec3Of(+size.xd, -size.yd, z), vec2Of(1, 0), Vector3.ORIGIN, Vector3.ORIGIN)
                add(vec3Of(+size.xd, +size.yd, z), vec2Of(1, 1), Vector3.ORIGIN, Vector3.ORIGIN)
                add(vec3Of(-size.xd, +size.yd, z), vec2Of(0, 1), Vector3.ORIGIN, Vector3.ORIGIN)
            }
        }
        if (Config.keyBindings.moveCamera.check(ctx.gui.input) ||
            Config.keyBindings.rotateCamera.check(ctx.gui.input)) {

            centerMark?.let {
                ctx.shader.apply {
                    useColor.setInt(0)
                    useLight.setInt(0)
                    useTexture.setInt(1)

                    matrixVP.setMatrix4(Matrix4d().apply {
                        scale(1 / ctx.viewport.xd, 1 / ctx.viewport.yd, 1.0)
                    }.toIMatrix())

                    matrixM.setMatrix4(TRSTransformation(
                            translation = Vector3.ORIGIN,
                            rotation = Quaternion.IDENTITY,
                            scale = Vector3.ONE
                    ).matrix)
                    ctx.gui.resources.centerMarkTexture.bind()
                    GLStateMachine.blend.enable()
                    accept(it)
                    GLStateMachine.blend.disable()
                }
            }
        }
    }
}