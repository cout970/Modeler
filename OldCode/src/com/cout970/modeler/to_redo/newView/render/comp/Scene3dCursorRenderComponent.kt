package com.cout970.modeler.to_redo.newView.render.comp

import com.cout970.modeler.to_redo.newView.render.comp.CursorRenderer.drawCursor
import com.cout970.modeler.to_redo.selection.*
import com.cout970.modeler.view.render.RenderContextOld

/**
 * Created by cout970 on 2017/03/20.
 */

class Scene3dCursorRenderComponent : IRenderableComponent {

    override fun render(ctx: RenderContextOld) {
        ctx.apply {
            if (selectionManager.selectionMode == SelectionMode.EDIT) {
                val selection = selectionManager.vertexPosSelection
                if (selection != VertexPosSelection.EMPTY) {
                    drawCursor(scene.viewTarget.cursor, ctx.scene.cameraHandler.camera, true)
                }
            } else {
                val selection = selectionManager.elementSelection
                if (selection != ElementSelection.EMPTY) {
                    drawCursor(scene.viewTarget.cursor, ctx.scene.cameraHandler.camera, true)
                }
            }
        }
    }
}