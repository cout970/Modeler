package com.cout970.modeler.view.newView.render.shader

import com.cout970.modeler.view.newView.render.RenderContext

/**
 * Created by cout970 on 2017/04/10.
 */
interface IShader {

    fun useShader(ctx: RenderContext, func: () -> Unit)
}