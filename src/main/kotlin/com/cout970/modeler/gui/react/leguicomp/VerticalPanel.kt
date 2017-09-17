package com.cout970.modeler.gui.react.leguicomp

import com.cout970.modeler.core.config.Config
import com.cout970.modeler.gui.comp.setBorderless
import com.cout970.modeler.gui.comp.setTransparent
import com.cout970.modeler.util.hide
import com.cout970.modeler.util.toColor
import org.joml.Vector2f
import org.liquidengine.legui.border.SimpleLineBorder
import org.liquidengine.legui.component.Panel
import org.liquidengine.legui.component.ScrollablePanel

/**
 * Created by cout970 on 2017/09/16.
 */
class VerticalPanel(
        x: Float = 0f, y: Float = 0f, width: Float = 10f, height: Float = 10f
) : ScrollablePanel<Panel<*>>(Vector2f(x, y), Vector2f(width, height)) {

    init {
        border = SimpleLineBorder(Config.colorPalette.borderColor.toColor(), 0.5f)
        backgroundColor = Config.colorPalette.darkColor.toColor()
        setTransparent()
        setBorderless()

        container.backgroundColor = Config.colorPalette.lightBrightColor.toColor()
        container.setTransparent()

        horizontalScrollBar.hide()
        verticalScrollBar.setTransparent()
        verticalScrollBar.arrowColor = Config.colorPalette.darkColor.toColor()
        verticalScrollBar.isArrowsEnabled = false
        verticalScrollBar.scrollColor = Config.colorPalette.darkestColor.toColor()
        verticalScrollBar.cornerRadius = 0f
    }
}