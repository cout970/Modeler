package com.cout970.modeler.to_redo.newView.render.shader

import com.cout970.modeler.view.render.RenderContextOld

/**
 * Created by cout970 on 2017/04/10.
 */
interface IShader {

    fun useShader(ctx: RenderContextOld, func: () -> Unit)
}