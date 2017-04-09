package com.cout970.modeler.newView.render.comp

import com.cout970.modeler.model.material.MaterialNone
import com.cout970.modeler.model.util.getElement
import com.cout970.modeler.model.util.getLeafPaths
import com.cout970.modeler.newView.render.RenderContext
import org.lwjgl.opengl.GL11

/**
 * Created by cout970 on 2017/03/19.
 */
class ModelRenderComponent : IRenderableComponent {

    override fun render(ctx: RenderContext) {
        MaterialNone.bind()
        ctx.apply {

            model.getLeafPaths()
                    .groupBy { model.resources.getMaterial(it) }
                    .forEach { path ->
                        path.key.bind()

                        renderCache(renderer.modelCache, model.hashCode()) {
                            tessellator.compile(GL11.GL_QUADS, shaderHandler.formatPTN) {
                                path.value
                                        .flatMap { model.getElement(it).getQuads() }
                                        .forEach { quad ->
                                            quad.tessellate(this)
                                        }
                            }
                        }
                    }
        }
    }
}