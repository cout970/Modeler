package com.cout970.modeler.view.render.comp

import com.cout970.modeler.selection.VertexTexSelection
import com.cout970.modeler.selection.vertexTexSelection
import com.cout970.modeler.view.controller.SelectionAxis
import com.cout970.modeler.view.render.RenderContext
import com.cout970.modeler.view.render.comp.CursorRenderer.drawCursor

/**
 * Created by cout970 on 2017/04/03.
 */
internal class Scene2dCursorRenderComponent : IRenderableComponent {

    override fun render(ctx: RenderContext) {
        ctx.apply {
            val axis = scene.selectorCache.selectedObject as? SelectionAxis ?: SelectionAxis.NONE
            val selection = selectionManager.vertexTexSelection
            if (selection != VertexTexSelection.EMPTY) {
                drawCursor(scene.cursor, axis, true)
            }
        }
    }
}