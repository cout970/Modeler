package com.cout970.modeler.gui.react.leguicomp

import com.cout970.modeler.core.config.Config
import com.cout970.modeler.gui.comp.setTransparent
import com.cout970.modeler.util.hide
import com.cout970.modeler.util.toColor
import org.joml.Vector2f
import org.liquidengine.legui.component.Component
import org.liquidengine.legui.component.ScrollablePanel

/**
 * Created by cout970 on 2017/09/16.
 */
class VerticalPanel(
        x: Float = 0f, y: Float = 0f, width: Float = 10f, height: Float = 10f
) : ScrollablePanel(Vector2f(x, y), Vector2f(width, height)) {

    init {
        setTransparent()
        container.setTransparent()
        horizontalScrollBar.hide()
        verticalScrollBar.setTransparent()
        verticalScrollBar.arrowColor = Config.colorPalette.darkColor.toColor()
        verticalScrollBar.scrollColor = Config.colorPalette.darkColor.toColor()
        verticalScrollBar.isArrowsEnabled = false
    }

    operator fun Component.unaryPlus() {
        container.add(this)
    }
}