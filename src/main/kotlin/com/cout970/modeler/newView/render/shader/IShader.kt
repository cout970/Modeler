package com.cout970.modeler.newView.render.shader

import com.cout970.modeler.newView.render.RenderContext

/**
 * Created by cout970 on 2017/04/10.
 */
interface IShader {

    fun useShader(ctx: RenderContext, func: () -> Unit)
}