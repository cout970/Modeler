package com.cout970.modeler.newView.render.comp

import com.cout970.modeler.newView.render.RenderContext
import com.cout970.modeler.newView.render.comp.CursorRenderer.drawCursor
import com.cout970.modeler.selection.*

/**
 * Created by cout970 on 2017/03/20.
 */

class Scene3dCursorRenderComponent : IRenderableComponent {

    override fun render(ctx: RenderContext) {
        ctx.apply {
            if (selectionManager.selectionMode == SelectionMode.EDIT) {
                val selection = selectionManager.vertexPosSelection
                if (selection != VertexPosSelection.EMPTY) {
                    drawCursor(scene.viewTarget.cursor, true)
                }
            } else {
                val selection = selectionManager.elementSelection
                if (selection != ElementSelection.EMPTY) {
                    drawCursor(scene.viewTarget.cursor, true)
                }
            }
        }
    }
}