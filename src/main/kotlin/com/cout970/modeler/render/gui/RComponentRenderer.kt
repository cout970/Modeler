package com.cout970.modeler.render.gui

import com.cout970.modeler.gui.reactive.RComponentWrapper
import org.liquidengine.legui.system.context.Context
import org.liquidengine.legui.system.renderer.nvg.component.NvgDefaultComponentRenderer

object RComponentRenderer : NvgDefaultComponentRenderer<RComponentWrapper<*, *, *>>() {

    override fun renderSelf(component: RComponentWrapper<*, *, *>?, context: Context?, nanovg: Long) {}

    override fun renderBorder(component: RComponentWrapper<*, *, *>?, context: Context?, nanovg: Long) {}
}