package com.cout970.modeler.gui.leguicomp

import com.cout970.modeler.core.config.Config
import com.cout970.modeler.util.setBorderless
import com.cout970.modeler.util.setTransparent
import com.cout970.modeler.util.toColor
import com.cout970.reactive.dsl.hide
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
        setBorderless()
        container.setTransparent()
        container.setBorderless()
        horizontalScrollBar.hide()
        verticalScrollBar.setTransparent()
        verticalScrollBar.setBorderless()
        verticalScrollBar.arrowColor = Config.colorPalette.darkColor.toColor()
        verticalScrollBar.scrollColor = Config.colorPalette.blackColor.toColor()
        verticalScrollBar.isArrowsEnabled = false
    }

    operator fun Component.unaryPlus() {
        container.add(this)
    }
}