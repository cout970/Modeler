package com.cout970.modeler.gui.react.leguicomp

import com.cout970.modeler.core.config.Config
import com.cout970.modeler.util.toColor
import org.liquidengine.legui.component.SelectBox

/**
 * Created by cout970 on 2017/09/30.
 */
class DropDown(val cmd: String, val x: Float = 0f, val y: Float = 0f, val width: Float = 10f,
               val height: Float = 10f) : SelectBox(x, y, width, height) {

    val selectedIndex get() = elements.indexOf(selection)

    init {
        selectionListPanel.verticalScrollBar.apply {
            backgroundColor = Config.colorPalette.brightestColor.toColor()
            scrollColor = Config.colorPalette.greyColor.toColor()
            arrowColor = Config.colorPalette.whiteColor.toColor()
        }
    }
}