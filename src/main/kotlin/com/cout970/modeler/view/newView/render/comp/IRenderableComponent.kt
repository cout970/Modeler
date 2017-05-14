package com.cout970.modeler.view.newView.render.comp

import com.cout970.modeler.view.newView.render.RenderContext


/**
 * Created by cout970 on 2017/03/19.
 */
interface IRenderableComponent {

    fun render(ctx: RenderContext)

    fun canRender(ctx: RenderContext): Boolean = true
}