package com.cout970.modeler.view.scene.render.comp

import com.cout970.modeler.model.material.MaterialNone
import com.cout970.modeler.view.scene.render.RenderContext
import org.lwjgl.opengl.GL11

/**
 * Created by cout970 on 2017/03/19.
 */
class ModelRenderComponent : IRenderableComponent {

    override fun render(ctx: RenderContext) {
        MaterialNone.bind()
        ctx.apply {
            renderCache(sceneController.modelCache, model.hashCode()) {
                tessellator.compile(GL11.GL_QUADS, shaderHandler.formatPTN) {

                    model.getQuads().forEach { quad ->
                        quad.tessellate(this)
                    }
                }
            }
        }
    }
}