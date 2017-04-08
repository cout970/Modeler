package com.cout970.modeler.newView.gui.comp

import com.cout970.modeler.config.Config
import com.cout970.modeler.util.toColor
import org.liquidengine.legui.component.Component
import org.liquidengine.legui.component.border.Border
import org.liquidengine.legui.context.LeguiContext
import org.liquidengine.legui.render.nvg.border.NvgSimpleLineBorderRenderer
import org.liquidengine.legui.util.NvgRenderUtils
import org.liquidengine.legui.util.Util

/**
 * Created by cout970 on 2017/01/24.
 */
object CBorderRenderer : NvgSimpleLineBorderRenderer() {

    override fun render(border: Border, context: LeguiContext, component: Component, nvgContext: Long) {
        if (border.isEnabled) {

            val pos = Util.calculatePosition(component)
            val size = component.size

            val borderColor = Config.colorPalette.borderColor.toColor()

            NvgRenderUtils.drawRectStroke(nvgContext, pos, size, borderColor, 0f, 1f)
        }
    }
}