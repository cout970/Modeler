package com.cout970.modeler.view.newView.render.comp

import com.cout970.modeler.to_redo.model.material.MaterialNone
import com.cout970.modeler.to_redo.model.util.getElement
import com.cout970.modeler.to_redo.model.util.getLeafPaths
import com.cout970.modeler.view.newView.render.RenderContext
import org.lwjgl.opengl.GL11

/**
 * Created by cout970 on 2017/03/19.
 */
class ModelRenderComponent : IRenderableComponent {

    override fun render(ctx: RenderContext) {
        MaterialNone.bind()
        ctx.apply {

            val group = model.getLeafPaths().groupBy { model.resources.getMaterial(it) }
            for ((material, elements) in group) {
                material.bind()

                renderCache(renderer.modelCache, model.hashCode()) {
                    tessellator.compile(GL11.GL_QUADS, shaderHandler.formatPTN) {
                        val quads = elements.flatMap { model.getElement(it).getQuads() }
                        quads.forEach { quad ->
                            quad.tessellate(this)
                        }
                    }
                }
            }
        }
    }
}