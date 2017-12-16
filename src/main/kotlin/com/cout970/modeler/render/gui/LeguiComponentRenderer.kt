package com.cout970.modeler.render.gui

import org.liquidengine.legui.component.Component
import org.liquidengine.legui.system.context.Context
import org.liquidengine.legui.system.renderer.nvg.component.NvgDefaultComponentRenderer

object LeguiComponentRenderer : NvgDefaultComponentRenderer<Component>() {

    override fun renderSelf(component: Component, ctx: Context, nanovg: Long) {
        if (!component.isVisible ||
            component.backgroundColor.w < 0.01 ||
            component.size.lengthSquared() < 0.0001) return

        super.renderSelf(component, ctx, nanovg)
    }

    override fun renderBorder(component: Component, context: Context, nanovg: Long) {
        if (!component.isVisible ||
            component.border == null ||
            !component.border.isEnabled ||
            component.size.lengthSquared() < 0.0001) return

        super.renderBorder(component, context, nanovg)
    }
}