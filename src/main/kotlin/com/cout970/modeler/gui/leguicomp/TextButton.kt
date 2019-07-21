package com.cout970.modeler.gui.leguicomp

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
        classes("text_button")
    }

    fun setTooltip(tooltip: String) {
        this.tooltip = InstantTooltip(tooltip)
    }

    override fun toString(): String = "TextButton(command='$command')"
}