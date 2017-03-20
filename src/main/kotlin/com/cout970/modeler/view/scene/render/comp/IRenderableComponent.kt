package com.cout970.modeler.view.scene.render.comp

import com.cout970.modeler.view.scene.render.RenderContext


/**
 * Created by cout970 on 2017/03/19.
 */
interface IRenderableComponent {

    fun render(ctx: RenderContext)
}