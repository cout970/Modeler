package com.cout970.modeler.render.gui

import org.liquidengine.legui.component.Component
import org.liquidengine.legui.style.Border
import org.liquidengine.legui.system.context.Context
import org.liquidengine.legui.system.renderer.nvg.NvgBorderRenderer

object BorderRenderer : NvgBorderRenderer<Border>() {

    override fun renderBorder(border: Border, component: Component, context: Context, nanovg: Long) {
        // Ignore
    }
}