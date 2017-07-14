package com.cout970.modeler.view.gui.comp

import com.cout970.modeler.core.config.Config
import com.cout970.modeler.util.toColor
import org.liquidengine.legui.color.ColorConstants
import org.liquidengine.legui.component.ToggleButton
import org.liquidengine.legui.icon.ImageIcon

/**
 * Created by cout970 on 2017/01/24.
 */
class CToggleButton(posX: Number, posY: Number, sizeX: Number, sizeY: Number)
    : ToggleButton(posX.toFloat(), posY.toFloat(), sizeX.toFloat(), sizeY.toFloat()) {

    init {
        backgroundColor = Config.colorPalette.buttonColor.toColor()
        toggledBackgroundColor = Config.colorPalette.selectedButton.toColor()
    }

    fun setImage(img: ImageIcon) {
        backgroundIcon = img
        togglededBackgroundIcon = img
        focusedBackgroundIcon = img
        hoveredBackgroundIcon = img
        pressedBackgroundIcon = img
        backgroundColor = ColorConstants.transparent()
        border.isEnabled = false
    }
}