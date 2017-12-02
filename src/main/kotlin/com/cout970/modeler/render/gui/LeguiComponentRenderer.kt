package com.cout970.modeler.render.gui

import org.liquidengine.legui.component.Component
import org.liquidengine.legui.system.context.Context
import org.liquidengine.legui.system.renderer.nvg.component.NvgDefaultComponentRenderer

object LeguiComponentRenderer : NvgDefaultComponentRenderer<Component>() {

    override fun renderSelf(component: Component, ctx: Context, nanovg: Long) {
        // Ignore
    }

    override fun renderBorder(component: Component?, context: Context?, nanovg: Long) {
        // Ignore
    }
}