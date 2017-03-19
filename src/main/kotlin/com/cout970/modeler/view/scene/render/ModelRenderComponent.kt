package com.cout970.modeler.view.scene.render

import com.cout970.glutilities.tessellator.VAO
import com.cout970.modeler.model.material.MaterialNone
import com.cout970.modeler.util.Cache
import org.lwjgl.opengl.GL11

/**
 * Created by cout970 on 2017/03/19.
 */
class ModelRenderComponent : IRenderableComponent {

    val vaoCache = Cache<Int, VAO>(2).apply { onRemove = { _, v -> v.close() } }

    override fun render(ctx: RenderContext) {
        MaterialNone.bind()
        ctx.apply {
            renderCache(vaoCache, model.hashCode()) {
                tessellator.compile(GL11.GL_QUADS, shaderHandler.formatPTN) {

                    model.getQuads().forEach { quad ->
                        quad.tessellate(this)
                    }
                }
            }
        }
    }
}