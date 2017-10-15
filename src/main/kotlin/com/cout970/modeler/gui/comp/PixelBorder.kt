package com.cout970.modeler.gui.comp

import com.cout970.modeler.core.config.Config
import com.cout970.modeler.util.toColor
import org.joml.Vector2f
import org.joml.Vector4f
import org.liquidengine.legui.border.Border
import org.liquidengine.legui.component.Component
import org.liquidengine.legui.system.context.Context
import org.liquidengine.legui.system.renderer.nvg.NvgBorderRenderer
import org.liquidengine.legui.system.renderer.nvg.util.NvgShapes

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
                NvgShapes.drawRect(nanovg, comp.absolutePosition, Vector2f(comp.size.x, 1f), border.color)
            }
            if (border.enableBottom) {
                NvgShapes.drawRect(nanovg,
                        Vector2f(comp.absolutePosition.x, comp.absolutePosition.y + comp.size.y - 1),
                        Vector2f(comp.size.x, 1f),
                        border.color)
            }
        }
    }
}