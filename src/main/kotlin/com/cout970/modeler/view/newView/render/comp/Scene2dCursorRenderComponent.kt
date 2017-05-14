package com.cout970.modeler.view.newView.render.comp

import com.cout970.modeler.to_redo.selection.VertexTexSelection
import com.cout970.modeler.to_redo.selection.vertexTexSelection
import com.cout970.modeler.view.newView.render.RenderContext
import com.cout970.modeler.view.newView.render.comp.CursorRenderer.drawCursor

/**
 * Created by cout970 on 2017/04/03.
 */
internal class Scene2dCursorRenderComponent : IRenderableComponent {

    override fun render(ctx: RenderContext) {
        ctx.apply {
            val selection = selectionManager.vertexTexSelection
            if (selection != VertexTexSelection.EMPTY) {
                drawCursor(scene.viewTarget.cursor, ctx.scene.cameraHandler.camera, true)
            }
        }
    }
}