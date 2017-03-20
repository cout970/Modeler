package com.cout970.modeler.view.scene.render.comp

import com.cout970.modeler.model.api.IElementLeaf
import com.cout970.modeler.model.util.getLeafElements
import com.cout970.modeler.model.util.toAABB
import com.cout970.modeler.util.RenderUtil
import com.cout970.modeler.view.scene.render.RenderContext
import org.lwjgl.opengl.GL11

/**
 * Created by cout970 on 2017/03/20.
 */
class AABBRenderComponent : IRenderableComponent {

    override fun render(ctx: RenderContext) {
        ctx.apply {
            if (sceneController.showBoundingBoxes.get()) {
                draw(GL11.GL_LINES, shaderHandler.formatPC) {
                    model.getLeafElements().map(IElementLeaf::toAABB).forEach {
                        RenderUtil.renderBox(this, it)
                    }
                }
            }
        }
    }
}