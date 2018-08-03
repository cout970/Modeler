package com.cout970.modeler.gui.leguicomp

import com.cout970.modeler.core.config.Config
import com.cout970.modeler.util.toColor
import org.liquidengine.legui.component.Button

/**
 * Created by cout970 on 2017/09/21.
 */
class TextButton(
        val command: String = "",
        text: String = "",
        posX: Number = 0f,
        posY: Number = 0f,
        sizeX: Number = 32f,
        sizeY: Number = 32f
) : Button(text, posX.toFloat(), posY.toFloat(), sizeX.toFloat(), sizeY.toFloat()) {

    init {
        textState.textColor = Config.colorPalette.textColor.toColor()
        classes("text_button")
    }

    fun setTooltip(tooltip: String) {
        this.tooltip = InstantTooltip(tooltip)
    }

    override fun toString(): String = "TextButton(command='$command')"
}