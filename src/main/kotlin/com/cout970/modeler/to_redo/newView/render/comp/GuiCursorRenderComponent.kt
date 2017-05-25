package com.cout970.modeler.to_redo.newView.render.comp

import com.cout970.glutilities.structure.GLStateMachine
import com.cout970.modeler.core.config.Config
import com.cout970.modeler.view.render.RenderContextOld
import com.cout970.vector.extensions.vec2Of
import com.cout970.vector.extensions.xd
import com.cout970.vector.extensions.yd
import org.lwjgl.opengl.GL11

/**
 * Created by cout970 on 2017/03/20.
 */
class GuiCursorRenderComponent : IRenderableComponent {

    override fun render(ctx: RenderContextOld) {
        ctx.apply {
            if (Config.keyBindings.moveCamera.check(input) ||
                Config.keyBindings.rotateCamera.check(input)) {

                val size = vec2Of(100)
                GLStateMachine.depthTest.disable()
                GLStateMachine.blend.enable()
                shaderHandler.cursorTexture.bind()
                tessellator.draw(GL11.GL_QUADS, shaderHandler.formatPT, shaderHandler.consumer) {
                    set(0, -size.xd / 2, -size.yd / 2, 0.0).set(1, 0, 0).endVertex()
                    set(0, -size.xd / 2, +size.yd / 2, 0.0).set(1, 1, 0).endVertex()
                    set(0, +size.xd / 2, +size.yd / 2, 0.0).set(1, 1, 1).endVertex()
                    set(0, +size.xd / 2, -size.yd / 2, 0.0).set(1, 0, 1).endVertex()
                }
                GLStateMachine.blend.disable()
                GLStateMachine.depthTest.enable()
            }
        }
    }
}