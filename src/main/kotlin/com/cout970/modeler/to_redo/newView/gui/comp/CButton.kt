package com.cout970.modeler.to_redo.newView.gui.comp

import com.cout970.modeler.core.config.Config
import com.cout970.modeler.util.toColor
import org.liquidengine.legui.component.Button
import org.liquidengine.legui.component.ImageView

/**
 * Created by cout970 on 2017/01/24.
 */
class CButton(
        text: String,
        posX: Number,
        posY: Number,
        sizeX: Number,
        sizeY: Number
) : Button(text, posX.toFloat(), posY.toFloat(), sizeX.toFloat(), sizeY.toFloat()) {

    init {
        textState.textColor = Config.colorPalette.textColor.toColor()
        backgroundColor = Config.colorPalette.buttonColor.toColor()
    }

    fun setImage(img: ImageView) {
        backgroundImage = img
        focusedBbackgroundImage = img
        hoveredBackgroundImage = img
        pressedBackgroundImage = img
    }

    fun setTextLeft() = this.apply {
        textState.horizontalAlign = org.liquidengine.legui.component.optional.align.HorizontalAlign.LEFT
        textState.padding.x += 5f
    }
}