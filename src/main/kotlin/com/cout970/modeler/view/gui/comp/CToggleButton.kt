package com.cout970.modeler.view.gui.comp

import com.cout970.modeler.config.Config
import com.cout970.modeler.util.toColor
import org.liquidengine.legui.component.ImageView
import org.liquidengine.legui.component.ToggleButton

/**
 * Created by cout970 on 2017/01/24.
 */
class CToggleButton(posX: Number, posY: Number, sizeX: Number, sizeY: Number)
    : ToggleButton(posX.toFloat(), posY.toFloat(), sizeX.toFloat(), sizeY.toFloat()) {

    init {
        backgroundColor = Config.colorPalette.buttonColor.toColor()
        toggledBackgroundColor = Config.colorPalette.selectedButton.toColor()
    }

    fun setImage(img: ImageView) {
        backgroundImage = img
        togglededBackgroundImage = img
        focusedBbackgroundImage = img
        hoveredBackgroundImage = img
        pressedBackgroundImage = img
    }
}