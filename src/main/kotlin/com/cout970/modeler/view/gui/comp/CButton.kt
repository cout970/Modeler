package com.cout970.modeler.view.gui.comp

import com.cout970.modeler.config.Config
import com.cout970.modeler.util.toColor
import org.liquidengine.legui.component.Button

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
}