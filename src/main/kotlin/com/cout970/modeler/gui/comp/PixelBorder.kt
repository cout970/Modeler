package com.cout970.modeler.gui.comp

import com.cout970.modeler.core.config.Config
import com.cout970.modeler.util.toColor
import org.joml.Vector2f
import org.joml.Vector4f
import org.liquidengine.legui.border.Border
import org.liquidengine.legui.component.Component
import org.liquidengine.legui.system.context.Context
import org.liquidengine.legui.system.renderer.nvg.NvgBorderRenderer
import org.liquidengine.legui.system.renderer.nvg.util.NvgRenderUtils

/**
 * Created by cout970 on 2017/08/29.
 */
class PixelBorder : Border() {

    var color: Vector4f = Config.colorPalette.blackColor.toColor()

    var enableTop = false
    var enableBottom = false
    var enableLeft = false
    var enableRight = false

    object PixelBorderRenderer : NvgBorderRenderer<PixelBorder>() {

        override fun renderBorder(border: PixelBorder, comp: Component, context: Context, nanovg: Long) {

            if (border.enableTop) {
                NvgRenderUtils.drawRectangle(nanovg, border.color, comp.screenPosition, Vector2f(comp.size.x, 1f))
            }
            if (border.enableBottom) {
                NvgRenderUtils.drawRectangle(nanovg, border.color,
                        Vector2f(comp.screenPosition.x, comp.screenPosition.y + comp.size.y - 1),
                        Vector2f(comp.size.x, 1f))
            }
        }
    }
}