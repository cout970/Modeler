package com.cout970.modeler.to_redo.newView.render.comp

import com.cout970.glutilities.structure.GLStateMachine
import com.cout970.modeler.to_redo.model.material.MaterialNone
import com.cout970.modeler.view.render.RenderContextOld
import com.cout970.vector.extensions.div
import com.cout970.vector.extensions.xi
import com.cout970.vector.extensions.yi
import org.lwjgl.opengl.GL11

/**
 * Created by cout970 on 2017/03/21.
 */

class UVOutlineRenderComponent : IRenderableComponent {

    override fun render(ctx: RenderContextOld) {
        ctx.apply {
            GLStateMachine.depthTest.disable()
            shaderHandler.enableColor = true
            MaterialNone.bind()

            val texture = model.resources.materials.firstOrNull() ?: MaterialNone
            val size = texture.size
            val offset = size / 2

            draw(GL11.GL_LINES, shaderHandler.formatPCT) {
                for (x in 0..size.xi) {
                    set(0, -offset.xi + x * (size.xi / size.xi), -offset.yi, 0)
                            .set(1, 0.5, 0.5, 0.5)
                            .set(2, 0.0, 0.0).endVertex()
                    set(0, -offset.xi + x * (size.xi / size.xi), size.yi - offset.yi, 0)
                            .set(1, 0.5, 0.5, 0.5)
                            .set(2, 0.0, 0.0).endVertex()
                }

                for (z in 0..size.yi) {
                    set(0, -offset.xi, z * (size.yi / size.yi) - offset.yi, 0)
                            .set(1, 0.5, 0.5, 0.5)
                            .set(2, 0.0, 0.0).endVertex()
                    set(0, -offset.xi + size.xi, z * (size.yi / size.yi) - offset.yi, 0)
                            .set(1, 0.5, 0.5, 0.5)
                            .set(2, 0.0, 0.0).endVertex()
                }
            }

            shaderHandler.enableColor = false
            GLStateMachine.depthTest.enable()
        }
    }
}