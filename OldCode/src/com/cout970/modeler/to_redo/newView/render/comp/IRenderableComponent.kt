package com.cout970.modeler.to_redo.newView.render.comp

import com.cout970.modeler.view.render.RenderContextOld


/**
 * Created by cout970 on 2017/03/19.
 */
interface IRenderableComponent {

    fun render(ctx: RenderContextOld)

    fun canRender(ctx: RenderContextOld): Boolean = true
}