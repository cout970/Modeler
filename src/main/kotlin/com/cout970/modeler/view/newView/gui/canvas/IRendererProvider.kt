package com.cout970.modeler.view.newView.gui.canvas

import com.cout970.modeler.view.newView.render.comp.IRenderableComponent

/**
 * Created by cout970 on 2017/05/02.
 */
interface IRendererProvider {

    fun getRenderers(): List<IRenderableComponent>
}