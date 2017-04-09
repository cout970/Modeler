package com.cout970.modeler.newView.render.comp

import com.cout970.glutilities.structure.GLStateMachine
import com.cout970.modeler.model.material.MaterialNone
import com.cout970.modeler.newView.render.RenderContext
import com.cout970.vector.extensions.div
import com.cout970.vector.extensions.xi
import com.cout970.vector.extensions.yi
import org.lwjgl.opengl.GL11

/**
 * Created by cout970 on 2017/03/21.
 */

class TextureRenderComponent : IRenderableComponent {

    override fun render(ctx: RenderContext) {
        ctx.apply {
            GLStateMachine.depthTest.disable()
            shaderHandler.enableColor = false

            val texture = model.resources.materials.firstOrNull() ?: MaterialNone
            val size = texture.size
            val offset = size / 2

            texture.bind()
            draw(GL11.GL_QUADS, shaderHandler.formatPCT) {
                set(0, -offset.xi, -offset.yi, 0)
                        .set(1, 1, 1, 1)
                        .set(2, 0.0, 1.0).endVertex()
                set(0, -offset.xi + size.xi, -offset.yi, 0)
                        .set(1, 1, 1, 1)
                        .set(2, 1.0, 1.0).endVertex()
                set(0, -offset.xi + size.xi, size.yi - offset.yi, 0)
                        .set(1, 1, 1, 1)
                        .set(2, 1.0, 0.0).endVertex()
                set(0, -offset.xi, size.yi - offset.yi, 0)
                        .set(1, 1, 1, 1)
                        .set(2, 0.0, 0.0).endVertex()
            }

            shaderHandler.enableColor = true
            GLStateMachine.depthTest.enable()
        }
    }
}